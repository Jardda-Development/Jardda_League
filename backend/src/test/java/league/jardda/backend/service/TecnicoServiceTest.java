package league.jardda.backend.service;

import league.jardda.backend.DTO.PageResponse;
import league.jardda.backend.DTO.TecnicoDTO;
import league.jardda.backend.entity.Pais;
import league.jardda.backend.entity.Tecnico;
import league.jardda.backend.exception.NotFoundException;
import league.jardda.backend.repository.TecnicoRepository;
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
public class TecnicoServiceTest {
    @Mock
    TecnicoRepository tecnicoRepository;
    @Mock
    ParameterValidator parameterValidator;
    @InjectMocks
    TecnicoService tecnicoService;

    private final Pageable pageable = PageRequest.of(0, 10);

    private Tecnico tecnicoDeTeste() {
        Pais pais = Pais.builder().paisId(1L).nomePais("Espanha").sigla("ESP").build();

        return Tecnico.builder()
                .tecnicoId(1L)
                .nomeTecnico("Guardiola")
                .aniversarioTecnico(LocalDate.of(1971, 1, 18))
                .paisTecnico(pais)
                .build();
    }

    @Test
    void lancaNotFoundQuandoNaoHaTecnicos() {
        when(tecnicoRepository.filtrar(null, null, null, pageable)).thenReturn(Page.empty());

        assertThatThrownBy(() -> tecnicoService.listarTecnico(null, null, null, pageable))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Nenhum técnico");
    }

    @Test
    void devolveTecnicosConvertidosParaDto() {
        when(tecnicoRepository.filtrar(null, null, null, pageable))
                .thenReturn(new PageImpl<>(List.of(tecnicoDeTeste())));

        PageResponse<TecnicoDTO> resposta = tecnicoService.listarTecnico(null, null, null, pageable);

        assertThat(resposta.itens()).hasSize(1);
        assertThat(resposta.itens().getFirst().nome()).isEqualTo("Guardiola");
    }

    @Test
    void naoConsultaRepositoryQuandoPaisNaoExiste() {
        doThrow(new NotFoundException("País com id 99 não encontrado."))
                .when(parameterValidator).validarPais(99L);

        assertThatThrownBy(() -> tecnicoService.listarTecnico(null, null, 99L, pageable))
                .isInstanceOf(NotFoundException.class);

        verifyNoInteractions(tecnicoRepository);
    }

    @Test
    void buscarPorIdDevolveDtoQuandoTecnicoExiste() {
        when(tecnicoRepository.findById(1L)).thenReturn(Optional.of(tecnicoDeTeste()));

        TecnicoDTO dto = tecnicoService.buscarPorId(1L);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.nome()).isEqualTo("Guardiola");
    }

    @Test
    void buscarPorIdLancaNotFoundQuandoTecnicoNaoExiste() {
        when(tecnicoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tecnicoService.buscarPorId(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("99");
    }
}
