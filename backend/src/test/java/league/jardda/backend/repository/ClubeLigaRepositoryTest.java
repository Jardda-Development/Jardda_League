package league.jardda.backend.repository;

import league.jardda.backend.entity.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ClubeLigaRepositoryTest extends RepositoryTestBase {

    private static LocalDate ano(int ano) {
        return LocalDate.of(ano, 1, 1);
    }

    private Clube montarClubeEmLigas() {
        Pais brasil = salvarPais("Brasil", "BRA");
        Cidade cidade = salvarCidade("Sao Paulo", brasil);
        Clube clube = salvarClube("Corinthians", cidade);
        Clube outroClube = salvarClube("Palmeiras", cidade);

        Liga estadual = salvarLiga("Estadual", brasil);
        Liga nacional = salvarLiga("Nacional", brasil);
        Liga copa = salvarLiga("Copa", brasil);
        Liga outra = salvarLiga("Outra", brasil);

        salvarParticipacao(clube, estadual, ano(2015), ano(2018));
        salvarParticipacao(clube, nacional, ano(2019), null);
        salvarParticipacao(clube, copa, ano(2021), null);
        salvarParticipacao(outroClube, outra, ano(2022), null);
        return clube;
    }

    private Liga montarLigaComClubes() {
        Pais brasil = salvarPais("Brasil", "BRA");
        Cidade cidade = salvarCidade("Sao Paulo", brasil);
        Liga liga = salvarLiga("Nacional", brasil);
        Liga outraLiga = salvarLiga("Estadual", brasil);

        Clube antigo = salvarClube("Antigo FC", cidade);
        Clube atual = salvarClube("Atual FC", cidade);
        Clube novo = salvarClube("Novo FC", cidade);
        Clube deOutraLiga = salvarClube("Outro FC", cidade);

        salvarParticipacao(antigo, liga, ano(2015), ano(2018));
        salvarParticipacao(atual, liga, ano(2019), null);
        salvarParticipacao(novo, liga, ano(2021), null);
        salvarParticipacao(deOutraLiga, outraLiga, ano(2022), null);
        return liga;
    }

    @Test
    void buscarPorClube_todos_ordenaPorEntradaDecrescenteEIgnoraOutrosClubes() {
        Clube clube = montarClubeEmLigas();

        List<ClubeLiga> resultado = clubeLigaRepository.buscarPorClube(clube.getClubeId(), false);

        assertThat(resultado).extracting(cl -> cl.getLiga().getNomeLiga())
                .containsExactly("Copa", "Nacional", "Estadual");
    }

    @Test
    void buscarPorClube_apenasAtivos_excluiLigasEncerradas() {
        Clube clube = montarClubeEmLigas();

        List<ClubeLiga> resultado = clubeLigaRepository.buscarPorClube(clube.getClubeId(), true);

        assertThat(resultado).extracting(cl -> cl.getLiga().getNomeLiga())
                .containsExactly("Copa", "Nacional");
    }

    @Test
    void buscarPorLiga_todos_ordenaPorEntradaDecrescenteEIgnoraOutrasLigas() {
        Liga liga = montarLigaComClubes();

        List<ClubeLiga> resultado = clubeLigaRepository.buscarPorLiga(liga.getLigaId(), false);

        assertThat(resultado).extracting(cl -> cl.getClube().getNomeClube())
                .containsExactly("Novo FC", "Atual FC", "Antigo FC");
    }

    @Test
    void buscarPorLiga_apenasAtivos_excluiClubesQueSairam() {
        Liga liga = montarLigaComClubes();

        List<ClubeLiga> resultado = clubeLigaRepository.buscarPorLiga(liga.getLigaId(), true);

        assertThat(resultado).extracting(cl -> cl.getClube().getNomeClube())
                .containsExactly("Novo FC", "Atual FC");
    }

    @Test
    void buscarPorLiga_semParticipacoes_devolveListaVazia() {
        Liga liga = salvarLiga("Nacional", salvarPais("Brasil", "BRA"));

        List<ClubeLiga> resultado = clubeLigaRepository.buscarPorLiga(liga.getLigaId(), false);

        assertThat(resultado).isEmpty();
    }
}
