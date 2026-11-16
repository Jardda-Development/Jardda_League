package league.jardda.backend.service;

import league.jardda.backend.DTO.PageResponse;
import league.jardda.backend.DTO.PartidaDTO;
import league.jardda.backend.entity.Partida;
import league.jardda.backend.exception.NotFoundException;
import league.jardda.backend.repository.PartidaRepository;
import league.jardda.backend.utils.PaginacaoUtils;
import league.jardda.backend.utils.ParameterValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PartidaService {
    private final PartidaRepository partidaRepository;
    private final ParameterValidator parameterValidator;

    private PartidaDTO toDTO(Partida partida) {
        return new PartidaDTO(
                partida.getPartidaId(),
                partida.getClube1().getClubeId(),
                partida.getClube1().getNomeClube(),
                partida.getClube2().getClubeId(),
                partida.getClube2().getNomeClube(),
                partida.getLigaPartida().getLigaId(),
                partida.getLigaPartida().getNomeLiga(),
                partida.getEstadioPartida() == null ? null : partida.getEstadioPartida().getEstadioId(),
                partida.getEstadioPartida() == null ? null : partida.getEstadioPartida().getNomeEstadio(),
                partida.getDataPartida(),
                partida.getPublicoPartida(),
                partida.getRodadaPartida()
        );
    }

    public PageResponse<PartidaDTO> listarPartida(String clubeCasa, String clubeFora, Integer rodada,
                                                  Long ligaId, String estadio, Long estadioId,
                                                  Pageable pageable) {
        parameterValidator.validarLiga(ligaId);
        parameterValidator.validarEstadio(estadioId);

        Page<Partida> partidas = partidaRepository.filtrar(clubeCasa, clubeFora, rodada, ligaId,
                estadio, estadioId, null, PaginacaoUtils.semOrdenacao(pageable));

        if (partidas.getTotalElements() == 0) {
            throw new NotFoundException("Nenhuma partida encontrada para os filtros informados");
        }
        return PageResponse.de(partidas.map(this::toDTO));
    }

    public PageResponse<PartidaDTO> listarPartidasDoClube(Long clubeId, Integer rodada, Long ligaId,
                                                          String estadio, Long estadioId,
                                                          Pageable pageable) {
        parameterValidator.validarClube(clubeId);
        parameterValidator.validarLiga(ligaId);
        parameterValidator.validarEstadio(estadioId);

        Page<Partida> partidas = partidaRepository.filtrar(null, null, rodada, ligaId,
                estadio, estadioId, clubeId, PaginacaoUtils.semOrdenacao(pageable));

        if (partidas.getTotalElements() == 0) {
            throw new NotFoundException("Clube com id " + clubeId + " não possui partidas");
        }
        return PageResponse.de(partidas.map(this::toDTO));
    }

    public PartidaDTO buscarPorId(Long id) {
        return partidaRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new NotFoundException("Partida com id " + id + " não encontrada."));
    }
}
