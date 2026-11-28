package league.jardda.backend.repository;

import league.jardda.backend.entity.Jogador;
import league.jardda.backend.entity.Pais;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

class JogadorRepositoryTest extends RepositoryTestBase {

    private void salvarCincoJogadores() {
        Pais brasil = salvarPais("Brasil", "BRA");
        salvarJogador("Zico", brasil);
        salvarJogador("Pele", brasil);
        salvarJogador("Garrincha", brasil);
        salvarJogador("Romario", brasil);
        salvarJogador("Bebeto", brasil);
    }

    @Test
    void filtrar_semFiltros_primeiraPagina() {
        salvarCincoJogadores();

        Page<Jogador> pagina = jogadorRepository.filtrar(null, null, null, PageRequest.of(0, 2));

        assertThat(pagina.getContent()).extracting(Jogador::getNomeJogador)
                .containsExactly("Bebeto", "Garrincha");
        assertThat(pagina.getTotalElements()).isEqualTo(5);
        assertThat(pagina.getTotalPages()).isEqualTo(3);
    }

    @Test
    void filtrar_semFiltros_ultimaPagina() {
        salvarCincoJogadores();

        Page<Jogador> pagina = jogadorRepository.filtrar(null, null, null, PageRequest.of(2, 2));

        assertThat(pagina.getContent()).extracting(Jogador::getNomeJogador)
                .containsExactly("Zico");
        assertThat(pagina.hasNext()).isFalse();
    }

    @Test
    void filtrar_porNome_totalRespeitaOFiltro() {
        salvarCincoJogadores();

        Page<Jogador> pagina = jogadorRepository.filtrar("O", null, null, PageRequest.of(0, 2));

        assertThat(pagina.getContent()).extracting(Jogador::getNomeJogador)
                .containsExactly("Bebeto", "Romario");
        assertThat(pagina.getTotalElements()).isEqualTo(3);
        assertThat(pagina.getTotalPages()).isEqualTo(2);
    }

    @Test
    void filtrar_porPais_ehExato() {
        Pais guine = salvarPais("Guine", "GIN");
        Pais bissau = salvarPais("Guine-Bissau", "GNB");
        salvarJogador("Sylla", guine);
        salvarJogador("Camara", guine);
        salvarJogador("Embalo", bissau);

        Page<Jogador> pagina = jogadorRepository.filtrar(null, "guine", null, PageRequest.of(0, 2));

        assertThat(pagina.getContent()).extracting(Jogador::getNomeJogador)
                .containsExactly("Camara", "Sylla");
        assertThat(pagina.getTotalElements()).isEqualTo(2);
    }

    @Test
    void filtrar_porPaisId_soDaquelePais() {
        Pais brasil = salvarPais("Brasil", "BRA");
        Pais argentina = salvarPais("Argentina", "ARG");
        salvarJogador("Neymar", brasil);
        salvarJogador("Messi", argentina);
        salvarJogador("Dibu", argentina);

        Page<Jogador> pagina = jogadorRepository.filtrar(null, null, argentina.getPaisId(), PageRequest.of(0, 2));

        assertThat(pagina.getContent()).extracting(Jogador::getNomeJogador)
                .containsExactly("Dibu", "Messi");
        assertThat(pagina.getTotalElements()).isEqualTo(2);
    }

    @Test
    void filtrar_combinados_aplicaTodosOsFiltros() {
        Pais brasil = salvarPais("Brasil", "BRA");
        Pais argentina = salvarPais("Argentina", "ARG");
        salvarJogador("Marcelo", brasil);
        salvarJogador("Cafu", brasil);
        salvarJogador("Martinez", argentina);

        Page<Jogador> pagina = jogadorRepository.filtrar("mar", null, brasil.getPaisId(), PageRequest.of(0, 1));

        assertThat(pagina.getContent()).extracting(Jogador::getNomeJogador)
                .containsExactly("Marcelo");
        assertThat(pagina.getTotalElements()).isEqualTo(1);
    }

    @Test
    void filtrar_semResultado_devolvePaginaVazia() {
        salvarCincoJogadores();

        Page<Jogador> pagina = jogadorRepository.filtrar("xyz", null, null, PageRequest.of(0, 10));

        assertThat(pagina.isEmpty()).isTrue();
        assertThat(pagina.getTotalElements()).isZero();
    }
}
