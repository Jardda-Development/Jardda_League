package league.jardda.backend.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ClubeJogadorDTO(
        Long id,
        Long jogadorId,
        String jogadorNome,
        Long clubeId,
        String clubeNome,
        String tipoContrato,
        LocalDate inicioContrato,
        LocalDate previsaoTermino,
        LocalDate terminoContrato,
        BigDecimal valorSalario
) {
}
