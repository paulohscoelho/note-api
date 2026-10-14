# Notes API

[![CI](https://github.com/paulohscoelho/note-api/actions/workflows/ci.yml/badge.svg)](https://github.com/paulohscoelho/note-api/actions/workflows/ci.yml)
![Java 21](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot 4](https://img.shields.io/badge/Spring%20Boot-4.1.1-6DB33F?logo=springboot&logoColor=white)
![Angular](https://img.shields.io/badge/Angular-20-DD0031?logo=angular&logoColor=white)
![PostgreSQL 16](https://img.shields.io/badge/PostgreSQL-16-4169E1?logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?logo=docker&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?logo=apachemaven&logoColor=white)
![Vercel](https://img.shields.io/badge/Vercel-000000?logo=vercel&logoColor=white)
![Render](https://img.shields.io/badge/Render-46E3B7?logo=render&logoColor=white)

Aplicação **full-stack** para gerenciamento de notas por usuário, com autenticação JWT, autorização por roles e deploy em produção.

## 🌐 Links de Produção

| Serviço | URL |
|---|---|
| **Frontend (Vercel)** | https://note-api-delta.vercel.app |
| **Backend (Render)** | https://note-api-8qb4.onrender.com |
| **Health Check** | https://note-api-8qb4.onrender.com/actuator/health |
| **Swagger UI** | https://note-api-8qb4.onrender.com/swagger-ui.html |

> ⚠️ O backend está no plano **Free do Render**. Ele pode levar ~50s para "acordar" se ficar inativo. Para contornar isso, um monitor do **UptimeRobot** faz um ping a cada 5 minutos no endpoint `/actuator/health`, mantendo a aplicação ativa.

## 📖 Sobre

A **Notes API** é uma aplicação full-stack para gerenciamento de notas por usuário. Cada usuário pode criar, consultar, atualizar e remover suas próprias notas. A consulta e a remoção de usuários são operações administrativas.

A autenticação é feita via **JWT**: o cliente faz login com email e senha, recebe um token e o envia no header `Authorization` das requisições seguintes. O acesso é controlado por **roles** (`USER` e `ADMIN`) combinadas com **ownership** dos recursos.

A principal característica é o isolamento de dados: um usuário `USER` enxerga apenas os próprios recursos, enquanto um usuário `ADMIN` tem acesso a todos os usuários e todas as notas por meio de endpoints administrativos dedicados.

## ✨ Funcionalidades

- **Autenticação JWT** com login via email e senha
- **Autorização por roles** (`USER` / `ADMIN`)
- **Autorização por ownership** — o usuário só acessa seus próprios recursos; o admin acessa todos
- **Gestão de usuários** (cadastro público; consulta e remoção só para `ADMIN`)
- **CRUD de notas**
- **Endpoints administrativos** para listar todos os usuários e todas as notas
- **Frontend Angular** com interceptors de autenticação e guards de rota
- **Tratamento de erros padronizado** (404, 409, 400, 401, 403, 500)
- **Seed idempotente** do admin inicial via variáveis de ambiente
- **Containerização completa** (Postgres + app + Adminer)
- **CORS configurado** para múltiplas origens (dev e produção)
- **Testes unitários** (29 cenários)
- **CI com GitHub Actions** rodando os testes em cada push/PR na `main`
- **Deploy automatizado**: backend no Render, frontend na Vercel, banco no Neon

## 🛠️ Tech Stack

| Categoria | Tecnologia |
|---|---|
| Linguagem Backend | Java 21 |
| Framework Backend | Spring Boot 4.1.1 (Spring MVC / Spring Data JPA) |
| Framework Frontend | Angular 20 (standalone components, signals, interceptors) |
| Banco de Dados | PostgreSQL 16 (Docker local) / PostgreSQL 18 (Neon em produção) |
| Autenticação | Spring Security + JWT (JJWT 0.12.5) |
| Build Backend | Maven (via wrapper `./mvnw`) |
| Build Frontend | Angular CLI + npm |
| Container | Docker + Docker Compose |
| Testes | JUnit 5, Mockito, AssertJ |
| CI | GitHub Actions |
| Deploy Backend | Render (Docker) |
| Deploy Frontend | Vercel |
| Deploy Banco | Neon (PostgreSQL serverless) |
| Keep-Alive | UptimeRobot |

## 🚀 Como Rodar

### Opção A — Stack completa via Docker (backend + banco)

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
| app (backend) | 8080 | http://localhost:8080 |
| adminer | 8081 | http://localhost:8081 |
| postgres | 5432 | `localhost:5432` |

### Opção B — Frontend Angular (desenvolvimento)

**Pré-requisitos:** Node.js 20+ e npm.

1. Entre na pasta do frontend:
   ```bash
   cd frontend
   ```

2. Instale as dependências:
   ```bash
   npm install
   ```

3. Rode o servidor de desenvolvimento:
   ```bash
   ng serve
   ```

4. Acesse em [http://localhost:4200](http://localhost:4200).

> O `environment.ts` do frontend aponta para `http://localhost:8080` em desenvolvimento e para o Render (`https://note-api-8qb4.onrender.com`) em produção (via `environment.prod.ts`).

### Opção C — Backend sem Docker (IDE)

**Pré-requisitos:** JDK 21 e Maven (ou usar o wrapper `./mvnw`).

1. Suba **apenas** o Postgres via Docker:
   ```bash
   docker compose up -d postgres
   ```

2. Rode a aplicação pela IDE ou pelo wrapper:
   ```bash
   ./mvnw spring-boot:run
   ```

## ⚙️ Variáveis de Ambiente

Crie um arquivo `.env` a partir do `.env.example`. Ele contém dados sensíveis e **não deve ser commitado**.

### Backend (local)

| Variável | Descrição | Exemplo |
|---|---|---|
| `JWT_SECRET` | Chave usada para assinar e validar os tokens JWT (Base64) | `openssl rand -base64 32` |
| `ADMIN_EMAIL` | Email do usuário admin criado no seed inicial | `admin@example.com` |
| `ADMIN_PASSWORD` | Senha do usuário admin criado no seed inicial | `change-me` |
| `DB_USER` | Usuário do PostgreSQL | `postgres` |
| `DB_PASSWORD` | Senha do PostgreSQL | `postgres` |
| `DB_NAME` | Nome do banco de dados | `notesdb` |
| `DB_PORT` | Porta exposta do PostgreSQL | `5432` |

### Backend (produção — Render)

| Variável | Descrição |
|---|---|
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `JWT_SECRET` | Chave JWT gerada com `openssl rand -base64 32` |
| `SPRING_DATASOURCE_URL` | JDBC URL do Neon (ex: `jdbc:postgresql://...neon.tech/neondb?sslmode=require`) |
| `SPRING_DATASOURCE_USERNAME` | Usuário do Neon |
| `SPRING_DATASOURCE_PASSWORD` | Senha do Neon |
| `ADMIN_EMAIL` | Email do admin inicial |
| `ADMIN_PASSWORD` | Senha do admin inicial |
| `APP_CORS_ALLOWED_ORIGINS` | Origens permitidas, separadas por vírgula (ex: `http://localhost:4200,https://note-api-delta.vercel.app`) |

## 📚 Endpoints

### Auth (público)

| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| POST | `/auth/login` | Autentica o usuário e retorna um token JWT | público |

### Users

| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| POST | `/users` | Cria um novo usuário | público |
| GET | `/users/me` | Retorna os dados do usuário autenticado | USER ou ADMIN |
| GET | `/users/{uuid}/notes` | Busca um usuário e suas notas | USER (dono) ou ADMIN |

### Notes

| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| POST | `/notes` | Cria uma nota para o usuário autenticado | USER ou ADMIN |
| GET | `/notes` | Lista as notas do usuário autenticado (paginado) | USER ou ADMIN |
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

## 📖 Documentação da API

A API tem documentação interativa via **Swagger UI** (OpenAPI 3):

- **Swagger UI (local):** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **Swagger UI (prod):** [https://note-api-8qb4.onrender.com/swagger-ui.html](https://note-api-8qb4.onrender.com/swagger-ui.html)
- **OpenAPI spec (JSON):** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

> ⚠️ O botão **"Authorize"** do Swagger UI tem um [bug conhecido](https://github.com/springdoc/springdoc-openapi/issues/3374) com Spring Boot 4 + springdoc 3.x. **Para testar endpoints autenticados**, use o Insomnia/curl.

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

### Estrutura de pacotes (backend)

```
notesapi/
├── admin/         # Endpoints administrativos e seed do admin inicial
├── auth/          # Autenticação e DTOs de login
├── common/        # Tratamento de erros padronizado (exceptions, handler global)
├── note/          # Domínio de notas (controller, service, entity, repository, dto, mapper)
├── security/      # Configuração de segurança, JWT, CORS e filtro de autenticação
└── user/          # Domínio de usuários (controller, service, entity, repository, dto, mapper)
```

### Estrutura de pastas (frontend)

```
frontend/src/app/
├── components/    # Componentes (login, registro, lista de notas, etc.)
├── guards/        # Guards de rota (authGuard)
├── interceptors/  # Interceptor HTTP que injeta o JWT nas requisições
├── models/        # Interfaces TypeScript (Note, User, Page, etc.)
├── services/      # Serviços HTTP (auth.service, note.service)
├── app.config.ts  # Configuração global (providers, httpClient, interceptor)
└── app.routes.ts  # Definição das rotas
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

### Fluxo de CORS

O `CorsConfig` lê a variável `APP_CORS_ALLOWED_ORIGINS` (separada por vírgula) e configura as origens permitidas. No `SecurityConfig`, requisições `OPTIONS` (preflight) são liberadas com `permitAll()`, e o `JwtAuthenticationFilter` ignora requisições OPTIONS para não bloquear o preflight.

## ☁️ Deploy

### Backend (Render + Docker)

1. Crie um **Web Service** no [Render](https://render.com) apontando para o repositório GitHub.
2. Selecione o ambiente **Docker** (o `Dockerfile` multi-stage na raiz já faz o build).
3. Configure as variáveis de ambiente de produção (ver seção acima).
4. O Render faz o build e disponibiliza a URL pública.

### Frontend (Vercel)

1. Importe o repositório na [Vercel](https://vercel.com).
2. Configure o **Root Directory** como `frontend`.
3. Framework Preset: **Angular**.
4. Build Command: `ng build --configuration production`.
5. Output Directory: `dist/frontend/browser`.

### Banco de Dados (Neon)

1. Crie um projeto no [Neon](https://neon.tech).
2. Copie a connection string no formato JDBC (`jdbc:postgresql://...neon.tech/neondb?sslmode=require`).
3. Configure as variáveis `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` e `SPRING_DATASOURCE_PASSWORD` no Render.

### Keep-Alive (UptimeRobot)

Como o plano Free do Render desliga a aplicação após 15 minutos de inatividade, um monitor HTTP do [UptimeRobot](https://uptimerobot.com) faz um ping a cada 5 minutos em `/actuator/health` para manter a aplicação ativa.

## 🔐 Decisões de Design

- **JWT em vez de sessão:** a API é *stateless*, o que facilita a escalabilidade horizontal e dispensa armazenamento de sessão no servidor.
- **404 em recurso alheio (e não 403):** evita IDOR, impedindo que um usuário descubra a existência de recursos de terceiros.
- **Seed do admin via `CommandLineRunner`:** é idempotente e garante o primeiro admin na subida da aplicação a partir de variáveis de ambiente.
- **Role via enum:** simples e suficiente, já que cada usuário tem exatamente uma role — um RBAC completo com tabelas não é necessário hoje.
- **`@PreAuthorize` + URL matcher:** aplica defesa em profundidade, validando o acesso tanto na camada de método quanto na configuração de segurança.
- **Environments no Angular:** URLs da API são externalizadas em `environment.ts` e `environment.prod.ts`, permitindo build de produção com `fileReplacements` via `angular.json`.

## 📁 Estrutura de Pastas

```
note-api/
├── .github/
│   └── workflows/
│       └── ci.yml           # Workflow do GitHub Actions
├── frontend/                # Aplicação Angular
│   ├── src/
│   │   ├── app/             # Componentes, services, guards, interceptors
│   │   └── environments/    # environment.ts (dev) e environment.prod.ts
│   ├── angular.json
│   └── package.json
├── src/
│   ├── main/
│   │   ├── java/notesapi/   # Código-fonte do backend
│   │   └── resources/
│   │       ├── application.yaml
│   │       ├── application-dev.yaml
│   │       └── application-prod.yaml
│   └── test/
│       └── java/notesapi/   # Testes unitários e de integração
├── .env.example             # Template das variáveis de ambiente
├── .gitignore
├── docker-compose.yml       # Orquestração: app + postgres + adminer
├── Dockerfile               # Build multi-stage da aplicação (backend)
├── mvnw / mvnw.cmd          # Maven Wrapper
├── pom.xml
└── README.md
```

## 👤 Autor

**Paulo Henrique Coelho**

- GitHub: [@paulohscoelho](https://github.com/paulohscoelho)