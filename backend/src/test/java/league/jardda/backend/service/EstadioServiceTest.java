package league.jardda.backend.service;

import league.jardda.backend.DTO.EstadioDTO;
import league.jardda.backend.entity.Cidade;
import league.jardda.backend.entity.Clube;
import league.jardda.backend.entity.Estadio;
import league.jardda.backend.exception.NotFoundException;
import league.jardda.backend.repository.EstadioRepository;
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
public class EstadioServiceTest {
    @Mock
    EstadioRepository estadioRepository;
    @Mock
    ParameterValidator parameterValidator;
    @InjectMocks
    EstadioService estadioService;

    private Estadio estadioDeTeste(Clube clube) {
        Cidade cidade = Cidade.builder().cidadeId(1L).nomeCidade("Barcelona").build();

        return Estadio.builder()
                .estadioId(1L)
                .nomeEstadio("Camp Nou")
                .cidadeEstadio(cidade)
                .clube(clube)
                .build();
    }

    @Test
    void lancaNotFoundQuandoNaoHaEstadios() {
        when(estadioRepository.filtrar(null, null, null, null)).thenReturn(List.of());

        assertThatThrownBy(() -> estadioService.listarEstadio(null, null, null, null))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Nenhum estádio");
    }

    @Test
    void devolveEstadiosConvertidosParaDto() {
        Clube clube = Clube.builder().clubeId(10L).nomeClube("FC Barcelona").build();
        when(estadioRepository.filtrar(null, null, null, null))
                .thenReturn(List.of(estadioDeTeste(clube)));

        List<EstadioDTO> resultado = estadioService.listarEstadio(null, null, null, null);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.getFirst().nome()).isEqualTo("Camp Nou");
        assertThat(resultado.getFirst().clubeId()).isEqualTo(10L);
        assertThat(resultado.getFirst().clubeNome()).isEqualTo("FC Barcelona");
    }

    @Test
    void devolveDtoSemClubeQuandoEstadioNaoTemClube() {
        when(estadioRepository.filtrar(null, null, null, null))
                .thenReturn(List.of(estadioDeTeste(null)));

        List<EstadioDTO> resultado = estadioService.listarEstadio(null, null, null, null);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.getFirst().nome()).isEqualTo("Camp Nou");
        assertThat(resultado.getFirst().clubeId()).isNull();
        assertThat(resultado.getFirst().clubeNome()).isNull();
    }

    @Test
    void naoConsultaRepositoryQuandoCidadeNaoExiste() {
        doThrow(new NotFoundException("Cidade com id 99 não encontrada."))
                .when(parameterValidator).validarCidade(99L);

        assertThatThrownBy(() -> estadioService.listarEstadio(null, null, 99L, null))
                .isInstanceOf(NotFoundException.class);

        verifyNoInteractions(estadioRepository);
    }

    @Test
    void naoConsultaRepositoryQuandoClubeNaoExiste() {
        doThrow(new NotFoundException("Clube com id 99 não encontrado."))
                .when(parameterValidator).validarClube(99L);

        assertThatThrownBy(() -> estadioService.listarEstadio(null, null, null, 99L))
                .isInstanceOf(NotFoundException.class);

        verifyNoInteractions(estadioRepository);
    }

    @Test
    void buscarPorIdDevolveDtoQuandoEstadioExiste() {
        when(estadioRepository.findById(1L)).thenReturn(Optional.of(estadioDeTeste(null)));

        EstadioDTO dto = estadioService.buscarPorId(1L);

        assertThat(dto.nome()).isEqualTo("Camp Nou");
        assertThat(dto.clubeId()).isNull();
    }

    @Test
    void buscarPorIdLancaNotFoundQuandoEstadioNaoExiste() {
        when(estadioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> estadioService.buscarPorId(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("99");
    }
}