package league.jardda.backend.controller;

import league.jardda.backend.DTO.ClubeLigaDTO;
import league.jardda.backend.DTO.LigaDTO;
import league.jardda.backend.DTO.PageResponse;
import league.jardda.backend.DTO.PartidaDTO;
import league.jardda.backend.service.ClubeLigaService;
import league.jardda.backend.service.LigaService;
import league.jardda.backend.service.PartidaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LigaController {
    private final LigaService ligaService;
    private final PartidaService partidaService;
    private final ClubeLigaService clubeLigaService;

    @GetMapping("/ligas")
    public ResponseEntity<List<LigaDTO>> listLiga(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String pais,
            @RequestParam(required = false) Long paisId
    ) {
        return ResponseEntity.ok(ligaService.listarLiga(nome, pais, paisId));
    }

    @GetMapping("/ligas/{id}")
    public ResponseEntity<LigaDTO> getLigaById(@PathVariable Long id) {
        return ResponseEntity.ok(ligaService.buscarPorId(id));
    }

    @GetMapping("/ligas/{id}/partidas")
    public ResponseEntity<PageResponse<PartidaDTO>> listPartidasDaLiga(
            @PathVariable Long id,
            @RequestParam(required = false) String clubeCasa,
            @RequestParam(required = false) String clubeFora,
            @RequestParam(required = false) Integer rodada,
            @RequestParam(required = false) String estadio,
            @RequestParam(required = false) Long estadioId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(partidaService.listarPartida(clubeCasa, clubeFora, rodada, id, estadio, estadioId, pageable));
    }

    @GetMapping("/ligas/{id}/clubes")
    public ResponseEntity<List<ClubeLigaDTO>> listClubesDaLiga(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean apenasAtivos) {
        return ResponseEntity.ok(clubeLigaService.listarPorLiga(id, apenasAtivos));
    }
}
