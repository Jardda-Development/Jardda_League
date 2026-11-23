package league.jardda.backend.repository;

import league.jardda.backend.entity.Cidade;
import league.jardda.backend.entity.Pais;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CidadeRepositoryTest extends RepositoryTestBase {
    @Autowired
    private CidadeRepository cidadeRepository;
    @Autowired
    private PaisRepository paisRepository;

    private Pais salvarPais(String nome, String sigla) {
        return paisRepository.save(Pais.builder().nomePais(nome).sigla(sigla).build());
    }

    private Cidade salvarCidade(String nome, Pais pais) {
        return cidadeRepository.save(Cidade.builder().nomeCidade(nome).pais(pais).build());
    }

    @Test
    void verificaSeItensSaoSalvosEOrdenadosDeFormaAlfabetica() {
        Pais brasil = salvarPais("Brasil", "BRA");
        Pais argentina = salvarPais("Argentina", "ARG");

        salvarCidade("Sao Paulo", brasil);
        salvarCidade("Buenos Aires", argentina);
        salvarCidade("Campina Grande", brasil);

        List<Cidade> resultado = cidadeRepository.filtrar(null, null, null);

        assertThat(resultado)
                .extracting(Cidade::getNomeCidade)
                .containsExactly("Buenos Aires", "Campina Grande", "Sao Paulo");
    }

    @Test
    void verificaSeBuscaPorNomeIgnoraDiferencaDeMaiusculasEMinusculas() {
        Pais brasil = salvarPais("Brasil", "BRA");

        salvarCidade("Sao Paulo", brasil);
        salvarCidade("Recife", brasil);
        salvarCidade("Sao Luis", brasil);

        List<Cidade> resultado = cidadeRepository.filtrar("SAO", null, null);

        assertThat(resultado)
                .extracting(Cidade::getNomeCidade)
                .containsExactly("Sao Luis", "Sao Paulo");
    }

    @Test
    void filtraCidadesAtravesDoNomeDoPais() {
        Pais guine = salvarPais("Guine", "GIN");
        Pais guineBissau = salvarPais("Guine-Bissau", "GNB");
        salvarCidade("Conacri", guine);
        salvarCidade("Bissau", guineBissau);

        List<Cidade> resultado = cidadeRepository.filtrar(null, "guine", null);

        assertThat(resultado)
                .extracting(Cidade::getNomeCidade)
                .containsExactly("Conacri");
    }

    @Test
    void filtraCidadesAtravesDoIdDoPais() {
        Pais brasil = salvarPais("Brasil", "BRA");
        Pais argentina = salvarPais("Argentina", "ARG");
        salvarCidade("Recife", brasil);
        salvarCidade("Rosario", argentina);

        List<Cidade> resultado = cidadeRepository.filtrar(null, null, brasil.getPaisId());

        assertThat(resultado).extracting(Cidade::getNomeCidade).containsExactly("Recife");
    }

    @Test
    void filtraCidadesAtravesDoNomeProximoEDoNomeDoPais() {
        Pais brasil = salvarPais("Brasil", "BRA");
        Pais argentina = salvarPais("Argentina", "ARG");
        salvarCidade("Sao Paulo", brasil);
        salvarCidade("Santa Fe", argentina);
        salvarCidade("Recife", brasil);

        List<Cidade> resultado = cidadeRepository.filtrar("SA", "brasil", null);

        assertThat(resultado).extracting(Cidade::getNomeCidade).containsExactly("Sao Paulo");
    }

    @Test
    void naoEncontraValor() {
        salvarCidade("Recife", salvarPais("Brasil", "BRA"));

        List<Cidade> resultado = cidadeRepository.filtrar("xyz", null, null);

        assertThat(resultado).isEmpty();
    }
}
