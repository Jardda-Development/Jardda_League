# Jardda League

Banco de dados relacional de futebol (clubes, ligas, estádios, jogadores, técnicos, contratos e partidas), modelado em PostgreSQL, com controle de acesso por roles. O modelo também foi convertido para MongoDB, por pedido do professor da disciplina, apenas como exercício acadêmico.

> **Aviso sobre os dados:** os nomes de **clubes, ligas e estádios** são reais. **Jogadores, técnicos, contratos, salários, partidas e estatísticas são fictícios**, criados apenas para testar o modelo. Nenhum dado deste repositório representa fatos reais sobre pessoas ou eventos reais.

## Problema

O projeto nasceu de um trabalho acadêmico de banco de dados. Eu e meu companheiro de equipe reescrevemos o modelo do zero para praticar modelagem relacional de ponta a ponta: integridade referencial, regras de negócio no próprio banco, modelagem temporal (contratos e participação em ligas) e controle de acesso. O objetivo é portfólio na área de Engenharia e Arquitetura de Dados, com custo zero de infraestrutura.

## Dados

O schema `jardda_league` tem 12 tabelas:

- **Localização:** `pais`, `cidade`
- **Entidades:** `clube`, `liga`, `estadio`, `jogador`, `tecnico`
- **Vínculos com período:** `clube_liga`, `clube_jogador`, `clube_tecnico`
- **Jogos:** `partida`, `estatistica_partida`

A carga de teste tem 4 países, 9 cidades, 12 clubes, 4 ligas, 11 estádios, 12 técnicos, 36 jogadores, 12 partidas e 12 registros de estatística.

O diagrama entidade-relacionamento e o minimundo estão em [`docs/`](docs/).

## Arquitetura

```mermaid
flowchart LR
    C[Postman / cliente] --> API[API Spring Boot]
    API -->|login de leitura| PG[(PostgreSQL - Neon)]
    PG -. modelo convertido .-> MG[(MongoDB - Atlas)]
```

- **PostgreSQL (Neon):** banco principal.
- **API Spring Boot:** consulta o banco (somente leitura).
- **MongoDB (Atlas):** modelo convertido por requisição do professor, apenas para fins de aprendizado acadêmico. Não é usado pela API nem no dia a dia do banco.

## Stack

- **PostgreSQL** no **Neon** (plano gratuito). Migrei do Supabase por limitações do plano gratuito, além de maior grau de aprendizado através do Neon.
- **MongoDB** no **Atlas** (plano gratuito), usado só para a conversão do modelo pedida pelo professor.
- **Spring Boot**, usado como API de consulta (somente leitura).
- **Postman** para testar as requisições.

## Como rodar o banco

Pré-requisitos: uma conta no Neon, um cliente SQL (ou o editor SQL do próprio Neon) e, para a parte do Mongo, uma conta no Atlas.

1. Crie um projeto no Neon e anote a string de conexão do `neondb_owner`.
2. Execute o script de criação do schema e das tabelas.
3. Execute o script de carga (`dados_jardda_league.sql`), que cadastra países, cidades, clubes, ligas, estádios, técnicos, jogadores, contratos, partidas e estatísticas. Ele usa ids numéricos fixos, então deve rodar **uma única vez**, em um schema recém-criado. Para repetir, esvazie as tabelas e reinicie as sequências (`TRUNCATE ... RESTART IDENTITY CASCADE`) antes.
4. Crie os dois roles de login, num arquivo **local**, fora do repositório, com senhas fortes:

   ```sql
   CREATE ROLE normal_user WITH LOGIN PASSWORD '<senha-forte>';
   CREATE ROLE admin WITH LOGIN PASSWORD '<senha-forte>';
   ```

5. Execute o script de políticas de acesso como `neondb_owner`.
6. (Opcional, não é necessário para o funcionamento do projeto) Mongo: crie as coleções com os arquivos em /mongodb/collections (pelo `mongosh` ou pelo Compass).

