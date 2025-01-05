# Sistema de Votação Cooperativa

## Descrição do Projeto
Este projeto é uma API REST para gerenciar sessões de votação em cooperativas. A aplicação permite criar pautas, abrir sessões de votação, registrar votos e obter resultados. Também inclui uma funcionalidade para validar CPFs e simular cenários de alta carga.

---

## 1. Pré-requisitos
Certifique-se de ter as seguintes ferramentas instaladas:

- **Java 17+**
- **Maven 3.6+**
- **PostgreSQL** ou outro banco de dados compatível
- **Git**

---

## 2. Configuração do Projeto

### 2.1 Clone o Repositório
Clone este repositório para sua máquina local:
```bash
git clone <url-do-repositorio>
cd sistema-votacao
```

### 2.2 Configurar o Banco de Dados

Crie um banco de dados no PostgreSQL ou outro banco que esteja utilizando. Exemplo:

CREATE DATABASE votacao;

Crie as tabelas necessárias para a aplicação com o seguinte script:
```bash

-- Criação da tabela de Pautas
CREATE TABLE pautas (
    id SERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL
);

-- Criação da tabela de Sessões de Votação
CREATE TABLE sessoes (
    id SERIAL PRIMARY KEY,
    pauta_id INT NOT NULL,
    duracao INT NOT NULL,
    status VARCHAR(50) NOT NULL,
    CONSTRAINT fk_pauta FOREIGN KEY (pauta_id) REFERENCES pautas(id)
);

-- Criação da tabela de Votos
CREATE TABLE votos (
    id SERIAL PRIMARY KEY,
    sessao_id INT NOT NULL,
    voto VARCHAR(50) NOT NULL,
    CONSTRAINT fk_sessao FOREIGN KEY (sessao_id) REFERENCES sessoes(id)
);

-- Criação da tabela de Log de Registros de Votação
CREATE TABLE log_registros (
    id SERIAL PRIMARY KEY,
    sessao_id INT NOT NULL,
    usuario_id INT NOT NULL,
    voto VARCHAR(50) NOT NULL,
    data_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sessao_log FOREIGN KEY (sessao_id) REFERENCES sessoes(id)
);

```

Em seguida, configure as credenciais do banco no arquivo application.properties:

```bash
spring.datasource.url=jdbc:postgresql://localhost:5432/votacao
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
```

## 2.3 Executar a Aplicação

Para rodar a aplicação, execute o seguinte comando:

mvn spring-boot:run

## 3. Testando a API
### 3.1 Endpoints Disponíveis
### 3.1.1 Criar Pauta

Este endpoint cria uma nova pauta para votação.
```bash

Requisição:

curl -X POST http://localhost:8081/api/pautas -H "Content-Type: application/json" -d '{"titulo": "Nova Pauta"}'

Resposta:

{
  "id": 1,
  "titulo": "Nova Pauta"
}

```

### 3.1.2 Abrir Sessão de Votação

Este endpoint abre uma sessão de votação associada a uma pauta, com duração configurável (em minutos).

Requisição:

curl -X POST http://localhost:8081/api/sessoes -H "Content-Type: application/json" -d '{"pautaId": 1, "duracao": 5}'
```bash
Resposta:

{
  "id": 1,
  "pautaId": 1,
  "duracao": 5,
  "status": "ABERTA"
}
```

###  3.1.3 Registrar Voto

Este endpoint registra um voto para uma sessão aberta, com a possibilidade de voto positivo ou negativo.

Requisição:
```bash
curl -X POST http://localhost:8081/api/votos -H "Content-Type: application/json" -d '{"sessaoId": 1, "voto": "SIM"}'

Resposta:

{
  "id": 1,
  "sessaoId": 1,
  "voto": "SIM"
}

```

###  3.1.4 Validar CPF

Este endpoint valida um CPF, retornando se o usuário pode ou não votar.

Requisição:

```bash

curl -X GET http://localhost:8081/api/cpf/validate/12345678901

Resposta (CPF válido):

{
  "status": "ABLE_TO_VOTE"
}

Resposta (CPF inválido - 404):

{
  "status": "UNABLE_TO_VOTE"
}

```

## 4. Testes de Performance
### 4.1 Preparar Gatling para Testes de Carga
### 4.1.1 Adicionar Dependências no pom.xml

Adicione a dependência do Gatling ao arquivo pom.xml:

<dependency>
  <groupId>io.gatling</groupId>
  <artifactId>gatling-core</artifactId>
  <version>3.7.0</version>
  <scope>test</scope>
</dependency>

### 4.1.2 Criar o Teste de Performance com Gatling

Crie um arquivo de teste de carga com Gatling em src/test/scala/PerformanceTest.scala:


```bash

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class PerformanceTest extends Simulation {

  val httpProtocol = http
    .baseUrl("http://localhost:8081")
    .acceptHeader("application/json")

  val scn = scenario("Criar Pauta e Registrar Voto")
    .exec(http("Criar Pauta")
      .post("/api/pautas")
      .body(StringBody("""{"titulo": "Pauta de Teste"}""")).asJson
      .check(status.is(200)))
    .exec(http("Abrir Sessão de Votação")
      .post("/api/sessoes")
      .body(StringBody("""{"pautaId": 1, "duracao": 5}""")).asJson
      .check(status.is(200)))
    .exec(http("Registrar Voto")
      .post("/api/votos")
      .body(StringBody("""{"sessaoId": 1, "voto": "SIM"}""")).asJson
      .check(status.is(200)))

  setUp(
    scn.inject(atOnceUsers(100)).protocols(httpProtocol)
  )
}

```

### 4.1.3 Executar Teste de Carga

Execute o teste de carga com o Gatling:

mvn test

## 5. Tarefa Bônus 1 - Integração com Sistemas Externos
### 5.1 Criar uma Facade/Client Fake para Validação de CPF

Para esta tarefa, você irá criar um serviço de validação de CPF que simula a resposta com base em geradores de CPF. A aplicação irá retornar se o CPF pode ou não votar.

Requisição para validar CPF:

```bash
curl -X GET http://localhost:8081/api/cpf/validate/12345678901

Resposta quando o CPF é válido:

{
  "status": "ABLE_TO_VOTE"
}

Resposta quando o CPF é inválido:

{
  "status": "UNABLE_TO_VOTE"
}
```