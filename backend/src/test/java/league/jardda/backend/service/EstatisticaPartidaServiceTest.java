package league.jardda.backend.service;

import league.jardda.backend.DTO.EstatisticaPartidaDTO;
import league.jardda.backend.entity.EstatisticaPartida;
import league.jardda.backend.exception.NotFoundException;
import league.jardda.backend.repository.EstatisticaPartidaRepository;
import league.jardda.backend.utils.ParameterValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EstatisticaPartidaServiceTest {
    @Mock
    EstatisticaPartidaRepository estatisticaPartidaRepository;
    @Mock
    ParameterValidator parameterValidator;
    @InjectMocks
    EstatisticaPartidaService estatisticaPartidaService;

    @Test
    void naoConsultaRepositoryQuandoPartidaNaoExiste() {
        doThrow(new NotFoundException("Partida com id 99 não encontrada."))
                .when(parameterValidator).validarPartida(99L);

        assertThatThrownBy(() -> estatisticaPartidaService.buscarPorPartida(99L))
                .isInstanceOf(NotFoundException.class);

        verifyNoInteractions(estatisticaPartidaRepository);
    }

    @Test
    void lancaNotFoundQuandoPartidaNaoTemEstatisticas() {
        when(estatisticaPartidaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> estatisticaPartidaService.buscarPorPartida(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("não possui estatísticas");

        verify(parameterValidator).validarPartida(1L);
    }

    @Test
    void devolveEstatisticasConvertidasParaDto() {
        EstatisticaPartida estatistica = EstatisticaPartida.builder()
                .partidaId(1L)
                .time1Gols(2)
                .time2Gols(1)
                .time1Cartoes(3)
                .time2Cartoes(4)
                .time1Chutes(10)
                .time2Chutes(7)
                .build();

        when(estatisticaPartidaRepository.findById(1L)).thenReturn(Optional.of(estatistica));

        EstatisticaPartidaDTO dto = estatisticaPartidaService.buscarPorPartida(1L);

        assertThat(dto.partidaId()).isEqualTo(1L);
        assertThat(dto.clubeCasaGols()).isEqualTo(2);
        assertThat(dto.clubeForaGols()).isEqualTo(1);
        assertThat(dto.clubeCasaCartoes()).isEqualTo(3);
        assertThat(dto.clubeForaCartoes()).isEqualTo(4);
        assertThat(dto.clubeCasaChutes()).isEqualTo(10);
        assertThat(dto.clubeForaChutes()).isEqualTo(7);
        verify(parameterValidator).validarPartida(1L);
    }
}
