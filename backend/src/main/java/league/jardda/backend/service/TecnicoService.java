package league.jardda.backend.service;

import league.jardda.backend.DTO.PageResponse;
import league.jardda.backend.DTO.TecnicoDTO;
import league.jardda.backend.entity.Tecnico;
import league.jardda.backend.exception.NotFoundException;
import league.jardda.backend.repository.TecnicoRepository;
import league.jardda.backend.utils.PaginacaoUtils;
import league.jardda.backend.utils.ParameterValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TecnicoService {
    private final TecnicoRepository tecnicoRepository;
    private final ParameterValidator parameterValidator;

    private TecnicoDTO toDTO(Tecnico tecnico) {
        return new TecnicoDTO(
                tecnico.getTecnicoId(),
                tecnico.getNomeTecnico(),
                tecnico.getAniversarioTecnico(),
                tecnico.getPaisTecnico().getNomePais()
        );
    }

    public PageResponse<TecnicoDTO> listarTecnico(String nome, String pais, Long paisId, Pageable pageable) {
        parameterValidator.validarPais(paisId);

        Page<Tecnico> tecnicos = tecnicoRepository.filtrar(nome, pais, paisId,
                PaginacaoUtils.semOrdenacao(pageable));

        if (tecnicos.getTotalElements() == 0) {
            throw new NotFoundException("Nenhum técnico encontrado para os filtros informados");
        }
        return PageResponse.de(tecnicos.map(this::toDTO));
    }

    public TecnicoDTO buscarPorId(Long id) {
        return tecnicoRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new NotFoundException("Técnico com id " + id + " não encontrado."));
    }
}
