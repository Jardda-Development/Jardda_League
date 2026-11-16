package league.jardda.backend.DTO;

public record EstatisticaPartidaDTO(
        Long partidaId,
        Integer clubeCasaGols,
        Integer clubeForaGols,
        Integer clubeCasaCartoes,
        Integer clubeForaCartoes,
        Integer clubeCasaChutes,
        Integer clubeForaChutes
) {
}
