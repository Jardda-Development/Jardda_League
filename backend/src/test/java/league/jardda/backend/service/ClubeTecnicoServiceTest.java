package league.jardda.backend.service;

import league.jardda.backend.DTO.ClubeTecnicoDTO;
import league.jardda.backend.entity.Clube;
import league.jardda.backend.entity.ClubeTecnico;
import league.jardda.backend.entity.Tecnico;
import league.jardda.backend.exception.NotFoundException;
import league.jardda.backend.repository.ClubeTecnicoRepository;
import league.jardda.backend.utils.ParameterValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClubeTecnicoServiceTest {
    @Mock
    ClubeTecnicoRepository clubeTecnicoRepository;
    @Mock
    ParameterValidator parameterValidator;
    @InjectMocks
    ClubeTecnicoService clubeTecnicoService;

    private ClubeTecnico contratoDe(Long contratoId, Long tecnicoId, String nomeTecnico) {
        Tecnico tecnico = Tecnico.builder().tecnicoId(tecnicoId).nomeTecnico(nomeTecnico).build();
        Clube clube = Clube.builder().clubeId(10L).nomeClube("Barcelona").build();

        return ClubeTecnico.builder()
                .clubeTecnicoId(contratoId)
                .clube(clube)
                .tecnico(tecnico)
                .tipoContratoTecnico("profissional")
                .inicioContratoTecnico(LocalDate.of(2020, 1, 10))
                .valorSalarioTecnico(new BigDecimal("300000.00"))
                .build();
    }

    @Test
    void lancaNotFoundQuandoClubeNaoTemTecnicos() {
        when(clubeTecnicoRepository.buscarPorClube(10L, false)).thenReturn(List.of());

        assertThatThrownBy(() -> clubeTecnicoService.listarPorClube(10L, false))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("não possui técnicos");

        verify(parameterValidator).validarClube(10L);
    }

    @Test
    void devolveTecnicosDoClubeConvertidosParaDto() {
        when(clubeTecnicoRepository.buscarPorClube(10L, true)).thenReturn(List.of(
                contratoDe(100L, 1L, "Guardiola"),
                contratoDe(101L, 2L, "Xavi")));

        List<ClubeTecnicoDTO> resultado = clubeTecnicoService.listarPorClube(10L, true);

        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(ClubeTecnicoDTO::tecnicoId).containsExactly(1L, 2L);
        verify(parameterValidator).validarClube(10L);
    }

    @Test
    void naoConsultaRepositoryQuandoClubeNaoExiste() {
        doThrow(new NotFoundException("Clube com id 99 não encontrado."))
                .when(parameterValidator).validarClube(99L);

        assertThatThrownBy(() -> clubeTecnicoService.listarPorClube(99L, true))
                .isInstanceOf(NotFoundException.class);

        verifyNoInteractions(clubeTecnicoRepository);
    }

    @Test
    void lancaNotFoundQuandoTecnicoNaoTemContratos() {
        when(clubeTecnicoRepository.buscarPorTecnico(1L, false)).thenReturn(List.of());

        assertThatThrownBy(() -> clubeTecnicoService.listarPorTecnico(1L, false))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("não possui contratos");

        verify(parameterValidator).validarTecnico(1L);
    }

    @Test
    void devolveContratosDoTecnicoConvertidosParaDto() {
        when(clubeTecnicoRepository.buscarPorTecnico(1L, false)).thenReturn(List.of(
                contratoDe(100L, 1L, "Guardiola"),
                contratoDe(101L, 1L, "Guardiola")));

        List<ClubeTecnicoDTO> resultado = clubeTecnicoService.listarPorTecnico(1L, false);

        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(ClubeTecnicoDTO::tecnicoId).containsOnly(1L);
        verify(parameterValidator).validarTecnico(1L);
    }

    @Test
    void naoConsultaRepositoryQuandoTecnicoNaoExiste() {
        doThrow(new NotFoundException("Técnico com id 99 não encontrado."))
                .when(parameterValidator).validarTecnico(99L);

        assertThatThrownBy(() -> clubeTecnicoService.listarPorTecnico(99L, true))
                .isInstanceOf(NotFoundException.class);

        verifyNoInteractions(clubeTecnicoRepository);
    }
}
