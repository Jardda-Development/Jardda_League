package league.jardda.backend.DTO;

public record EstadioDTO(
        Long id,
        String nome,
        String cidade,
        Long clubeId,
        String clubeNome
) {
}
