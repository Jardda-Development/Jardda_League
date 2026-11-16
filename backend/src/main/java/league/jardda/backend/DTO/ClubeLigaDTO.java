package league.jardda.backend.DTO;

import java.time.LocalDate;

public record ClubeLigaDTO(
        Long id,
        Long clubeId,
        String clubeNome,
        Long ligaId,
        String ligaNome,
        LocalDate entrada,
        LocalDate saida
) {
}
