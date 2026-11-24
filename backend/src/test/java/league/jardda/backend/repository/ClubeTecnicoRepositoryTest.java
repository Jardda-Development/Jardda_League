package league.jardda.backend.repository;

import league.jardda.backend.entity.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ClubeTecnicoRepositoryTest extends RepositoryTestBase {

    private static LocalDate ano(int ano) {
        return LocalDate.of(ano, 1, 1);
    }

    private Clube montarHistoricoDoClube() {
        Pais brasil = salvarPais("Brasil", "BRA");
        Cidade cidade = salvarCidade("Sao Paulo", brasil);
        Clube clube = salvarClube("Corinthians", cidade);
        Clube outroClube = salvarClube("Palmeiras", cidade);

        Tecnico antigo = salvarTecnico("Antigo", brasil);
        Tecnico medio = salvarTecnico("Medio", brasil);
        Tecnico recente = salvarTecnico("Recente", brasil);
        Tecnico deOutroClube = salvarTecnico("Outro", brasil);

        salvarContratoTecnico(clube, antigo, ano(2018), ano(2020));
        salvarContratoTecnico(clube, medio, ano(2020), ano(2022));
        salvarContratoTecnico(clube, recente, ano(2022), null);
        salvarContratoTecnico(outroClube, deOutroClube, ano(2022), null);
        return clube;
    }

    private Tecnico montarCarreiraDoTecnico() {
        Pais brasil = salvarPais("Brasil", "BRA");
        Cidade cidade = salvarCidade("Sao Paulo", brasil);
        Clube antigoClube = salvarClube("Santos", cidade);
        Clube atualClube = salvarClube("Corinthians", cidade);
        Clube outroClube = salvarClube("Palmeiras", cidade);

        Tecnico tecnico = salvarTecnico("Mestre", brasil);
        Tecnico outro = salvarTecnico("Outro", brasil);

        salvarContratoTecnico(antigoClube, tecnico, ano(2015), ano(2018));
        salvarContratoTecnico(atualClube, tecnico, ano(2018), null);
        salvarContratoTecnico(outroClube, outro, ano(2019), null);
        return tecnico;
    }

    @Test
    void buscarPorClube_todos_ordenaPorInicioDecrescenteEIgnoraOutrosClubes() {
        Clube clube = montarHistoricoDoClube();

        List<ClubeTecnico> resultado = clubeTecnicoRepository.buscarPorClube(clube.getClubeId(), false);

        assertThat(resultado).extracting(ct -> ct.getTecnico().getNomeTecnico())
                .containsExactly("Recente", "Medio", "Antigo");
    }

    @Test
    void buscarPorClube_apenasAtivos_soOTecnicoAtual() {
        Clube clube = montarHistoricoDoClube();

        List<ClubeTecnico> resultado = clubeTecnicoRepository.buscarPorClube(clube.getClubeId(), true);

        assertThat(resultado).extracting(ct -> ct.getTecnico().getNomeTecnico())
                .containsExactly("Recente");
    }

    @Test
    void buscarPorTecnico_todos_ordenaPorInicioDecrescente() {
        Tecnico tecnico = montarCarreiraDoTecnico();

        List<ClubeTecnico> resultado = clubeTecnicoRepository.buscarPorTecnico(tecnico.getTecnicoId(), false);

        assertThat(resultado).extracting(ct -> ct.getClube().getNomeClube())
                .containsExactly("Corinthians", "Santos");
    }

    @Test
    void buscarPorTecnico_apenasAtivos_soOContratoAtual() {
        Tecnico tecnico = montarCarreiraDoTecnico();

        List<ClubeTecnico> resultado = clubeTecnicoRepository.buscarPorTecnico(tecnico.getTecnicoId(), true);

        assertThat(resultado).extracting(ct -> ct.getClube().getNomeClube())
                .containsExactly("Corinthians");
    }

    @Test
    void buscarPorClube_semContratos_devolveListaVazia() {
        Clube clube = salvarClube("Corinthians", salvarCidade("Sao Paulo", salvarPais("Brasil", "BRA")));

        List<ClubeTecnico> resultado = clubeTecnicoRepository.buscarPorClube(clube.getClubeId(), false);

        assertThat(resultado).isEmpty();
    }
}
