# API REST — Baozi Store

Atividade Prática da disciplina **Desenvolvimento Web Back-End** — Centro Universitário Internacional UNINTER.

API REST que informatiza o controle de **clientes**, **produtos** e **pedidos** da Baozi Store, uma pequena loja de pãozinho chinês.

> **Autor:** `NOME_DO_ALUNO` — RU `0000000`

---

## Tecnologias

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 17 |
| Framework | Spring Boot 3.3.5 |
| Web / MVC | Spring Web (Spring MVC) |
| Persistência | Spring Data JPA + Hibernate |
| Banco de dados | H2 (modo arquivo) |
| Validação | Jakarta Bean Validation |
| Build | Maven |
| Testes de API | Postman |

---

## Como executar

Pré-requisito: **JDK 17 ou superior** instalado.

```bash
# 1. clonar o repositório
git clone <URL-DO-SEU-REPOSITORIO>
cd baozi-store

# 2. subir a aplicação
mvn spring-boot:run
```

> Sem o Maven instalado? No macOS: `brew install maven`. Alternativamente, abra a pasta do
> projeto no IntelliJ IDEA ou no VS Code (extensão *Extension Pack for Java*) e execute a
> classe `BaoziStoreApiApplication` — a IDE baixa as dependências sozinha.

A API sobe em **http://localhost:8080**.

### Console do banco de dados

Com a aplicação rodando, acesse **http://localhost:8080/h2-console**:

| Campo | Valor |
|---|---|
| JDBC URL | `jdbc:h2:file:./data/baozidb` |
| User Name | `sa` |
| Password | *(deixar em branco)* |

### Testes automatizados

```bash
mvn test
```

---

## Estrutura do projeto

```
src/main/java/com/baozistore/api
├── BaoziStoreApiApplication.java   ponto de entrada
├── model/                          entidades JPA        (M do MVC)
│   ├── Cliente.java
│   ├── Produto.java
│   └── Pedido.java
├── repository/                     Spring Data JPA      (M do MVC)
│   ├── ClienteRepository.java
│   ├── ProdutoRepository.java
│   └── PedidoRepository.java
├── controller/                     endpoints REST       (C do MVC)
│   ├── ClienteController.java
│   ├── ProdutoController.java
│   └── PedidoController.java
├── dto/                            contratos JSON de Pedido
│   ├── PedidoRequest.java
│   └── PedidoResponse.java
└── exception/                      tratamento de erros em JSON
    ├── RecursoNaoEncontradoException.java
    ├── RegraDeNegocioException.java
    └── GlobalExceptionHandler.java
```

---

## Modelo de dados

```
CLIENTE                    PRODUTO
─────────────────          ─────────────────
id            Long         id          Long
nome          String       nome        String
clienteDesde  LocalDate    preco       BigDecimal
                           estoque     Boolean
       │ faz                      │ vendido em
       └──────────┐    ┌──────────┘
                  ▼    ▼
                 PEDIDO
                 ─────────────────
                 id          Long
                 clienteId   Long   → FK cliente(id)
                 produtoId   Long   → FK produto(id)
                 quantidade  Integer
```

---

## Endpoints

Base: `http://localhost:8080` · Formato: `application/json`

### Clientes — `/api/clientes`

| Método | Rota | Descrição | Sucesso |
|---|---|---|---|
| `POST` | `/api/clientes` | Cadastrar cliente | `201 Created` |
| `GET` | `/api/clientes` | Listar todos | `200 OK` |
| `GET` | `/api/clientes/{id}` | Consultar por ID | `200 OK` |
| `PUT` | `/api/clientes/{id}` | Atualizar | `200 OK` |
| `DELETE` | `/api/clientes/{id}` | Apagar | `204 No Content` |

```json
POST /api/clientes
{ "nome": "Maria Silva123456", "clienteDesde": "2026-08-20" }
```

### Produtos — `/api/produtos`

| Método | Rota | Descrição | Sucesso |
|---|---|---|---|
| `POST` | `/api/produtos` | Cadastrar produto | `201 Created` |
| `GET` | `/api/produtos` | Listar todos | `200 OK` |
| `GET` | `/api/produtos/{id}` | Consultar por ID | `200 OK` |
| `PUT` | `/api/produtos/{id}` | Atualizar | `200 OK` |
| `DELETE` | `/api/produtos/{id}` | Apagar | `204 No Content` |

```json
POST /api/produtos
{ "nome": "Baozi de Porco", "preco": 8.50, "estoque": true }
```

### Pedidos — `/api/pedidos`

| Método | Rota | Descrição | Sucesso |
|---|---|---|---|
| `POST` | `/api/pedidos` | Registrar pedido | `201 Created` |
| `GET` | `/api/pedidos` | Listar todos | `200 OK` |
| `GET` | `/api/pedidos/{id}` | Consultar por ID | `200 OK` |
| `PUT` | `/api/pedidos/{id}` | Atualizar | `200 OK` |
| `DELETE` | `/api/pedidos/{id}` | Apagar | `204 No Content` |

```json
POST /api/pedidos
{ "clienteId": 1, "produtoId": 1, "quantidade": 10 }
```

Resposta:

```json
{
  "id": 1,
  "clienteId": 1,
  "clienteNome": "Maria Silva123456",
  "produtoId": 1,
  "produtoNome": "Baozi de Porco",
  "precoUnitario": 8.50,
  "quantidade": 10,
  "valorTotal": 85.00
}
```

---

## Regras de negócio

| # | Regra | Resposta se violada |
|---|---|---|
| RN1 | A quantidade do pedido deve ser maior que zero | `400 Bad Request` |
| RN2 | O preço do produto deve ser maior que zero | `400 Bad Request` |
| RN3 | O pedido só aceita cliente e produto existentes | `404 Not Found` |
| RN4 | O produto precisa estar em estoque para ser pedido | `409 Conflict` |
| RN5 | Cliente ou produto com pedidos vinculados não pode ser excluído | `409 Conflict` |
| RN6 | `clienteDesde` ausente assume a data atual | — |

Todos os erros são devolvidos em JSON, num formato único:

```json
{
  "timestamp": "2026-08-20T14:32:10.123",
  "status": 404,
  "erro": "Not Found",
  "mensagem": "Cliente de id 99 nao encontrado(a)",
  "caminho": "/api/clientes/99"
}
```

---

## Collection do Postman

O arquivo `postman/BaoziStore.postman_collection.json` na raiz do repositório pode ser importado no Postman (**Import → File**) com todas as requisições prontas.