## API (Spring Boot)

API de consulta sobre o schema `jardda_league`. A API é somente de leitura: só tem rotas `GET`.

### Requisitos

- Java 21 ou superior
- Maven (se o projeto tiver o wrapper, use `./mvnw` em vez do `mvn` instalado, para evitar conflito de versão)
- O banco criado conforme a seção anterior

### Variáveis de ambiente

A conexão é montada a partir de cinco variáveis:

| Variável | Exemplo |
|----------|---------|
| `DB_HOST` | host fornecido pelo Neon |
| `DB_PORT` | `5432` |
| `DB_NAME` | nome do banco |
| `DB_USER` | login do banco (o `normal_user` entra quando as políticas de acesso estiverem aplicadas) |
| `DB_PASS` | senha do role |

Crie um arquivo `.env` na raiz, **fora do controle de versão** (confirme que está no `.gitignore`):

```
DB_HOST=
DB_PORT=5432
DB_NAME=
DB_USER=
DB_PASS=
```

O Spring não lê o `.env` sozinho. Carregue as variáveis antes de subir a API:

- **Terminal:** `set -a; source .env; set +a`
- **IntelliJ:** em *Run > Edit Configurations > Environment variables*, cole as cinco variáveis.

Se a conexão com o Neon falhar, confira se o banco exige SSL e, se for o caso, acrescente `?sslmode=require` ao nome do banco na URL.

### Como rodar

```bash
set -a; source .env; set +a
./mvnw spring-boot:run
```

A API sobe em `http://localhost:8080`.

### Rotas

Todos os filtros são opcionais e combinados com `AND`. Texto em `nome` e `estadio` é "contém", sem diferenciar maiúsculas. `pais`, `clube`, `clubeCasa` e `clubeFora` comparam o nome exato. Rotas marcadas com `[paginada]` devolvem páginas (veja abaixo).

```
GET /cidades                      nome, pais, paisId
GET /cidades/{id}
GET /cidades/{id}/estadios        nome, clube, clubeId
GET /cidades/{id}/clubes          nome

GET /clubes                       nome, pais, cidadeId, paisId
GET /clubes/{id}
GET /clubes/{id}/estadios         nome
GET /clubes/{id}/partidas         rodada, ligaId, estadio, estadioId   [paginada]
GET /clubes/{id}/elenco           apenasAtivos
GET /clubes/{id}/tecnicos         apenasAtivos
GET /clubes/{id}/ligas            apenasAtivos

GET /estadios                     nome, clube, cidadeId, clubeId
GET /estadios/{id}
GET /estadios/{id}/partidas       clubeCasa, clubeFora, rodada, ligaId   [paginada]

GET /jogadores                    nome, pais, paisId   [paginada]
GET /jogadores/{id}
GET /jogadores/{id}/contratos     apenasAtivos
GET /jogadores/{id}/clube-atual

GET /ligas                        nome, pais, paisId
GET /ligas/{id}
GET /ligas/{id}/partidas          clubeCasa, clubeFora, rodada, estadio, estadioId   [paginada]
GET /ligas/{id}/clubes            apenasAtivos

GET /paises
GET /paises/{id}
GET /paises/{id}/cidades          nome
GET /paises/{id}/ligas            nome
GET /paises/{id}/clubes           nome, cidadeId
GET /paises/{id}/jogadores        nome   [paginada]
GET /paises/{id}/tecnicos         nome   [paginada]

GET /partidas                     clubeCasa, clubeFora, rodada, ligaId, estadio, estadioId   [paginada]
GET /partidas/{id}
GET /partidas/{id}/estatisticas

GET /tecnicos                     nome, pais, paisId   [paginada]
GET /tecnicos/{id}
GET /tecnicos/{id}/contratos      apenasAtivos
```

`apenasAtivos=true` devolve só os vínculos sem data de término (contrato ou participação em liga em andamento).

