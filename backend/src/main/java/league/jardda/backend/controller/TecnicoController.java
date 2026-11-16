package league.jardda.backend.controller;

import league.jardda.backend.DTO.ClubeTecnicoDTO;
import league.jardda.backend.DTO.PageResponse;
import league.jardda.backend.DTO.TecnicoDTO;
import league.jardda.backend.service.ClubeTecnicoService;
import league.jardda.backend.service.TecnicoService;
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
public class TecnicoController {
    private final TecnicoService tecnicoService;
    private final ClubeTecnicoService clubeTecnicoService;

    @GetMapping("/tecnicos")
    public ResponseEntity<PageResponse<TecnicoDTO>> listTecnico(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String pais,
            @RequestParam(required = false) Long paisId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(tecnicoService.listarTecnico(nome, pais, paisId, pageable));
    }

    @GetMapping("/tecnicos/{id}")
    public ResponseEntity<TecnicoDTO> getTecnicoById(@PathVariable Long id) {
        return ResponseEntity.ok(tecnicoService.buscarPorId(id));
    }

    @GetMapping("/tecnicos/{id}/contratos")
    public ResponseEntity<List<ClubeTecnicoDTO>> listContratosDoTecnico(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean apenasAtivos) {
        return ResponseEntity.ok(clubeTecnicoService.listarPorTecnico(id, apenasAtivos));
    }
}
