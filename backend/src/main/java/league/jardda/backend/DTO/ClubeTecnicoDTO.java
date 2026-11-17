package league.jardda.backend.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ClubeTecnicoDTO(
        Long id,
        Long clubeId,
        String clubeNome,
        Long tecnicoId,
        String tecnicoNome,
        String tipoContrato,
        LocalDate inicioContrato,
        LocalDate previsaoTermino,
        LocalDate terminoContrato,
        BigDecimal valorSalario
) {
}
