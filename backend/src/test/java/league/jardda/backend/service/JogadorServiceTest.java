package league.jardda.backend.service;

import league.jardda.backend.DTO.JogadorDTO;
import league.jardda.backend.DTO.PageResponse;
import league.jardda.backend.entity.Jogador;
import league.jardda.backend.entity.Pais;
import league.jardda.backend.exception.NotFoundException;
import league.jardda.backend.repository.JogadorRepository;
import league.jardda.backend.utils.ParameterValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JogadorServiceTest {
    @Mock
    JogadorRepository jogadorRepository;
    @Mock
    ParameterValidator parameterValidator;
    @InjectMocks
    JogadorService jogadorService;

    private final Pageable pageable = PageRequest.of(0, 10);

    private Jogador jogadorDeTeste() {
        Pais pais = Pais.builder()
                .paisId(1L)
                .nomePais("Brasil")
                .sigla("BRA")
                .build();

        return Jogador.builder()
                .jogadorId(1L)
                .nomeJogador("Ronaldinho Gaúcho")
                .paisJogador(pais)
                .aniversarioJogador(LocalDate.of(1980, 3, 21))
                .posicao("MEI")
                .lesionado(false)
                .build();
    }


    @Test
    void retornaDtoQuandoJogadorExiste() {
        when(jogadorRepository.findById(1L)).thenReturn(Optional.of(jogadorDeTeste()));

        JogadorDTO dto = jogadorService.buscarPorId(1L);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.nome()).isEqualTo("Ronaldinho Gaúcho");
        assertThat(dto.pais()).isEqualTo("Brasil");
        assertThat(dto.posicao()).isEqualTo("MEI");
        assertThat(dto.lesionado()).isFalse();
    }

    @Test
    void lancaNotFoundQuandoNaoHaJogadores() {
        when(jogadorRepository.filtrar(null, null, null, pageable))
                .thenReturn(Page.empty());

        assertThatThrownBy(() -> jogadorService.listarJogador(null, null, null, pageable))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Nenhum jogador");
    }

    @Test
    void devolveJogadoresConvertidosParaDto() {
        when(jogadorRepository.filtrar(null, null, null, pageable))
                .thenReturn(new PageImpl<>(List.of(jogadorDeTeste())));

        PageResponse<JogadorDTO> resposta =
                jogadorService.listarJogador(null, null, null, pageable);

        assertThat(resposta.itens()).hasSize(1);
        assertThat(resposta.itens().getFirst().nome()).isEqualTo("Ronaldinho Gaúcho");
    }

    @Test
    void naoConsultaRepositoryQuandoPaisNaoExiste() {
        doThrow(new NotFoundException("País com id 99 não encontrado."))
                .when(parameterValidator).validarPais(99L);

        assertThatThrownBy(() -> jogadorService.listarJogador(null, null, 99L, pageable))
                .isInstanceOf(NotFoundException.class);

        verifyNoInteractions(jogadorRepository);
    }

    @Test
    void buscarPorIdLancaNotFoundQuandoJogadorNaoExiste() {
        when(jogadorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> jogadorService.buscarPorId(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("99");
    }
}
