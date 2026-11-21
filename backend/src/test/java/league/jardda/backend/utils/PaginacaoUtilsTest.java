package league.jardda.backend.utils;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


public class PaginacaoUtilsTest {
    @Test
    void mantemNumeroDePagina() {
        Pageable entrada = PageRequest.of(2, 5, Sort.by("nome").descending());
        Pageable resultado = PaginacaoUtils.semOrdenacao(entrada);

        Assertions.assertThat(resultado.getPageNumber()).isEqualTo(2);
    }

    @Test
    void mantemTamanhoDePagina() {
        Pageable entrada = PageRequest.of(2, 5, Sort.by("nome").descending());
        Pageable resultado = PaginacaoUtils.semOrdenacao(entrada);

        Assertions.assertThat(resultado.getPageSize()).isEqualTo(5);
    }

    @Test
    void removeOrdenacao() {
        Pageable entrada = PageRequest.of(2, 5, Sort.by("nome").descending());
        Pageable resultado = PaginacaoUtils.semOrdenacao(entrada);

        Assertions.assertThat(resultado.getSort().isSorted()).isFalse();
    }
}
