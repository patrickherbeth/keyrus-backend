# Sistema de Votação Cooperativa

## Descrição do Projeto
Este projeto é uma API REST para gerenciar contas. A aplicação permite criar pautas, abrir sessões de votação, registrar votos e obter resultados. Também inclui uma funcionalidade para validar CPFs e simular cenários de alta carga.

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
git clone https://github.com/patrickherbeth/keyrus-backend
cd keyrus-backend
```

### 2.2 Configurar o Banco de Dados

Crie um banco de dados no PostgreSQL ou outro banco que esteja utilizando. Exemplo:

CREATE DATABASE demo;

Crie as tabelas necessárias para a aplicação com o seguinte script:
```bash

-- Criação da tabela de usuários
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Criação da tabela de dívidas
CREATE TABLE debts (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    amount NUMERIC(10, 2) NOT NULL,
    due_date DATE NOT NULL,
    status VARCHAR(20) CHECK (status IN ('Pendente', 'Pago', 'Atrasado')) NOT NULL,
    observations TEXT,
    user_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);


```