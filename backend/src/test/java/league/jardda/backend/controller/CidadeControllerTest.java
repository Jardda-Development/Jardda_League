package league.jardda.backend.controller;

import league.jardda.backend.DTO.CidadeDTO;
import league.jardda.backend.exception.NotFoundException;
import league.jardda.backend.service.CidadeService;
import league.jardda.backend.service.ClubeService;
import league.jardda.backend.service.EstadioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CidadeController.class)
class CidadeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CidadeService cidadeService;

    @MockitoBean
    private EstadioService estadioService;

    @MockitoBean
    private ClubeService clubeService;

    @Test
    void buscarPorId_existente_devolve200ComJson() throws Exception {
        CidadeDTO dto = new CidadeDTO(1L, "João Pessoa", "Brasil", "BRA");
        when(cidadeService.buscarPorId(1L)).thenReturn(dto);

        mockMvc.perform(get("/cidades/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("João Pessoa"))
                .andExpect(jsonPath("$.pais").value("Brasil"))
                .andExpect(jsonPath("$.siglaPais").value("BRA"));

    }

    @Test
    void listar_comFiltros_repassaParametrosAoService() throws Exception {
        CidadeDTO dto = new CidadeDTO(1L, "João Pessoa", "Brasil", "BRA");
        when(cidadeService.listarCidade("joao", null, 1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/cidades")
                        .param("nome", "joao")
                        .param("paisId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nome").value("João Pessoa"));

        verify(cidadeService).listarCidade("joao", null, 1L);
    }

    @Test
    void listarEstadiosDaCidade_comNome_chamaServiceComIdComoCidade() throws Exception {
        when(estadioService.listarEstadio("arena", null, 5L, null)).thenReturn(List.of());

        mockMvc.perform(get("/cidades/5/estadios").param("nome", "arena"))
                .andExpect(status().isOk());

        verify(estadioService).listarEstadio("arena", null, 5L, null);
    }

    @Test
    void listarClubesDaCidade_comNome_chamaServiceComIdComoCidade() throws Exception {
        when(clubeService.listarClube("botafogo", null, 5L, null)).thenReturn(List.of());

        mockMvc.perform(get("/cidades/5/clubes").param("nome", "botafogo"))
                .andExpect(status().isOk());

        verify(clubeService).listarClube("botafogo", null, 5L, null);
    }
}
