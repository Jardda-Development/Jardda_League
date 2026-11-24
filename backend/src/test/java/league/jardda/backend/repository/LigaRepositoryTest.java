package league.jardda.backend.repository;

import league.jardda.backend.entity.Liga;
import league.jardda.backend.entity.Pais;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LigaRepositoryTest extends RepositoryTestBase {

    @Test
    void filtrar_semFiltros_incluiLigasSemPaisEOrdenaPorNome() {
        Pais brasil = salvarPais("Brasil", "BRA");
        salvarLiga("Libertadores", null);
        salvarLiga("Campeonato Brasileiro", brasil);
        salvarLiga("Amistosos de Verao", null);

        List<Liga> resultado = ligaRepository.filtrar(null, null, null);

        assertThat(resultado).extracting(Liga::getNomeLiga)
                .containsExactly("Amistosos de Verao", "Campeonato Brasileiro", "Libertadores");
    }

    @Test
    void filtrar_porNome_contemEIgnoraMaiusculas() {
        Pais brasil = salvarPais("Brasil", "BRA");
        salvarLiga("Copa do Brasil", brasil);
        salvarLiga("Copa Libertadores", null);
        salvarLiga("Brasileirao", brasil);

        List<Liga> resultado = ligaRepository.filtrar("COPA", null, null);

        assertThat(resultado).extracting(Liga::getNomeLiga)
                .containsExactly("Copa do Brasil", "Copa Libertadores");
    }

    @Test
    void filtrar_porPais_ehExatoEExcluiLigasSemPais() {
        salvarLiga("Liga Guine", salvarPais("Guine", "GIN"));
        salvarLiga("Liga Bissau", salvarPais("Guine-Bissau", "GNB"));
        salvarLiga("Liga Sem Pais", null);

        List<Liga> resultado = ligaRepository.filtrar(null, "guine", null);

        assertThat(resultado).extracting(Liga::getNomeLiga).containsExactly("Liga Guine");
    }

    @Test
    void filtrar_porPaisId_soDaquelePais() {
        Pais brasil = salvarPais("Brasil", "BRA");
        Pais argentina = salvarPais("Argentina", "ARG");
        salvarLiga("Campeonato Brasileiro", brasil);
        salvarLiga("Liga Argentina", argentina);
        salvarLiga("Liga Sem Pais", null);

        List<Liga> resultado = ligaRepository.filtrar(null, null, brasil.getPaisId());

        assertThat(resultado).extracting(Liga::getNomeLiga).containsExactly("Campeonato Brasileiro");
    }

    @Test
    void filtrar_combinados_aplicaTodosOsFiltros() {
        Pais brasil = salvarPais("Brasil", "BRA");
        Pais argentina = salvarPais("Argentina", "ARG");
        salvarLiga("Copa do Brasil", brasil);
        salvarLiga("Campeonato Brasileiro", brasil);
        salvarLiga("Copa Argentina", argentina);

        List<Liga> resultado = ligaRepository.filtrar("copa", null, brasil.getPaisId());

        assertThat(resultado).extracting(Liga::getNomeLiga).containsExactly("Copa do Brasil");
    }

    @Test
    void filtrar_semResultado_devolveListaVazia() {
        salvarLiga("Campeonato Brasileiro", salvarPais("Brasil", "BRA"));

        List<Liga> resultado = ligaRepository.filtrar("xyz", null, null);

        assertThat(resultado).isEmpty();
    }
}
