package league.jardda.backend.service;

import league.jardda.backend.entity.Pais;
import league.jardda.backend.exception.NotFoundException;
import league.jardda.backend.repository.PaisRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaisServiceTest {
    @Mock
    PaisRepository paisRepository;
    @InjectMocks
    PaisService paisService;

    private final Sort ordemPorNome = Sort.by("nomePais");

    private Pais paisDeTeste(Long id, String nome, String sigla) {
        return Pais.builder().paisId(id).nomePais(nome).sigla(sigla).build();
    }

    @Test
    void lancaNotFoundQuandoNaoHaPaises() {
        when(paisRepository.findAll(ordemPorNome)).thenReturn(List.of());

        assertThatThrownBy(() -> paisService.listarPais())
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Nenhum país");
    }

    @Test
    void pedeOrdenacaoPorNomeEDevolveOsPaises() {
        List<Pais> paises = List.of(
                paisDeTeste(2L, "Argentina", "ARG"),
                paisDeTeste(1L, "Brasil", "BRA"));
        when(paisRepository.findAll(ordemPorNome)).thenReturn(paises);

        List<Pais> resultado = paisService.listarPais();

        assertThat(resultado).containsExactlyElementsOf(paises);
    }

    @Test
    void devolvePaisQuandoExiste() {
        Pais pais = paisDeTeste(1L, "Brasil", "BRA");
        when(paisRepository.findById(1L)).thenReturn(Optional.of(pais));

        assertThat(paisService.buscarPorId(1L)).isSameAs(pais);
    }

    @Test
    void lancaNotFoundQuandoPaisNaoExiste() {
        when(paisRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paisService.buscarPorId(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("99");
    }
}
