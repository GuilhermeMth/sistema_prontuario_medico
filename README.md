# Sistema de Gerenciamento de Pacientes e Exames

Projeto final da disciplina **Programação Orientada a Objetos (POO)** – Unidade II
Curso: **Tecnologia em Sistemas para Internet**
Instituição: **IFPE – Campus Igarassu**

> **Professor:** Gustavo Nóbrega
> **Aluno:** Guilherme Matheus

---

## 📋 Descrição Geral

Este sistema tem como objetivo o gerenciamento de pacientes e exames médicos em ambiente console, utilizando Java e persistência em banco de dados MySQL com o padrão de projeto DAO. A aplicação oferece funcionalidades de cadastro, listagem, edição e remoção (CRUD), organizadas em módulos e seguindo boas práticas de orientação a objetos.

---

## 📦 Funcionalidades

### 🔹 Módulo de Pacientes

* **Cadastrar Paciente** (`RF01`): Insere novo paciente com nome e CPF.
* **Listar Pacientes** (`RF02`): Exibe todos os pacientes cadastrados em uma tabela.
* **Editar Paciente** (`RF03`): Permite modificar nome e/ou CPF.
* **Excluir Paciente** (`RF04`): Remove um paciente do sistema (com verificação de integridade relacional).
* **Associar Exames ao Paciente** (`RF05`): Vincula exames existentes ou novos a um paciente.

### 🔹 Módulo de Exames

* **Cadastrar Exame** (`RF06`): Adiciona um novo exame com descrição, data e associação a um paciente.
* **Listar Exames** (`RF07`): Exibe todos os exames registrados, agrupados por paciente.
* **Editar Exame** (`RF08`): Permite modificar descrição, data ou paciente associado.
* **Excluir Exame** (`RF09`): Remove um exame do sistema.

### 🔹 Módulo de Sistema

* **Inicialização do Banco de Dados** (`RF10`): Cria o banco e as tabelas se ainda não existirem.
* **Leitura de Arquivo de Configuração** (`RF11`): Carrega as informações de conexão via arquivo `.txt` com pares chave-valor:

  ```
  DBNAME=sistema
  DBUSER=root
  DBPASSWORD=senha123
  DBADDRESS=localhost
  DBPORT=3306
  ```

---

## 🧱 Estrutura do Banco de Dados

### Tabela: `PACIENTES`

| Campo | Tipo         | Restrições                   |
| ----- | ------------ | ---------------------------- |
| id    | BIGINT       | PRIMARY KEY, AUTO\_INCREMENT |
| cpf   | VARCHAR(14)  | UNIQUE, NOT NULL             |
| nome  | VARCHAR(255) | NOT NULL                     |

### Tabela: `EXAMES`

| Campo        | Tipo         | Restrições                             |
| ------------ | ------------ | -------------------------------------- |
| id           | BIGINT       | PRIMARY KEY, AUTO\_INCREMENT           |
| descricao    | VARCHAR(255) | NOT NULL                               |
| data\_exame  | DATE         | NOT NULL                               |
| paciente\_id | BIGINT       | FOREIGN KEY (`PACIENTES.id`), NOT NULL |

---

## 🔧 Requisitos Técnicos

### Requisitos Funcionais

* CRUD completo para pacientes e exames.
* Relação 1\:N entre pacientes e exames.
* Leitura de configuração via arquivo externo.
* Banco é instanciado automaticamente caso não exista.

### Requisitos Não Funcionais

* Banco de dados MySQL.
* Interface via Java Console.
* Dependências compatíveis com licenças livres (GNU/Apache).
* Aplicação empacotada como `.jar`.

---

## 📦 Empacotamento e Execução

### 1. Compilar o projeto:

```bash
javac -d bin src/**/*.java
```

### 2. Criar `MANIFEST.MF` com a classe principal:

```txt
Main-Class: br.com.projeto.Main
Class-Path: lib/mysql-connector-java-8.0.xx.jar
```

### 3. Empacotar em `.jar`:

```bash
jar cvfm sistema.jar MANIFEST.MF -C bin .
```

### 4. Executar:

```bash
java -jar sistema.jar
```

---

## ✅ Casos de Uso Atendidos

* Cadastrar, editar, listar e remover pacientes e exames.
* Associar exames a pacientes.
* Remover paciente e também seus exames (com integridade relacional).
* Interface em console clara e interativa.
* Arquivo `.jar` final para portabilidade entre plataformas.

---

## 🔗 Referências

* [Data Access Object (DAO) Design Pattern](https://www.oracle.com/java/technologies/data-access-object.html)
* [Java Archive (JAR) Files](https://docs.oracle.com/javase/tutorial/deployment/jar/)

---

## 📥 Download

* [Download SistemaProntuario.jar](https://drive.google.com/drive/folders/1aNrcBuIQI0V4pr3eNylN04yKAWpCYoxZ?usp=drive_link)
