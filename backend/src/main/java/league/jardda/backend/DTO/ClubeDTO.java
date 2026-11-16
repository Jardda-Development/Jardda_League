package league.jardda.backend.DTO;


public record ClubeDTO(
        Long id,
        String nome,
        String cidade,
        String pais,
        String siglaPais
) {
}
