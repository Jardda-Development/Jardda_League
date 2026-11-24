package league.jardda.backend.repository;

import league.jardda.backend.entity.Cidade;
import league.jardda.backend.entity.Clube;
import league.jardda.backend.entity.Estadio;
import league.jardda.backend.entity.Pais;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EstadioRepositoryTest extends RepositoryTestBase {

    @Test
    void filtrar_semFiltros_incluiEstadiosSemClubeEOrdenaPorNome() {
        Cidade saoPaulo = salvarCidade("Sao Paulo", salvarPais("Brasil", "BRA"));
        Clube corinthians = salvarClube("Corinthians", saoPaulo);
        salvarEstadio("Pacaembu", saoPaulo, null);
        salvarEstadio("Morumbi", saoPaulo, null);
        salvarEstadio("Arena Corinthians", saoPaulo, corinthians);

        List<Estadio> resultado = estadioRepository.filtrar(null, null, null, null);

        assertThat(resultado).extracting(Estadio::getNomeEstadio)
                .containsExactly("Arena Corinthians", "Morumbi", "Pacaembu");
    }

    @Test
    void filtrar_porNome_contemEIgnoraMaiusculas() {
        Cidade saoPaulo = salvarCidade("Sao Paulo", salvarPais("Brasil", "BRA"));
        salvarEstadio("Arena Palmeiras", saoPaulo, null);
        salvarEstadio("Morumbi", saoPaulo, null);
        salvarEstadio("Arena Corinthians", saoPaulo, null);

        List<Estadio> resultado = estadioRepository.filtrar("ARENA", null, null, null);

        assertThat(resultado).extracting(Estadio::getNomeEstadio)
                .containsExactly("Arena Corinthians", "Arena Palmeiras");
    }

    @Test
    void filtrar_porClube_ehExatoEExcluiEstadiosSemClube() {
        Cidade portoAlegre = salvarCidade("Porto Alegre", salvarPais("Brasil", "BRA"));
        Clube inter = salvarClube("Inter", portoAlegre);
        Clube internacional = salvarClube("Internacional", portoAlegre);
        salvarEstadio("Estadio Alfa", portoAlegre, inter);
        salvarEstadio("Estadio Beta", portoAlegre, internacional);
        salvarEstadio("Estadio Gama", portoAlegre, null);

        List<Estadio> resultado = estadioRepository.filtrar(null, "inter", null, null);

        assertThat(resultado).extracting(Estadio::getNomeEstadio).containsExactly("Estadio Alfa");
    }

    @Test
    void filtrar_porCidadeId_soDaquelaCidade() {
        Pais brasil = salvarPais("Brasil", "BRA");
        Cidade saoPaulo = salvarCidade("Sao Paulo", brasil);
        Cidade rio = salvarCidade("Rio de Janeiro", brasil);
        salvarEstadio("Morumbi", saoPaulo, null);
        salvarEstadio("Maracana", rio, null);

        List<Estadio> resultado = estadioRepository.filtrar(null, null, saoPaulo.getCidadeId(), null);

        assertThat(resultado).extracting(Estadio::getNomeEstadio).containsExactly("Morumbi");
    }

    @Test
    void filtrar_porClubeId_soDaqueleClubeEExcluiSemClube() {
        Cidade saoPaulo = salvarCidade("Sao Paulo", salvarPais("Brasil", "BRA"));
        Clube corinthians = salvarClube("Corinthians", saoPaulo);
        Clube palmeiras = salvarClube("Palmeiras", saoPaulo);
        salvarEstadio("Arena Corinthians", saoPaulo, corinthians);
        salvarEstadio("Arena Palmeiras", saoPaulo, palmeiras);
        salvarEstadio("Pacaembu", saoPaulo, null);

        List<Estadio> resultado = estadioRepository.filtrar(null, null, null, corinthians.getClubeId());

        assertThat(resultado).extracting(Estadio::getNomeEstadio).containsExactly("Arena Corinthians");
    }

    @Test
    void filtrar_combinados_aplicaTodosOsFiltros() {
        Pais brasil = salvarPais("Brasil", "BRA");
        Cidade saoPaulo = salvarCidade("Sao Paulo", brasil);
        Cidade rio = salvarCidade("Rio de Janeiro", brasil);
        salvarEstadio("Arena Corinthians", saoPaulo, null);
        salvarEstadio("Arena Carioca", rio, null);
        salvarEstadio("Morumbi", saoPaulo, null);

        List<Estadio> resultado = estadioRepository.filtrar("arena", null, saoPaulo.getCidadeId(), null);

        assertThat(resultado).extracting(Estadio::getNomeEstadio).containsExactly("Arena Corinthians");
    }

    @Test
    void filtrar_semResultado_devolveListaVazia() {
        salvarEstadio("Morumbi", salvarCidade("Sao Paulo", salvarPais("Brasil", "BRA")), null);

        List<Estadio> resultado = estadioRepository.filtrar("xyz", null, null, null);

        assertThat(resultado).isEmpty();
    }
}
