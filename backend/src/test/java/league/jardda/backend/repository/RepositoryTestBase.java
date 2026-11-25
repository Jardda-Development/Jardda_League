package league.jardda.backend.repository;

import league.jardda.backend.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.math.BigDecimal;
import java.time.LocalDate;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
abstract class RepositoryTestBase {
    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18")
            .withInitScript("criacao_schema_e_tabelas.sql");

    @Autowired
    protected PaisRepository paisRepository;

    @Autowired
    protected CidadeRepository cidadeRepository;

    @Autowired
    protected ClubeRepository clubeRepository;

    @Autowired
    protected  LigaRepository ligaRepository;

    @Autowired
    protected EstadioRepository estadioRepository;

    @Autowired
    protected JogadorRepository jogadorRepository;

    @Autowired
    protected TecnicoRepository tecnicoRepository;

    @Autowired
    protected ClubeJogadorRepository clubeJogadorRepository;

    @Autowired
    protected ClubeTecnicoRepository clubeTecnicoRepository;

    @Autowired
    protected ClubeLigaRepository clubeLigaRepository;

    @Autowired
    protected PartidaRepository partidaRepository;

    protected Pais salvarPais(String nome, String sigla) {
        return paisRepository.save(Pais.builder()
                .nomePais(nome)
                .sigla(sigla)
                .build()
        );
    }

    protected Cidade salvarCidade(String nome, Pais pais) {
        return cidadeRepository.save(Cidade.builder()
                .nomeCidade(nome)
                .pais(pais)
                .build()
        );
    }

    protected Clube salvarClube(String nome, Cidade cidade) {
        return clubeRepository.save(Clube.builder()
                .nomeClube(nome)
                .cidade(cidade)
                .build()
        );
    }

    protected Liga salvarLiga(String nome, Pais pais) {
        return ligaRepository.save(Liga.builder()
                .nomeLiga(nome)
                .paisLiga(pais)
                .estiloLiga("pontos_corridos")
                .build()
        );
    }

    protected Estadio salvarEstadio(String nome, Cidade cidade, Clube clube) {
        return estadioRepository.save(Estadio.builder()
                .nomeEstadio(nome)
                .cidadeEstadio(cidade)
                .clube(clube)
                .build());
    }

    protected Jogador salvarJogador(String nome, Pais pais) {
        return jogadorRepository.save(Jogador.builder()
                .nomeJogador(nome)
                .paisJogador(pais)
                .aniversarioJogador(LocalDate.of(2000, 1, 1))
                .posicao("ATC")
                .lesionado(false)
                .build());
    }

    protected Tecnico salvarTecnico(String nome, Pais pais) {
        return tecnicoRepository.save(Tecnico.builder()
                .nomeTecnico(nome)
                .paisTecnico(pais)
                .aniversarioTecnico(LocalDate.of(1970, 1, 1))
                .build());
    }

    protected ClubeJogador salvarContratoJogador(Clube clube, Jogador jogador,
                                                 LocalDate inicio, LocalDate termino) {
        return clubeJogadorRepository.save(ClubeJogador.builder()
                .clube(clube)
                .jogador(jogador)
                .tipoContratoJogador("definitivo")
                .inicioContratoJogador(inicio)
                .previsaoContratoJogador(inicio.plusYears(3))
                .terminoContratoJogador(termino)
                .valorSalarioJogador(new BigDecimal("1000.00"))
                .build());
    }

    protected ClubeTecnico salvarContratoTecnico(Clube clube, Tecnico tecnico,
                                                 LocalDate inicio, LocalDate termino) {
        return clubeTecnicoRepository.save(ClubeTecnico.builder()
                .clube(clube)
                .tecnico(tecnico)
                .tipoContratoTecnico("definitivo")
                .inicioContratoTecnico(inicio)
                .previsaoContratoTecnico(inicio.plusYears(3))
                .terminoContratoTecnico(termino)
                .valorSalarioTecnico(new BigDecimal("1000.00"))
                .build());
    }

    protected ClubeLiga salvarParticipacao(Clube clube, Liga liga,
                                           LocalDate entrada, LocalDate saida) {
        return clubeLigaRepository.save(ClubeLiga.builder()
                .clube(clube)
                .liga(liga)
                .periodoEntrada(entrada)
                .periodoSaida(saida)
                .build());
    }

    protected Partida salvarPartida(Clube casa, Clube fora, Liga liga, Estadio estadio,
                                    Integer rodada, LocalDate data) {
        return partidaRepository.save(Partida.builder()
                .clube1(casa)
                .clube2(fora)
                .ligaPartida(liga)
                .estadioPartida(estadio)
                .rodadaPartida(rodada)
                .dataPartida(data)
                .publicoPartida(1000)
                .build());
    }
}