### Paginação

As rotas de partidas, jogadores e técnicos aceitam `?page=0&size=10`. O padrão é 10 itens por página e o máximo é 30. A ordem é fixa (partidas por data, jogadores e técnicos por nome) e o parâmetro `sort` é ignorado.

```json
{
  "itens": [ { "id": 1, "clubeCasa": "...", "clubeFora": "...", "rodada": 1 } ],
  "pagina": 0,
  "tamanho": 10,
  "totalElementos": 12,
  "totalPaginas": 2
}
```

Uma página além do fim devolve `itens` vazio com status 200.

### Erros

Todo erro, inclusive os do próprio Spring, sai no mesmo formato:

```json
{
  "title": "Recurso não encontrado",
  "status": 404,
  "details": "Clube com id 99 não encontrado",
  "timestamp": "2026-11-15T23:10:00"
}
```

- **404:** o id não existe, o id de um filtro não existe (ex.: `ligaId` inválido) ou nenhum resultado bate com os filtros.
- **400:** parâmetro com tipo errado (ex.: `/clubes/abc`).
- **500:** erro inesperado, com mensagem genérica. O detalhe real fica só no log do servidor.

### Testando com o Postman

A coleção `jardda-league.postman_collection.json` tem todas as rotas, com os filtros opcionais desmarcados. Importe o arquivo, confira a variável `baseUrl` (`http://localhost:8080`) e a variável `id` (um id que exista no banco) e use o *Run collection* para chamar todas de uma vez. Cada requisição falha se a resposta for 5xx.

### Próximos passos

- Spring Security (usuários) e políticas de acesso por linha (RLS) no banco.
- Testes automatizados (services, repositories e controllers).

## Políticas de acesso

O dono das tabelas é o `neondb_owner`, usado só para manter a estrutura. A aplicação usa dois logins:

| Role | Permissão |
|------|-----------|
| `normal_user` | Somente leitura (`SELECT`) em todas as tabelas |
| `admin` | Todos os privilégios nas tabelas e nas sequences |

Pontos importantes da implementação:

- É preciso `USAGE` no schema antes de qualquer permissão nas tabelas.
- O `admin` precisa de permissão nas **sequences** dos campos `SERIAL`, senão o `INSERT` falha ao gerar o id.
- `ALTER DEFAULT PRIVILEGES` faz tabelas criadas no futuro herdarem as mesmas permissões.

**Por que dois logins:** são só duas strings de conexão, sem configurar troca de role a cada transação (a API só precisa da de leitura). A decisão considerou 5 alternativas (dois logins, troca de role por transação, variável de sessão, autorização só na aplicação e Neon Data API com JWT) e priorizou simplicidade para quem mantém a API.

### Testes

Testados manualmente, conectado como cada role:

| Role | Operação | Esperado |
|------|----------|----------|
| `normal_user` | `SELECT` em qualquer tabela | Permitido |
| `normal_user` | `INSERT`, `UPDATE`, `DELETE` | Negado (`permission denied`) |
| `admin` | `SELECT` | Permitido |
| `admin` | `INSERT` (com id automático) | Permitido |
| `admin` | `UPDATE`, `DELETE` | Permitido |

## Decisões de modelagem

- **Mandante:** `time_1` é sempre o mandante, e `time_2` o visitante, com `CHECK` para que sejam clubes diferentes.
- **País derivado via cidade:** clube e estádio não têm coluna de país. Ele vem da cidade, para ter uma única fonte de verdade.
- **Contrato com previsão e término:** a previsão (expectativa na assinatura) é separada do término real, que só é preenchido quando acontece. Contrato ativo é o que não tem término.
- **Um contrato ativo por pessoa:** garantido por índice único parcial (`WHERE termino IS NULL`) em jogador e técnico.
- **Clube e liga com período:** `clube_liga` tem entrada e saída, para guardar o histórico.
- **Lesão como booleano:** em vez de um departamento médico, `lesionado` fica no jogador (existem jogadores lesionados sem clube).
- **Estatística em tabela separada:** `estatistica_partida` é 1:1 com `partida`. Escolhi separar para manter `partida` enxuta, aceitando o custo de um `JOIN`.
- **`CHECK` com lista fixa em vez de `ENUM`:** mais simples de alterar.
- **Estádio com clube opcional:** estádios podem ser compartilhados.
- **Datas validadas no banco:** `CHECK` entre 1900-01-01 e a data atual.
- **Salário:** `NUMERIC(10,2)`, valor anual em dólares.

