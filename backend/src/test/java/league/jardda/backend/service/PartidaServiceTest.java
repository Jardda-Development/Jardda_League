package league.jardda.backend.service;

import league.jardda.backend.DTO.PageResponse;
import league.jardda.backend.DTO.PartidaDTO;
import league.jardda.backend.entity.Clube;
import league.jardda.backend.entity.Estadio;
import league.jardda.backend.entity.Liga;
import league.jardda.backend.entity.Partida;
import league.jardda.backend.exception.NotFoundException;
import league.jardda.backend.repository.PartidaRepository;
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
public class PartidaServiceTest {
    @Mock
    PartidaRepository partidaRepository;
    @Mock
    ParameterValidator parameterValidator;
    @InjectMocks
    PartidaService partidaService;

    private final Pageable pageable = PageRequest.of(0, 10);

    private Partida partidaDeTeste(Estadio estadio) {
        Clube casa = Clube.builder().clubeId(10L).nomeClube("Flamengo").build();
        Clube fora = Clube.builder().clubeId(11L).nomeClube("Vasco").build();
        Liga liga = Liga.builder().ligaId(2L).nomeLiga("Brasileirão").build();

        return Partida.builder()
                .partidaId(1L)
                .clube1(casa)
                .clube2(fora)
                .ligaPartida(liga)
                .estadioPartida(estadio)
                .dataPartida(LocalDate.of(2026, 5, 10))
                .publicoPartida(50000)
                .rodadaPartida(5)
                .build();
    }

    // ---------- listarPartida ----------
    @Test
    void lancaNotFoundQuandoNaoHaPartidas() {
        when(partidaRepository.filtrar(null, null, null, null, null, null, null, pageable))
                .thenReturn(Page.empty());

        assertThatThrownBy(() -> partidaService.listarPartida(null, null, null, null, null, null, pageable))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Nenhuma partida");
    }

    @Test
    void devolvePartidasConvertidasParaDto() {
        Estadio estadio = Estadio.builder().estadioId(3L).nomeEstadio("Maracanã").build();
        when(partidaRepository.filtrar(null, null, null, null, null, null, null, pageable))
                .thenReturn(new PageImpl<>(List.of(partidaDeTeste(estadio))));

        PageResponse<PartidaDTO> resposta =
                partidaService.listarPartida(null, null, null, null, null, null, pageable);

        assertThat(resposta.itens()).hasSize(1);
        assertThat(resposta.itens().getFirst().id()).isEqualTo(1L);
        assertThat(resposta.itens().getFirst().estadioId()).isEqualTo(3L);
        assertThat(resposta.itens().getFirst().estadio()).isEqualTo("Maracanã");
    }

    @Test
    void devolveDtoSemEstadioQuandoPartidaNaoTemEstadio() {
        when(partidaRepository.filtrar(null, null, null, null, null, null, null, pageable))
                .thenReturn(new PageImpl<>(List.of(partidaDeTeste(null))));

        PageResponse<PartidaDTO> resposta =
                partidaService.listarPartida(null, null, null, null, null, null, pageable);

        assertThat(resposta.itens().getFirst().id()).isEqualTo(1L);
        assertThat(resposta.itens().getFirst().estadioId()).isNull();
        assertThat(resposta.itens().getFirst().estadio()).isNull();
    }

    @Test
    void listarPartidaNaoConsultaRepositoryQuandoLigaNaoExiste() {
        doThrow(new NotFoundException("Liga com id 99 não encontrada."))
                .when(parameterValidator).validarLiga(99L);

        assertThatThrownBy(() -> partidaService.listarPartida(null, null, null, 99L, null, null, pageable))
                .isInstanceOf(NotFoundException.class);

        verifyNoInteractions(partidaRepository);
    }

    @Test
    void listarPartidaNaoConsultaRepositoryQuandoEstadioNaoExiste() {
        doThrow(new NotFoundException("Estádio com id 99 não encontrado."))
                .when(parameterValidator).validarEstadio(99L);

        assertThatThrownBy(() -> partidaService.listarPartida(null, null, null, null, null, 99L, pageable))
                .isInstanceOf(NotFoundException.class);

        verifyNoInteractions(partidaRepository);
    }

    // ---------- listarPartidasDoClube ----------
    @Test
    void lancaNotFoundQuandoClubeNaoTemPartidas() {
        when(partidaRepository.filtrar(null, null, null, null, null, null, 10L, pageable))
                .thenReturn(Page.empty());

        assertThatThrownBy(() -> partidaService.listarPartidasDoClube(10L, null, null, null, null, pageable))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("não possui partidas");

        verify(parameterValidator).validarClube(10L);
    }

    @Test
    void repassaCadaFiltroNaPosicaoCertaAoRepository() {
        when(partidaRepository.filtrar(null, null, 5, 2L, "Maracanã", 3L, 10L, pageable))
                .thenReturn(new PageImpl<>(List.of(partidaDeTeste(null))));

        PageResponse<PartidaDTO> resposta =
                partidaService.listarPartidasDoClube(10L, 5, 2L, "Maracanã", 3L, pageable);

        assertThat(resposta.itens()).hasSize(1);
        verify(parameterValidator).validarClube(10L);
        verify(parameterValidator).validarLiga(2L);
        verify(parameterValidator).validarEstadio(3L);
    }

    @Test
    void listarPartidasDoClubeNaoConsultaRepositoryQuandoClubeNaoExiste() {
        doThrow(new NotFoundException("Clube com id 99 não encontrado."))
                .when(parameterValidator).validarClube(99L);

        assertThatThrownBy(() -> partidaService.listarPartidasDoClube(99L, null, null, null, null, pageable))
                .isInstanceOf(NotFoundException.class);

        verifyNoInteractions(partidaRepository);
    }

    @Test
    void listarPartidasDoClubeNaoConsultaRepositoryQuandoLigaNaoExiste() {
        doThrow(new NotFoundException("Liga com id 99 não encontrada."))
                .when(parameterValidator).validarLiga(99L);

        assertThatThrownBy(() -> partidaService.listarPartidasDoClube(10L, null, 99L, null, null, pageable))
                .isInstanceOf(NotFoundException.class);

        verifyNoInteractions(partidaRepository);
    }

    @Test
    void listarPartidasDoClubeNaoConsultaRepositoryQuandoEstadioNaoExiste() {
        doThrow(new NotFoundException("Estádio com id 99 não encontrado."))
                .when(parameterValidator).validarEstadio(99L);

        assertThatThrownBy(() -> partidaService.listarPartidasDoClube(10L, null, null, null, 99L, pageable))
                .isInstanceOf(NotFoundException.class);

        verifyNoInteractions(partidaRepository);
    }

    @Test
    void buscarPorIdDevolveDtoQuandoPartidaExiste() {
        when(partidaRepository.findById(1L)).thenReturn(Optional.of(partidaDeTeste(null)));

        PartidaDTO dto = partidaService.buscarPorId(1L);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.estadioId()).isNull();
    }

    @Test
    void buscarPorIdLancaNotFoundQuandoPartidaNaoExiste() {
        when(partidaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> partidaService.buscarPorId(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("99");
    }
}
