package league.jardda.backend.handler;

import league.jardda.backend.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RestExceptionHandlerTest.ControllerFalso.class)
@Import(RestExceptionHandlerTest.ControllerFalso.class)
class RestExceptionHandlerTest {

    @RestController
    static class ControllerFalso {
        @GetMapping("/teste/nao-encontrado")
        void naoEncontrado() {
            throw new NotFoundException("Recurso 99 não encontrado");
        }

        @GetMapping("/teste/erro")
        void erroInesperado() {
            throw new RuntimeException("falha no banco: tabela cidade");
        }

        @GetMapping("/teste/id/{id}")
        void comId(@PathVariable Long id) {
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    void notFoundException_devolve404NoFormatoPadrao() throws Exception {
        mockMvc.perform(get("/teste/nao-encontrado"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso não encontrado"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.details").value("Recurso 99 não encontrado"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void erroDoSpring_devolve400NoFormatoPadrao() throws Exception {
        mockMvc.perform(get("/teste/id/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.details").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void erroInesperado_devolve500SemVazarDetalhes() throws Exception {
        mockMvc.perform(get("/teste/erro"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.title").value("Erro interno"))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.details").value("Ocorreu um erro inesperado"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
