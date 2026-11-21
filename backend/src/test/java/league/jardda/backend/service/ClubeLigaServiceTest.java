package league.jardda.backend.service;

import league.jardda.backend.DTO.ClubeLigaDTO;
import league.jardda.backend.entity.Clube;
import league.jardda.backend.entity.ClubeLiga;
import league.jardda.backend.entity.Liga;
import league.jardda.backend.exception.NotFoundException;
import league.jardda.backend.repository.ClubeLigaRepository;
import league.jardda.backend.utils.ParameterValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClubeLigaServiceTest {
    @Mock
    ClubeLigaRepository clubeLigaRepository;
    @Mock
    ParameterValidator parameterValidator;
    @InjectMocks
    ClubeLigaService clubeLigaService;

    private ClubeLiga vinculoDe(Long vinculoId, Long clubeId, Long ligaId) {
        Clube clube = Clube.builder().clubeId(clubeId).nomeClube("Clube " + clubeId).build();
        Liga liga = Liga.builder().ligaId(ligaId).nomeLiga("Liga " + ligaId).build();

        return ClubeLiga.builder()
                .clubeLigaId(vinculoId)
                .clube(clube)
                .liga(liga)
                .periodoEntrada(LocalDate.of(2020, 1, 1))
                .build();
    }

    @Test
    void lancaNotFoundQuandoClubeNaoTemLigas() {
        when(clubeLigaRepository.buscarPorClube(10L, false)).thenReturn(List.of());

        assertThatThrownBy(() -> clubeLigaService.listarPorClube(10L, false))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("não possui ligas");

        verify(parameterValidator).validarClube(10L);
    }

    @Test
    void devolveLigasDoClubeConvertidasParaDto() {
        when(clubeLigaRepository.buscarPorClube(10L, true)).thenReturn(List.of(
                vinculoDe(100L, 10L, 1L),
                vinculoDe(101L, 10L, 2L)));

        List<ClubeLigaDTO> resultado = clubeLigaService.listarPorClube(10L, true);

        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(ClubeLigaDTO::ligaId).containsExactly(1L, 2L);
        verify(parameterValidator).validarClube(10L);
    }

    @Test
    void naoConsultaRepositoryQuandoClubeNaoExiste() {
        doThrow(new NotFoundException("Clube com id 99 não encontrado."))
                .when(parameterValidator).validarClube(99L);

        assertThatThrownBy(() -> clubeLigaService.listarPorClube(99L, true))
                .isInstanceOf(NotFoundException.class);

        verifyNoInteractions(clubeLigaRepository);
    }

    @Test
    void lancaNotFoundQuandoLigaNaoTemClubes() {
        when(clubeLigaRepository.buscarPorLiga(1L, false)).thenReturn(List.of());

        assertThatThrownBy(() -> clubeLigaService.listarPorLiga(1L, false))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("não possui clubes");

        verify(parameterValidator).validarLiga(1L);
    }

    @Test
    void devolveClubesDaLigaConvertidosParaDto() {
        when(clubeLigaRepository.buscarPorLiga(1L, true)).thenReturn(List.of(
                vinculoDe(100L, 10L, 1L),
                vinculoDe(101L, 11L, 1L)));

        List<ClubeLigaDTO> resultado = clubeLigaService.listarPorLiga(1L, true);

        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(ClubeLigaDTO::clubeId).containsExactly(10L, 11L);
        verify(parameterValidator).validarLiga(1L);
    }

    @Test
    void naoConsultaRepositoryQuandoLigaNaoExiste() {
        doThrow(new NotFoundException("Liga com id 99 não encontrada."))
                .when(parameterValidator).validarLiga(99L);

        assertThatThrownBy(() -> clubeLigaService.listarPorLiga(99L, true))
                .isInstanceOf(NotFoundException.class);

        verifyNoInteractions(clubeLigaRepository);
    }
}