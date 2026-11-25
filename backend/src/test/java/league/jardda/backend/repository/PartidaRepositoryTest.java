package league.jardda.backend.repository;

import league.jardda.backend.entity.Cidade;
import league.jardda.backend.entity.Clube;
import league.jardda.backend.entity.Estadio;
import league.jardda.backend.entity.Liga;
import league.jardda.backend.entity.Pais;
import league.jardda.backend.entity.Partida;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PartidaRepositoryTest extends RepositoryTestBase {
    private static final Pageable TUDO = PageRequest.of(0, 10);

    private Clube corinthians;
    private Clube palmeiras;
    private Clube santos;
    private Liga brasileirao;
    private Estadio arena;
    private Estadio allianz;
    private Partida p1;
    private Partida p2;
    private Partida p3;

    private static LocalDate marco(int dia) {
        return LocalDate.of(2024, 3, dia);
    }

    @BeforeEach
    void montarCenario() {
        Pais brasil = salvarPais("Brasil", "BRA");
        Cidade cidade = salvarCidade("Sao Paulo", brasil);
        corinthians = salvarClube("Corinthians", cidade);
        palmeiras = salvarClube("Palmeiras", cidade);
        santos = salvarClube("Santos", cidade);
        Clube portuguesa = salvarClube("Portuguesa", cidade);
        brasileirao = salvarLiga("Brasileirao", brasil);
        Liga copa = salvarLiga("Copa do Brasil", brasil);
        arena = salvarEstadio("Arena Corinthians", cidade, corinthians);
        allianz = salvarEstadio("Allianz Parque", cidade, palmeiras);

        p1 = salvarPartida(corinthians, palmeiras, brasileirao, arena, 1, marco(10));
        p2 = salvarPartida(santos, portuguesa, brasileirao, null, 1, marco(3));
        p3 = salvarPartida(palmeiras, santos, copa, allianz, 2, marco(17));
    }

    // ordem dos parâmetros: casa, fora, rodada, liga, estadio, estadioId, clubeId, pageable

    @Test
    void filtrar_semFiltros_ordenaPorDataEIncluiPartidaSemEstadio() {
        Page<Partida> pagina = partidaRepository.filtrar(null, null, null, null, null, null, null, TUDO);

        assertThat(pagina.getContent()).extracting(Partida::getPartidaId)
                .containsExactly(p2.getPartidaId(), p1.getPartidaId(), p3.getPartidaId());
        assertThat(pagina.getTotalElements()).isEqualTo(3);
    }

    @Test
    void filtrar_porClubeCasa_soQuandoOClubeJogaEmCasa() {
        Page<Partida> pagina = partidaRepository.filtrar("PALMEIRAS", null, null, null, null, null, null, PageRequest.of(0, 1));

        assertThat(pagina.getContent()).extracting(Partida::getPartidaId)
                .containsExactly(p3.getPartidaId());
        assertThat(pagina.getTotalElements()).isEqualTo(1);
    }

    @Test
    void filtrar_porClubeFora_soQuandoOClubeJogaFora() {
        Page<Partida> pagina = partidaRepository.filtrar(null, "santos", null, null, null, null, null, PageRequest.of(0, 1));

        assertThat(pagina.getContent()).extracting(Partida::getPartidaId)
                .containsExactly(p3.getPartidaId());
        assertThat(pagina.getTotalElements()).isEqualTo(1);
    }

    @Test
    void filtrar_porRodada() {
        Page<Partida> pagina = partidaRepository.filtrar(null, null, 1, null, null, null, null, PageRequest.of(0, 2));

        assertThat(pagina.getContent()).extracting(Partida::getPartidaId)
                .containsExactly(p2.getPartidaId(), p1.getPartidaId());
        assertThat(pagina.getTotalElements()).isEqualTo(2);
    }

    @Test
    void filtrar_porLiga() {
        Page<Partida> pagina = partidaRepository.filtrar(null, null, null, brasileirao.getLigaId(), null, null, null, PageRequest.of(0, 2));

        assertThat(pagina.getContent()).extracting(Partida::getPartidaId)
                .containsExactly(p2.getPartidaId(), p1.getPartidaId());
        assertThat(pagina.getTotalElements()).isEqualTo(2);
    }

    @Test
    void filtrar_porNomeDoEstadio_contemEExcluiPartidaSemEstadio() {
        Page<Partida> pagina = partidaRepository.filtrar(null, null, null, null, "ARENA", null, null, PageRequest.of(0, 1));

        assertThat(pagina.getContent()).extracting(Partida::getPartidaId)
                .containsExactly(p1.getPartidaId());
        assertThat(pagina.getTotalElements()).isEqualTo(1);
    }

    @Test
    void filtrar_porEstadioId() {
        Page<Partida> pagina = partidaRepository.filtrar(null, null, null, null, null, allianz.getEstadioId(), null, PageRequest.of(0, 1));

        assertThat(pagina.getContent()).extracting(Partida::getPartidaId)
                .containsExactly(p3.getPartidaId());
        assertThat(pagina.getTotalElements()).isEqualTo(1);
    }

    @Test
    void filtrar_porClubeId_vemDeQualquerLado() {
        Page<Partida> pagina = partidaRepository.filtrar(null, null, null, null, null, null, santos.getClubeId(), PageRequest.of(0, 2));

        assertThat(pagina.getContent()).extracting(Partida::getPartidaId)
                .containsExactly(p2.getPartidaId(), p3.getPartidaId());
        assertThat(pagina.getTotalElements()).isEqualTo(2);
    }

    @Test
    void filtrar_combinados_aplicaTodosOsFiltros() {
        Page<Partida> pagina = partidaRepository.filtrar(null, null, 1, null, null, null, santos.getClubeId(), PageRequest.of(0, 1));

        assertThat(pagina.getContent()).extracting(Partida::getPartidaId)
                .containsExactly(p2.getPartidaId());
        assertThat(pagina.getTotalElements()).isEqualTo(1);
    }

    @Test
    void filtrar_primeiraPagina() {
        Page<Partida> pagina = partidaRepository.filtrar(null, null, null, null, null, null, null, PageRequest.of(0, 2));

        assertThat(pagina.getContent()).extracting(Partida::getPartidaId)
                .containsExactly(p2.getPartidaId(), p1.getPartidaId());
        assertThat(pagina.getTotalElements()).isEqualTo(3);
        assertThat(pagina.getTotalPages()).isEqualTo(2);
    }

    @Test
    void filtrar_ultimaPagina() {
        Page<Partida> pagina = partidaRepository.filtrar(null, null, null, null, null, null, null, PageRequest.of(1, 2));

        assertThat(pagina.getContent()).extracting(Partida::getPartidaId)
                .containsExactly(p3.getPartidaId());
        assertThat(pagina.hasNext()).isFalse();
    }

    @Test
    void filtrar_semResultado_devolvePaginaVazia() {
        Page<Partida> pagina = partidaRepository.filtrar("xyz", null, null, null, null, null, null, TUDO);

        assertThat(pagina.isEmpty()).isTrue();
        assertThat(pagina.getTotalElements()).isZero();
    }
}