## PostgreSQL x MongoDB

A conversão para MongoDB foi uma requisição do professor da disciplina e foi feita somente para aprendizado acadêmico: ela não tem efeito real sobre o banco nem é usada no dia a dia do projeto, que roda inteiramente em PostgreSQL. As 12 tabelas viraram 6 coleções (`clubes`, `ligas`, `estadios`, `tecnicos`, `jogadores`, `partidas`). A descrição campo a campo está no arquivo `modelo_mongodb.md`.

**O que foi embutido**
- País e cidade viram subdocumentos.
- Contratos ficam como um array dentro de jogador e técnico.
- O histórico de ligas fica como um array dentro do clube.
- A estatística vira um subdocumento da partida (era 1:1).

**O que ficou como referência:** onde um documento aponta para outro (por exemplo, partida para clube), guardo o id e uma cópia do nome. Isso evita junções nas leituras comuns, ao custo de a cópia poder ficar desatualizada.

**O que o MongoDB não garante e o PostgreSQL garantia**
- Chaves estrangeiras (integridade entre coleções).
- `CHECK` de intervalo de datas, `termino > inicio` e `time_1 <> time_2`.
- Um único contrato ativo por pessoa (o índice parcial não se aplica a arrays embutidos).

Essas regras passam a ser responsabilidade da aplicação.

**Minha avaliação:** o domínio de futebol é fortemente relacional (vínculos N:N com período, partidas que referenciam dois clubes, classificação calculada por agregação). Por isso, o PostgreSQL é a escolha principal e a única base realmente usada. A conversão serviu só para comparar, na prática acadêmica, as duas formas de modelar. Não há carga real no Mongo: apenas as coleções e, opcionalmente, dados de exemplo fictícios.

## Aprendizados

- **Erros recorrentes de DDL:** vírgulas faltando, `FOREIGN KEY` sem parênteses, `CHECK` sem o nome da coluna, datas sem aspas. O hábito que ajuda é rodar o script do zero num schema de teste a cada mudança.
- **Transações:** depois de um erro dentro de uma transação, o Postgres ignora tudo até o `ROLLBACK` (erro 25P02).
- **Cargas e ids:** a sequência do `SERIAL` não volta atrás quando uma transação falha, então ids digitados à mão na carga só funcionam em um schema recém-criado. Para repetir uma carga, é preciso esvaziar as tabelas e reiniciar as sequências.
- **Permissões em camadas:** `USAGE` no schema, permissão na tabela e permissão na sequence são coisas distintas.
- **Escopo:** cortar a frente de mídia e congelar o schema foi o que permitiu publicar.
- **Modelagem em documentos:** escolher o que embutir e o que referenciar é uma decisão sobre como os dados serão lidos.

## Uso de IA

Usei o Claude (Anthropic) e o ChatGPT (OpenIA) como ferramentas de **consulta e de melhoria da arquitetura**: revisão do schema, discussão das alternativas de políticas de acesso e dos trade-offs entre PostgreSQL e MongoDB, montagem deste README e explicação de erros. A IA também gerou os dados utilizados no banco, devido ao tamanho do escopo do banco, além de auxiliar na construção dos scripts do MongoDB. As decisões de escopo e de modelagem, a execução dos scripts e os testes foram feitos pela equipe.
