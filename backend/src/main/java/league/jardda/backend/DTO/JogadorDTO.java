package league.jardda.backend.DTO;

import java.time.LocalDate;

public record JogadorDTO(
        Long id,
        String nome,
        String pais,
        LocalDate dataDeNascimento,
        String posicao,
        Boolean lesionado
) {
}
