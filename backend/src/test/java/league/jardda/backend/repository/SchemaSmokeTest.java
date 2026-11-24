package league.jardda.backend.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class SchemaSmokeTest extends RepositoryTestBase {
    @Autowired
    private PaisRepository paisRepository;

    @Test
    void deveSubirOContainerComOSchemaCriado() {
        assertThat(paisRepository.count()).isZero();
    }
}