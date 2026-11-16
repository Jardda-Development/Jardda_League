package league.jardda.backend.service;

import league.jardda.backend.DTO.JogadorDTO;
import league.jardda.backend.DTO.PageResponse;
import league.jardda.backend.entity.Jogador;
import league.jardda.backend.exception.NotFoundException;
import league.jardda.backend.repository.JogadorRepository;
import league.jardda.backend.utils.PaginacaoUtils;
import league.jardda.backend.utils.ParameterValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JogadorService {
    private final JogadorRepository jogadorRepository;
    private final ParameterValidator parameterValidator;

    private JogadorDTO toDTO(Jogador jogador) {
        return new JogadorDTO(
                jogador.getJogadorId(),
                jogador.getNomeJogador(),
                jogador.getPaisJogador().getNomePais(),
                jogador.getAniversarioJogador(),
                jogador.getPosicao(),
                jogador.getLesionado()
        );
    }

    public PageResponse<JogadorDTO> listarJogador(String nome, String pais, Long paisId, Pageable pageable) {
        parameterValidator.validarPais(paisId);

        Page<Jogador> jogadores = jogadorRepository.filtrar(nome, pais, paisId,
                PaginacaoUtils.semOrdenacao(pageable));

        if (jogadores.getTotalElements() == 0) {
            throw new NotFoundException("Nenhum jogador encontrado para os filtros informados");
        }
        return PageResponse.de(jogadores.map(this::toDTO));
    }

    public JogadorDTO buscarPorId(Long id) {
        return jogadorRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new NotFoundException("Jogador com id " + id + " não encontrado."));
    }
}