# Notes API

[![CI](https://github.com/paulohscoelho/note-api/actions/workflows/ci.yml/badge.svg)](https://github.com/paulohscoelho/note-api/actions/workflows/ci.yml)
![Java 21](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot 4](https://img.shields.io/badge/Spring%20Boot-4.1.1-6DB33F?logo=springboot&logoColor=white)
![PostgreSQL 16](https://img.shields.io/badge/PostgreSQL-16-4169E1?logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?logo=docker&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?logo=apachemaven&logoColor=white)

API REST para gerenciamento de notas por usuário, com autenticação JWT e autorização por roles.

## 📖 Sobre

A **Notes API** é uma API REST para gerenciamento de notas por usuário. Cada usuário pode criar, consultar, atualizar e remover suas próprias notas. A consulta e a remoção de usuários são operações administrativas.

A autenticação é feita via **JWT**: o cliente faz login com email e senha, recebe um token e o envia no header `Authorization` das requisições seguintes. O acesso é controlado por **roles** (`USER` e `ADMIN`) combinadas com **ownership** dos recursos.

A principal característica é o isolamento de dados: um usuário `USER` enxerga apenas os próprios recursos, enquanto um usuário `ADMIN` tem acesso a todos os usuários e todas as notas por meio de endpoints administrativos dedicados.

## ✨ Funcionalidades

- **Autenticação JWT** com login via email e senha
- **Autorização por roles** (`USER` / `ADMIN`)
- **Autorização por ownership** — o usuário só acessa seus próprios recursos; o admin acessa todos
- **Gestão de usuários** (cadastro público; consulta e remoção só para `ADMIN`)
- **CRUD de notas**
- **Endpoints administrativos** para listar todos os usuários e todas as notas
- **Tratamento de erros padronizado** (404, 409, 400, 401, 403, 500)
- **Seed idempotente** do admin inicial via variáveis de ambiente
- **Containerização completa** (Postgres + app + Adminer)
- **Testes unitários** (29 cenários)
- **CI com GitHub Actions** rodando os testes em cada push/PR na `main`

## 🛠️ Tech Stack

| Categoria | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 4.1.1 (Spring MVC / Spring Data JPA) |
| Banco de Dados | PostgreSQL 16 |
| Autenticação | Spring Security + JWT (JJWT 0.12.5) |
| Build | Maven (via wrapper `./mvnw`) |
| Container | Docker + Docker Compose |
| Testes | JUnit 5, Mockito, AssertJ |
| CI | GitHub Actions |

## 🚀 Como Rodar

### Opção A — Só Docker (quick start)

**Pré-requisitos:** Docker Desktop instalado.

1. Clone o repositório:
   ```bash
   git clone https://github.com/paulohscoelho/note-api.git
   cd note-api
   ```

2. Copie o `.env.example` para `.env` e preencha as variáveis:
   ```bash
   cp .env.example .env
   ```

3. Suba os serviços:
   ```bash
   docker compose up -d
   ```

4. Confirme que está tudo no ar:
   ```bash
   docker compose ps
   curl http://localhost:8080/actuator/health
   ```

5. Acesse a API em [http://localhost:8080](http://localhost:8080).

**Serviços expostos:**

| Serviço | Porta | URL |
|---|---|---|
| app | 8080 | http://localhost:8080 |
| adminer | 8081 | http://localhost:8081 |
| postgres | 5432 | `localhost:5432` |

### Opção B — Desenvolvimento local

**Pré-requisitos:** JDK 21 e Maven (ou usar o wrapper `./mvnw`).

1. Suba **apenas** o Postgres via Docker:
   ```bash
   docker compose up -d postgres
   ```

2. Rode a aplicação pela IDE ou pelo wrapper:
   ```bash
   ./mvnw spring-boot:run
   ```

3. Defina as variáveis de ambiente no ambiente de execução (as mesmas do `.env`).

## ⚙️ Variáveis de Ambiente

Crie um arquivo `.env` a partir do `.env.example`. Ele contém dados sensíveis e **não deve ser commitado**.

| Variável | Descrição | Exemplo |
|---|---|---|
| `JWT_SECRET` | Chave usada para assinar e validar os tokens JWT (Base64) | `openssl rand -base64 32` |
| `ADMIN_EMAIL` | Email do usuário admin criado no seed inicial | `admin@example.com` |
| `ADMIN_PASSWORD` | Senha do usuário admin criado no seed inicial | `change-me` |
| `DB_USER` | Usuário do PostgreSQL | `postgres` |
| `DB_PASSWORD` | Senha do PostgreSQL | `postgres` |
| `DB_NAME` | Nome do banco de dados | `notesdb` |
| `DB_PORT` | Porta exposta do PostgreSQL | `5432` |

## 📚 Endpoints

### Auth (público)

| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| POST | `/auth/login` | Autentica o usuário e retorna um token JWT | público |

### Users

| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| POST | `/users` | Cria um novo usuário | público |
| GET | `/users/{uuid}/notes` | Busca um usuário e suas notas | USER (dono) ou ADMIN |

### Notes

| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| POST | `/notes` | Cria uma nota para o usuário autenticado | USER ou ADMIN |
| GET | `/notes` | Lista as notas do usuário autenticado | USER ou ADMIN |
| GET | `/notes/{id}` | Busca uma nota pelo id | USER (dono) ou ADMIN |
| PUT | `/notes/{id}` | Atualiza uma nota | USER (dono) ou ADMIN |
| DELETE | `/notes/{id}` | Remove uma nota | USER (dono) ou ADMIN |

### Admin

| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| GET | `/admin/users` | Lista todos os usuários | ADMIN |
| GET | `/admin/users/{uuid}` | Busca um usuário pelo UUID | ADMIN |
| GET | `/admin/users/search?email=` | Busca um usuário pelo email | ADMIN |
| DELETE | `/admin/users/{uuid}` | Remove um usuário | ADMIN |
| GET | `/admin/notes` | Lista todas as notas | ADMIN |

### Infra

| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| GET | `/actuator/health` | Health check da aplicação | público |

## 🧪 Testes

Rode a suíte com:

```bash
./mvnw test
```

Atualmente são **29 testes unitários** cobrindo `NoteService`, `UserService` e `JwtService`.

Padrões e ferramentas utilizados:

- **JUnit 5** com `@Nested` para organizar cenários por contexto
- **Mockito** com `ArgumentCaptor` para inspecionar argumentos capturados
- **AssertJ** para asserções fluentes
- **`verifyNoInteractions`** para garantir que dependências não foram acionadas

O **CI** executa os testes automaticamente a cada push e PR na branch `main`, via GitHub Actions.

## 🏗️ Arquitetura

### Estrutura de pacotes

```
notesapi/
├── admin/         # Endpoints administrativos e seed do admin inicial
├── auth/          # Autenticação e DTOs de login
├── common/        # Tratamento de erros padronizado (exceptions, handler global)
├── note/          # Domínio de notas (controller, service, entity, repository, dto, mapper)
├── security/      # Configuração de segurança, JWT e filtro de autenticação
└── user/          # Domínio de usuários (controller, service, entity, repository, dto, mapper)
```

### Fluxo de autenticação

1. O cliente envia `POST /auth/login` com email e senha.
2. O `AuthenticationManager` valida as credenciais.
3. O `JwtService` gera um token contendo o subject (email) e a claim de role.
4. O cliente passa a enviar o token no header `Authorization: Bearer <token>`.
5. O `JwtAuthenticationFilter` intercepta a requisição, valida o token e carrega os `UserDetails`.
6. O `SecurityContext` é populado com a autenticação, liberando o acesso conforme role e ownership.

### Modelo de autorização

- **Roles:** `USER` (acessa apenas os próprios recursos) e `ADMIN` (acessa tudo).
- **Ownership:** verificado no service (via `loadAccessibleNote`) e nas anotações `@PreAuthorize` (ex.: `#uuid == authentication.principal.uuid or hasRole('ADMIN')`).

## 🔐 Decisões de Design

- **JWT em vez de sessão:** a API é *stateless*, o que facilita a escalabilidade horizontal e dispensa armazenamento de sessão no servidor.
- **404 em recurso alheio (e não 403):** evita IDOR, impedindo que um usuário descubra a existência de recursos de terceiros.
- **Seed do admin via `CommandLineRunner`:** é idempotente e garante o primeiro admin na subida da aplicação a partir de variáveis de ambiente.
- **Role via enum:** simples e suficiente, já que cada usuário tem exatamente uma role — um RBAC completo com tabelas não é necessário hoje.
- **`@PreAuthorize` + URL matcher:** aplica defesa em profundidade, validando o acesso tanto na camada de método quanto na configuração de segurança.

## 📁 Estrutura de Pastas

```
note-api/
├── .github/
│   └── workflows/
│       └── ci.yml           # Workflow do GitHub Actions
├── src/
│   ├── main/
│   │   ├── java/notesapi/   # Código-fonte da aplicação
│   │   └── resources/
│   │       └── application.yaml
│   └── test/
│       └── java/notesapi/   # Testes unitários e de integração
├── .env.example             # Template das variáveis de ambiente
├── .gitignore
├── docker-compose.yml       # Orquestração: app + postgres + adminer
├── Dockerfile               # Build multi-stage da aplicação
├── mvnw / mvnw.cmd          # Maven Wrapper
├── pom.xml
└── README.md
```