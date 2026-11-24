package league.jardda.backend.repository;

import league.jardda.backend.entity.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ClubeJogadorRepositoryTest extends RepositoryTestBase {

    private Clube montarElenco() {
        Pais brasil = salvarPais("Brasil", "BRA");
        Cidade cidade = salvarCidade("Sao Paulo", brasil);
        Clube clube = salvarClube("Corinthians", cidade);
        Clube outroClube = salvarClube("Palmeiras", cidade);

        Jogador antigo = salvarJogador("Antigo", brasil);
        Jogador medio = salvarJogador("Medio", brasil);
        Jogador recente = salvarJogador("Recente", brasil);
        Jogador deOutroClube = salvarJogador("Outro", brasil);

        salvarContratoJogador(clube, antigo, LocalDate.of(2018, 1, 1), LocalDate.of(2020, 1, 1));
        salvarContratoJogador(clube, medio, LocalDate.of(2019, 1, 1), null);
        salvarContratoJogador(clube, recente, LocalDate.of(2021, 1, 1), null);
        salvarContratoJogador(outroClube, deOutroClube, LocalDate.of(2022, 1, 1), null);
        return clube;
    }

    @Test
    void buscarPorClube_todos_ordenaPorInicioDecrescenteEIgnoraOutrosClubes() {
        Clube clube = montarElenco();

        List<ClubeJogador> resultado = clubeJogadorRepository.buscarPorClube(clube.getClubeId(), false);

        assertThat(resultado).extracting(cj -> cj.getJogador().getNomeJogador())
                .containsExactly("Recente", "Medio", "Antigo");
    }

    @Test
    void buscarPorClube_apenasAtivos_excluiContratosEncerrados() {
        Clube clube = montarElenco();

        List<ClubeJogador> resultado = clubeJogadorRepository.buscarPorClube(clube.getClubeId(), true);

        assertThat(resultado).extracting(cj -> cj.getJogador().getNomeJogador())
                .containsExactly("Recente", "Medio");
    }

    @Test
    void buscarPorJogador_todos_ordenaPorInicioDecrescente() {
        Pais brasil = salvarPais("Brasil", "BRA");
        Cidade cidade = salvarCidade("Sao Paulo", brasil);
        Clube antigoClube = salvarClube("Santos", cidade);
        Clube atualClube = salvarClube("Corinthians", cidade);
        Jogador jogador = salvarJogador("Craque", brasil);
        Jogador outro = salvarJogador("Outro", brasil);
        salvarContratoJogador(antigoClube, jogador, LocalDate.of(2015, 1, 1), LocalDate.of(2018, 1, 1));
        salvarContratoJogador(atualClube, jogador, LocalDate.of(2018, 1, 1), null);
        salvarContratoJogador(atualClube, outro, LocalDate.of(2019, 1, 1), null);

        List<ClubeJogador> resultado = clubeJogadorRepository.buscarPorJogador(jogador.getJogadorId(), false);

        assertThat(resultado).extracting(cj -> cj.getClube().getNomeClube())
                .containsExactly("Corinthians", "Santos");
    }

    @Test
    void buscarPorJogador_apenasAtivos_soOContratoAtual() {
        Pais brasil = salvarPais("Brasil", "BRA");
        Cidade cidade = salvarCidade("Sao Paulo", brasil);
        Clube antigoClube = salvarClube("Santos", cidade);
        Clube atualClube = salvarClube("Corinthians", cidade);
        Jogador jogador = salvarJogador("Craque", brasil);
        salvarContratoJogador(antigoClube, jogador, LocalDate.of(2015, 1, 1), LocalDate.of(2018, 1, 1));
        salvarContratoJogador(atualClube, jogador, LocalDate.of(2018, 1, 1), null);

        List<ClubeJogador> resultado = clubeJogadorRepository.buscarPorJogador(jogador.getJogadorId(), true);

        assertThat(resultado).extracting(cj -> cj.getClube().getNomeClube())
                .containsExactly("Corinthians");
    }

    @Test
    void buscarPorClube_semContratos_devolveListaVazia() {
        Clube clube = salvarClube("Corinthians", salvarCidade("Sao Paulo", salvarPais("Brasil", "BRA")));

        List<ClubeJogador> resultado = clubeJogadorRepository.buscarPorClube(clube.getClubeId(), false);

        assertThat(resultado).isEmpty();
    }
}
