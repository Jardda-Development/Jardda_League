package league.jardda.backend.DTO;

public record LigaDTO(
        Long id,
        String nome,
        String pais,
        String estilo,
        String categoria
) {
}
