package league.jardda.backend.repository;

import league.jardda.backend.entity.Cidade;
import league.jardda.backend.entity.Clube;
import league.jardda.backend.entity.Pais;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ClubeRepositoryTest extends RepositoryTestBase {

    @Test
    void filtrar_semFiltros_ordenaPorNome() {
        Cidade saoPaulo = salvarCidade("Sao Paulo", salvarPais("Brasil", "BRA"));
        salvarClube("Portuguesa", saoPaulo);
        salvarClube("Corinthians", saoPaulo);
        salvarClube("Palmeiras", saoPaulo);

        List<Clube> resultado = clubeRepository.filtrar(null, null, null, null);

        assertThat(resultado).extracting(Clube::getNomeClube)
                .containsExactly("Corinthians", "Palmeiras", "Portuguesa");
    }

    @Test
    void filtrar_porNome_contemEIgnoraMaiusculas() {
        Cidade rio = salvarCidade("Rio de Janeiro", salvarPais("Brasil", "BRA"));
        salvarClube("Vasco", rio);
        salvarClube("Fluminense", rio);
        salvarClube("Flamengo", rio);

        List<Clube> resultado = clubeRepository.filtrar("FL", null, null, null);

        assertThat(resultado).extracting(Clube::getNomeClube)
                .containsExactly("Flamengo", "Fluminense");
    }

    @Test
    void filtrar_porPais_ehExato() {
        Cidade conacri = salvarCidade("Conacri", salvarPais("Guine", "GIN"));
        Cidade bissau = salvarCidade("Bissau", salvarPais("Guine-Bissau", "GNB"));
        salvarClube("Horoya", conacri);
        salvarClube("Sporting de Bissau", bissau);

        List<Clube> resultado = clubeRepository.filtrar(null, "guine", null, null);

        assertThat(resultado).extracting(Clube::getNomeClube).containsExactly("Horoya");
    }

    @Test
    void filtrar_porCidadeId_soDaquelaCidade() {
        Pais brasil = salvarPais("Brasil", "BRA");
        Cidade saoPaulo = salvarCidade("Sao Paulo", brasil);
        Cidade rio = salvarCidade("Rio de Janeiro", brasil);
        salvarClube("Corinthians", saoPaulo);
        salvarClube("Flamengo", rio);

        List<Clube> resultado = clubeRepository.filtrar(null, null, saoPaulo.getCidadeId(), null);

        assertThat(resultado).extracting(Clube::getNomeClube).containsExactly("Corinthians");
    }

    @Test
    void filtrar_porPaisId_soDaquelePais() {
        Pais brasil = salvarPais("Brasil", "BRA");
        Pais argentina = salvarPais("Argentina", "ARG");
        salvarClube("Corinthians", salvarCidade("Sao Paulo", brasil));
        salvarClube("Boca Juniors", salvarCidade("Buenos Aires", argentina));

        List<Clube> resultado = clubeRepository.filtrar(null, null, null, brasil.getPaisId());

        assertThat(resultado).extracting(Clube::getNomeClube).containsExactly("Corinthians");
    }

    @Test
    void filtrar_combinados_aplicaTodosOsFiltros() {
        Pais brasil = salvarPais("Brasil", "BRA");
        Cidade saoPaulo = salvarCidade("Sao Paulo", brasil);
        Cidade rio = salvarCidade("Rio de Janeiro", brasil);
        salvarClube("Corinthians", saoPaulo);
        salvarClube("Palmeiras", saoPaulo);
        salvarClube("Cosmos", rio);

        List<Clube> resultado = clubeRepository.filtrar("co", null, saoPaulo.getCidadeId(), null);

        assertThat(resultado).extracting(Clube::getNomeClube).containsExactly("Corinthians");
    }

    @Test
    void filtrar_semResultado_devolveListaVazia() {
        salvarClube("Corinthians", salvarCidade("Sao Paulo", salvarPais("Brasil", "BRA")));

        List<Clube> resultado = clubeRepository.filtrar("xyz", null, null, null);

        assertThat(resultado).isEmpty();
    }
}
