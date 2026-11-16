package league.jardda.backend.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public final class PaginacaoUtils {
    private PaginacaoUtils() {}

    public static Pageable semOrdenacao(Pageable pageable) {
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
    }
}