package league.jardda.backend.service;

import league.jardda.backend.DTO.ClubeDTO;
import league.jardda.backend.entity.Cidade;
import league.jardda.backend.entity.Clube;
import league.jardda.backend.entity.Pais;
import league.jardda.backend.exception.NotFoundException;
import league.jardda.backend.repository.ClubeRepository;
import league.jardda.backend.utils.ParameterValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClubeServiceTest {
    @Mock
    ClubeRepository clubeRepository;
    @Mock
    ParameterValidator parameterValidator;
    @InjectMocks
    ClubeService clubeService;

    private Clube clubeDeTeste() {
        Pais pais = Pais.builder().paisId(1L).nomePais("Espanha").sigla("ESP").build();
        Cidade cidade = Cidade.builder().cidadeId(1L).nomeCidade("Barcelona").pais(pais).build();

        return Clube.builder()
                .clubeId(10L)
                .nomeClube("FC Barcelona")
                .cidade(cidade)
                .build();
    }

    @Test
    void lancaNotFoundQuandoNaoHaClubes() {
        when(clubeRepository.filtrar(null, null, null, null)).thenReturn(List.of());

        assertThatThrownBy(() -> clubeService.listarClube(null, null, null, null))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Nenhum clube");
    }

    @Test
    void devolveClubesConvertidosParaDto() {
        when(clubeRepository.filtrar(null, null, null, null)).thenReturn(List.of(clubeDeTeste()));

        List<ClubeDTO> resultado = clubeService.listarClube(null, null, null, null);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.getFirst().nome()).isEqualTo("FC Barcelona");
        assertThat(resultado.getFirst().pais()).isEqualTo("Espanha");
    }

    @Test
    void naoConsultaRepositoryQuandoCidadeNaoExiste() {
        doThrow(new NotFoundException("Cidade com id 99 não encontrada."))
                .when(parameterValidator).validarCidade(99L);

        assertThatThrownBy(() -> clubeService.listarClube(null, null, 99L, null))
                .isInstanceOf(NotFoundException.class);

        verifyNoInteractions(clubeRepository);
    }

    @Test
    void naoConsultaRepositoryQuandoPaisNaoExiste() {
        doThrow(new NotFoundException("País com id 99 não encontrado."))
                .when(parameterValidator).validarPais(99L);

        assertThatThrownBy(() -> clubeService.listarClube(null, null, null, 99L))
                .isInstanceOf(NotFoundException.class);

        verifyNoInteractions(clubeRepository);
    }

    @Test
    void buscarPorIdDevolveDtoQuandoClubeExiste() {
        when(clubeRepository.findById(10L)).thenReturn(Optional.of(clubeDeTeste()));

        ClubeDTO dto = clubeService.buscarPorId(10L);

        assertThat(dto.id()).isEqualTo(10L);
        assertThat(dto.nome()).isEqualTo("FC Barcelona");
    }

    @Test
    void buscarPorIdLancaNotFoundQuandoClubeNaoExiste() {
        when(clubeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clubeService.buscarPorId(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("99");
    }
}