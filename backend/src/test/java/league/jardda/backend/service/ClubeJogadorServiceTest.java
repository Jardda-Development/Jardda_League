package league.jardda.backend.service;

import league.jardda.backend.DTO.ClubeJogadorDTO;
import league.jardda.backend.entity.Clube;
import league.jardda.backend.entity.ClubeJogador;
import league.jardda.backend.entity.Jogador;
import league.jardda.backend.exception.NotFoundException;
import league.jardda.backend.repository.ClubeJogadorRepository;
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
public class ClubeJogadorServiceTest {
    @Mock
    ClubeJogadorRepository clubeJogadorRepository;
    @Mock
    ParameterValidator parameterValidator;
    @InjectMocks
    ClubeJogadorService clubeJogadorService;

    private ClubeJogador contratoDeTeste() {
        Jogador jogador = Jogador.builder()
                .jogadorId(1L)
                .nomeJogador("Ronaldinho Gaúcho")
                .build();

        Clube clube = Clube.builder()
                .clubeId(10L)
                .nomeClube("Barcelona")
                .build();

        return ClubeJogador.builder()
                .clubeJogadorId(100L)
                .jogador(jogador)
                .clube(clube)
                .tipoContratoJogador("definitivo")
                .inicioContratoJogador(LocalDate.of(2003, 7, 19))
                .valorSalarioJogador(new BigDecimal("500000.00"))
                .build();
    }


    @Test
    void lancaNotFoundQuandoJogadorNaoTemClubeAtual() {
        when(clubeJogadorRepository.buscarPorJogador(1L, true))
                .thenReturn(List.of());

        assertThatThrownBy(() -> clubeJogadorService.buscarClubeAtual(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("não possui clube atual");
    }

    @Test
    void devolveDtoDoContratoAtivoQuandoJogadorTemClubeAtual() {
        when(clubeJogadorRepository.buscarPorJogador(1L, true))
                .thenReturn(List.of(contratoDeTeste()));

        ClubeJogadorDTO dto = clubeJogadorService.buscarClubeAtual(1L);

        assertThat(dto.jogadorId()).isEqualTo(1L);
        assertThat(dto.clubeId()).isEqualTo(10L);
        assertThat(dto.clubeNome()).isEqualTo("Barcelona");
        assertThat(dto.valorSalario()).isEqualByComparingTo("500000.00");
        assertThat(dto.terminoContrato()).isNull();
    }

    @Test
    void lancaNotFoundQuandoJogadorNaoTemContratos() {
        when(clubeJogadorRepository.buscarPorJogador(1L, false))
                .thenReturn(List.of());

        assertThatThrownBy(() -> clubeJogadorService.listarPorJogador(1L, false))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("não possui contratos");

        verify(parameterValidator).validarJogador(1L);
    }


    private ClubeJogador contratoDe(Long contratoId, Long jogadorId, String nomeJogador) {
        Jogador jogador = Jogador.builder()
                .jogadorId(jogadorId)
                .nomeJogador(nomeJogador)
                .build();

        Clube clube = Clube.builder()
                .clubeId(10L)
                .nomeClube("Barcelona")
                .build();

        return ClubeJogador.builder()
                .clubeJogadorId(contratoId)
                .jogador(jogador)
                .clube(clube)
                .tipoContratoJogador("profissional")
                .inicioContratoJogador(LocalDate.of(2003, 7, 19))
                .valorSalarioJogador(new BigDecimal("500000.00"))
                .build();
    }

    @Test
    void devolveElencoConvertidoParaDto() {
        when(clubeJogadorRepository.buscarPorClube(10L, true))
                .thenReturn(List.of(
                        contratoDe(100L, 1L, "Ronaldinho Gaúcho"),
                        contratoDe(101L, 2L, "Deco")));

        List<ClubeJogadorDTO> elenco = clubeJogadorService.listarElenco(10L, true);

        assertThat(elenco).hasSize(2);
        assertThat(elenco)
                .extracting(ClubeJogadorDTO::jogadorNome)
                .containsExactly("Ronaldinho Gaúcho", "Deco");
        verify(parameterValidator).validarClube(10L);
    }

    @Test
    void naoConsultaRepositoryQuandoClubeNaoExiste() {
        doThrow(new NotFoundException("Clube com id 99 não encontrado."))
                .when(parameterValidator).validarClube(99L);

        assertThatThrownBy(() -> clubeJogadorService.listarElenco(99L, true))
                .isInstanceOf(NotFoundException.class);

        verifyNoInteractions(clubeJogadorRepository);
    }

    @Test
    void lancaNotFoundQuandoClubeNaoTemJogadores() {
        when(clubeJogadorRepository.buscarPorClube(10L, false))
                .thenReturn(List.of());

        assertThatThrownBy(() -> clubeJogadorService.listarElenco(10L, false))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("não possui jogadores");

        verify(parameterValidator).validarClube(10L);
    }

    @Test
    void naoConsultaRepositoryQuandoJogadorNaoExiste() {
        doThrow(new NotFoundException("Jogador com id 99 não encontrado."))
                .when(parameterValidator).validarJogador(99L);

        assertThatThrownBy(() -> clubeJogadorService.buscarClubeAtual(99L))
                .isInstanceOf(NotFoundException.class);

        verifyNoInteractions(clubeJogadorRepository);
    }

    @Test
    void devolveContratosDoJogadorConvertidosParaDto() {
        when(clubeJogadorRepository.buscarPorJogador(1L, false))
                .thenReturn(List.of(
                        contratoDe(100L, 1L, "Ronaldinho Gaúcho"),
                        contratoDe(101L, 1L, "Ronaldinho Gaúcho")));

        List<ClubeJogadorDTO> contratos = clubeJogadorService.listarPorJogador(1L, false);

        assertThat(contratos).hasSize(2);
        assertThat(contratos)
                .extracting(ClubeJogadorDTO::jogadorId)
                .containsOnly(1L);
        verify(parameterValidator).validarJogador(1L);
    }
}
