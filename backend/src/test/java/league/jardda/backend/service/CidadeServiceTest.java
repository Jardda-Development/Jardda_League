package league.jardda.backend.service;

import league.jardda.backend.DTO.CidadeDTO;
import league.jardda.backend.entity.Cidade;
import league.jardda.backend.entity.Pais;
import league.jardda.backend.exception.NotFoundException;
import league.jardda.backend.repository.CidadeRepository;
import league.jardda.backend.utils.ParameterValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CidadeServiceTest {
    @Mock
    CidadeRepository cidadeRepository;
    @Mock
    ParameterValidator parameterValidator;
    @InjectMocks
    CidadeService cidadeService;

    private Cidade cidadeDeTeste() {
        Pais pais = Pais.builder().paisId(1L).nomePais("Brasil").sigla("BRA").build();

        return Cidade.builder()
                .cidadeId(1L)
                .nomeCidade("Porto Alegre")
                .pais(pais)
                .build();
    }

    @Test
    void lancaNotFoundQuandoNaoHaCidades() {
        when(cidadeRepository.filtrar(null, null, null)).thenReturn(List.of());

        assertThatThrownBy(() -> cidadeService.listarCidade(null, null, null))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Nenhuma cidade");
    }

    @Test
    void devolveCidadesConvertidasParaDto() {
        when(cidadeRepository.filtrar(null, null, null)).thenReturn(List.of(cidadeDeTeste()));

        List<CidadeDTO> resultado = cidadeService.listarCidade(null, null, null);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.getFirst().nome()).isEqualTo("Porto Alegre");
        assertThat(resultado.getFirst().pais()).isEqualTo("Brasil");
    }

    @Test
    void naoConsultaRepositoryQuandoPaisNaoExiste() {
        doThrow(new NotFoundException("País com id 99 não encontrado."))
                .when(parameterValidator).validarPais(99L);

        assertThatThrownBy(() -> cidadeService.listarCidade(null, null, 99L))
                .isInstanceOf(NotFoundException.class);

        verifyNoInteractions(cidadeRepository);
    }

    @Test
    void buscarPorIdDevolveDtoQuandoCidadeExiste() {
        when(cidadeRepository.findById(1L)).thenReturn(Optional.of(cidadeDeTeste()));

        CidadeDTO dto = cidadeService.buscarPorId(1L);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.nome()).isEqualTo("Porto Alegre");
    }

    @Test
    void buscarPorIdLancaNotFoundQuandoCidadeNaoExiste() {
        when(cidadeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cidadeService.buscarPorId(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("99");
    }
}
