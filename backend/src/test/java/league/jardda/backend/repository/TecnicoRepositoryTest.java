package league.jardda.backend.repository;

import league.jardda.backend.entity.Pais;
import league.jardda.backend.entity.Tecnico;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

class TecnicoRepositoryTest extends RepositoryTestBase {

    private void salvarCincoTecnicos() {
        Pais brasil = salvarPais("Brasil", "BRA");
        salvarTecnico("Zagallo", brasil);
        salvarTecnico("Tite", brasil);
        salvarTecnico("Felipao", brasil);
        salvarTecnico("Dorival", brasil);
        salvarTecnico("Abel", brasil);
    }

    @Test
    void filtrar_semFiltros_primeiraPagina() {
        salvarCincoTecnicos();

        Page<Tecnico> pagina = tecnicoRepository.filtrar(null, null, null, PageRequest.of(0, 2));

        assertThat(pagina.getContent()).extracting(Tecnico::getNomeTecnico)
                .containsExactly("Abel", "Dorival");
        assertThat(pagina.getTotalElements()).isEqualTo(5);
        assertThat(pagina.getTotalPages()).isEqualTo(3);
    }

    @Test
    void filtrar_semFiltros_ultimaPagina() {
        salvarCincoTecnicos();

        Page<Tecnico> pagina = tecnicoRepository.filtrar(null, null, null, PageRequest.of(2, 2));

        assertThat(pagina.getContent()).extracting(Tecnico::getNomeTecnico)
                .containsExactly("Zagallo");
        assertThat(pagina.hasNext()).isFalse();
    }

    @Test
    void filtrar_porNome_totalRespeitaOFiltro() {
        salvarCincoTecnicos();

        Page<Tecnico> pagina = tecnicoRepository.filtrar("O", null, null, PageRequest.of(0, 2));

        assertThat(pagina.getContent()).extracting(Tecnico::getNomeTecnico)
                .containsExactly("Dorival", "Felipao");
        assertThat(pagina.getTotalElements()).isEqualTo(3);
        assertThat(pagina.getTotalPages()).isEqualTo(2);
    }

    @Test
    void filtrar_porPais_ehExato() {
        Pais guine = salvarPais("Guine", "GIN");
        Pais bissau = salvarPais("Guine-Bissau", "GNB");
        salvarTecnico("Sylla", guine);
        salvarTecnico("Camara", guine);
        salvarTecnico("Embalo", bissau);

        Page<Tecnico> pagina = tecnicoRepository.filtrar(null, "guine", null, PageRequest.of(0, 2));

        assertThat(pagina.getContent()).extracting(Tecnico::getNomeTecnico)
                .containsExactly("Camara", "Sylla");
        assertThat(pagina.getTotalElements()).isEqualTo(2);
    }

    @Test
    void filtrar_porPaisId_soDaquelePais() {
        Pais brasil = salvarPais("Brasil", "BRA");
        Pais argentina = salvarPais("Argentina", "ARG");
        salvarTecnico("Tite", brasil);
        salvarTecnico("Scaloni", argentina);
        salvarTecnico("Bielsa", argentina);

        Page<Tecnico> pagina = tecnicoRepository.filtrar(null, null, argentina.getPaisId(), PageRequest.of(0, 2));

        assertThat(pagina.getContent()).extracting(Tecnico::getNomeTecnico)
                .containsExactly("Bielsa", "Scaloni");
        assertThat(pagina.getTotalElements()).isEqualTo(2);
    }

    @Test
    void filtrar_combinados_aplicaTodosOsFiltros() {
        Pais brasil = salvarPais("Brasil", "BRA");
        Pais argentina = salvarPais("Argentina", "ARG");
        salvarTecnico("Mano", brasil);
        salvarTecnico("Tite", brasil);
        salvarTecnico("Mancini", argentina);

        Page<Tecnico> pagina = tecnicoRepository.filtrar("ma", null, brasil.getPaisId(), PageRequest.of(0, 1));

        assertThat(pagina.getContent()).extracting(Tecnico::getNomeTecnico)
                .containsExactly("Mano");
        assertThat(pagina.getTotalElements()).isEqualTo(1);
    }

    @Test
    void filtrar_semResultado_devolvePaginaVazia() {
        salvarCincoTecnicos();

        Page<Tecnico> pagina = tecnicoRepository.filtrar("xyz", null, null, PageRequest.of(0, 10));

        assertThat(pagina.isEmpty()).isTrue();
        assertThat(pagina.getTotalElements()).isZero();
    }
}