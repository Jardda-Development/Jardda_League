package league.jardda.backend.service;

import league.jardda.backend.DTO.LigaDTO;
import league.jardda.backend.entity.Liga;
import league.jardda.backend.entity.Pais;
import league.jardda.backend.exception.NotFoundException;
import league.jardda.backend.repository.LigaRepository;
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
public class LigaServiceTest {
    @Mock
    LigaRepository ligaRepository;
    @Mock
    ParameterValidator parameterValidator;
    @InjectMocks
    LigaService ligaService;

    private Liga ligaDeTeste(Pais pais) {
        return Liga.builder()
                .ligaId(1L)
                .nomeLiga("Brasileirão")
                .paisLiga(pais)
                .estiloLiga("pontos_corridos")
                .categoriaLiga("nacional")
                .build();
    }

    @Test
    void lancaNotFoundQuandoNaoHaLigas() {
        when(ligaRepository.filtrar(null, null, null)).thenReturn(List.of());

        assertThatThrownBy(() -> ligaService.listarLiga(null, null, null))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Nenhuma liga");
    }

    @Test
    void devolveLigasConvertidasParaDto() {
        Pais pais = Pais.builder().paisId(1L).nomePais("Brasil").sigla("BRA").build();
        when(ligaRepository.filtrar(null, null, null)).thenReturn(List.of(ligaDeTeste(pais)));

        List<LigaDTO> resultado = ligaService.listarLiga(null, null, null);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.getFirst().nome()).isEqualTo("Brasileirão");
        assertThat(resultado.getFirst().pais()).isEqualTo("Brasil");
    }

    @Test
    void devolveDtoComPaisNuloQuandoLigaNaoTemPais() {
        when(ligaRepository.filtrar(null, null, null)).thenReturn(List.of(ligaDeTeste(null)));

        List<LigaDTO> resultado = ligaService.listarLiga(null, null, null);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.getFirst().nome()).isEqualTo("Brasileirão");
        assertThat(resultado.getFirst().pais()).isNull();
    }

    @Test
    void naoConsultaRepositoryQuandoPaisNaoExiste() {
        doThrow(new NotFoundException("País com id 99 não encontrado."))
                .when(parameterValidator).validarPais(99L);

        assertThatThrownBy(() -> ligaService.listarLiga(null, null, 99L))
                .isInstanceOf(NotFoundException.class);

        verifyNoInteractions(ligaRepository);
    }

    @Test
    void buscarPorIdDevolveDtoQuandoLigaExiste() {
        Pais pais = Pais.builder().paisId(1L).nomePais("Brasil").sigla("BRA").build();
        when(ligaRepository.findById(1L)).thenReturn(Optional.of(ligaDeTeste(pais)));

        LigaDTO dto = ligaService.buscarPorId(1L);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.pais()).isEqualTo("Brasil");
    }

    @Test
    void buscarPorIdLancaNotFoundQuandoLigaNaoExiste() {
        when(ligaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ligaService.buscarPorId(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("99");
    }
}
