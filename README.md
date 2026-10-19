# Notes API

[![CI](https://github.com/paulohscoelho/note-api/actions/workflows/ci.yml/badge.svg)](https://github.com/paulohscoelho/note-api/actions/workflows/ci.yml)
![Java 21](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot 4](https://img.shields.io/badge/Spring%20Boot-4.1.1-6DB33F?logo=springboot&logoColor=white)
![Angular](https://img.shields.io/badge/Angular-20-DD0031?logo=angular&logoColor=white)
![PostgreSQL 16](https://img.shields.io/badge/PostgreSQL-16-4169E1?logo=postgresql&logoColor=white)
![Flyway](https://img.shields.io/badge/Flyway-CC0200?logo=flyway&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?logo=docker&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?logo=apachemaven&logoColor=white)
![Vercel](https://img.shields.io/badge/Vercel-000000?logo=vercel&logoColor=white)
![Render](https://img.shields.io/badge/Render-46E3B7?logo=render&logoColor=white)

Aplicação **full-stack** para gerenciamento de notas por usuário, com autenticação JWT, autorização por roles (RBAC), migrações versionadas e deploy automatizado em produção.

## 🌐 Links de Produção

| Serviço | URL |
|---|---|
| **Frontend (Vercel)** | https://note-api-delta.vercel.app |
| **Backend (Render)** | https://note-api-8qb4.onrender.com |
| **Health Check** | https://note-api-8qb4.onrender.com/actuator/health |
| **Swagger UI** | https://note-api-8qb4.onrender.com/swagger-ui.html |

> ⚠️ O backend está no plano **Free do Render**. Um monitor do **UptimeRobot** faz um ping a cada 5 minutos no endpoint `/actuator/health` para manter a aplicação ativa e evitar a hibernação.

## 📖 Sobre

A **Notes API** é uma aplicação full-stack para gerenciamento de notas. Cada usuário pode criar, consultar, atualizar e remover suas próprias notas, atribuindo a elas níveis de **prioridade** e **data de vencimento**.

A autenticação é feita via **JWT**: o cliente faz login com email e senha, recebe um token e o envia no header `Authorization`. O acesso é controlado por **roles** (`USER` e `ADMIN`) combinadas com **ownership** dos recursos.

O isolamento de dados é garantido: um usuário `USER` enxerga apenas os próprios recursos. Já o `ADMIN` possui acesso a um **Painel Administrativo** no frontend, onde pode listar, buscar, alterar a role e deletar usuários, além de visualizar todas as notas do sistema (com regras de negócio que impedem a exclusão/rebaixamento do último administrador).

## ✨ Funcionalidades

- **Autenticação JWT** com login via email e senha.
- **Autorização por roles** (`USER` / `ADMIN`) com proteção no Backend e Frontend (Guards).
- **Painel de Administração:** Tabela de usuários, busca em tempo real, alteração de roles e exclusão.
- **CRUD de Notas Avançado:** Inclusão de **Prioridade** (com badges coloridas) e **Data de Vencimento**.
- **Filtros e Ordenação:** Listagem de notas por mais recentes, título, maior/menor prioridade e vencimento.
- **Paginação e Busca:** Busca textual por título/conteúdo e paginação de resultados.
- **Versionamento de Banco:** Migrações estruturadas com **Flyway** (V1, V2, V3).
- **Tratamento de erros padronizado** (404, 409, 400, 401, 403, 500).
- **Seed idempotente** do admin inicial via variáveis de ambiente.
- **Containerização completa** (Postgres + app + Adminer).
- **CORS configurado** para múltiplas origens (dev e produção).
- **Testes Automatizados:** Testes unitários (Mockito) e de integração (Testcontainers).
- **Cobertura de Código:** Validação de cobertura com **JaCoCo** (mínimo de 80%).
- **CI/CD:** GitHub Actions para testes/cobertura e deploy automático (Render/Vercel).

## 🛠️ Tech Stack

| Categoria | Tecnologia |
|---|---|
| Linguagem Backend | Java 21 |
| Framework Backend | Spring Boot 4.1.1 (Spring MVC / Spring Data JPA) |
| Framework Frontend | Angular 20 (standalone components, signals, guards, interceptors) |
| Banco de Dados | PostgreSQL 16 (Docker local) / PostgreSQL 18 (Neon em produção) |
| Migração de Banco | Flyway |
| Autenticação | Spring Security + JWT (JJWT 0.12.5) |
| Build Backend | Maven (via wrapper `./mvnw`) |
| Build Frontend | Angular CLI + npm |
| Container | Docker + Docker Compose |
| Testes | JUnit 5, Mockito, Testcontainers, AssertJ, JaCoCo |
| CI/CD | GitHub Actions |
| Deploy | Render (Backend), Vercel (Frontend), Neon (Banco) |

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

> O `environment.ts` do frontend aponta para `http://127.0.0.1:8080` em desenvolvimento e para o Render (`https://note-api-8qb4.onrender.com`) em produção (via `environment.prod.ts`).

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

### Backend (local e produção)

| Variável | Descrição |
|---|---|
| `SPRING_PROFILES_ACTIVE` | `prod` (no Render) ou `dev` (local) |
| `JWT_SECRET` | Chave usada para assinar e validar os tokens JWT (Base64) |
| `SPRING_DATASOURCE_URL` | JDBC URL do banco (ex: `jdbc:postgresql://...neon.tech/neondb?sslmode=require`) |
| `SPRING_DATASOURCE_USERNAME` | Usuário do banco |
| `SPRING_DATASOURCE_PASSWORD` | Senha do banco |
| `ADMIN_EMAIL` | Email do usuário admin criado no seed inicial |
| `ADMIN_PASSWORD` | Senha do usuário admin criado no seed inicial |
| `APP_CORS_ALLOWED_ORIGINS` | Origens permitidas, separadas por vírgula |

## 📚 Endpoints Principais

### Auth
| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| POST | `/auth/login` | Autentica o usuário e retorna um token JWT | público |

### Users
| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| POST | `/users` | Cria um novo usuário | público |
| GET | `/users/me` | Retorna os dados do usuário autenticado | USER ou ADMIN |

### Notes (Campos: `title`, `content`, `priority`, `dueDate`)
| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| POST | `/notes` | Cria uma nota | USER ou ADMIN |
| GET | `/notes` | Lista as notas do usuário (paginado e com filtros) | USER ou ADMIN |
| GET | `/notes/{id}` | Busca uma nota pelo id | USER (dono) ou ADMIN |
| PUT | `/notes/{id}` | Atualiza uma nota | USER (dono) ou ADMIN |
| DELETE | `/notes/{id}` | Remove uma nota | USER (dono) ou ADMIN |

### Admin
| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| GET | `/admin/users` | Lista todos os usuários (paginado) | ADMIN |
| GET | `/admin/users/{uuid}` | Busca um usuário pelo UUID | ADMIN |
| PATCH | `/admin/users/{uuid}/role` | Altera a role de um usuário | ADMIN |
| DELETE | `/admin/users/{uuid}` | Remove um usuário | ADMIN |
| GET | `/admin/notes` | Lista todas as notas do sistema | ADMIN |

## 🧪 Testes e Cobertura

Rode a suíte de testes (unitários e de integração com Testcontainers) e gere o relatório de cobertura:

```bash
./mvnw verify
```

- **Testes Unitários:** JUnit 5, Mockito e AssertJ.
- **Testes de Integração:** Testcontainers (sobe um PostgreSQL temporário) e MockMvc.
- **Cobertura (JaCoCo):** O build falha se a cobertura de linhas cair abaixo de **80%**. O relatório HTML fica em `target/site/jacoco/index.html`.

O **CI** (GitHub Actions) executa os testes e valida a cobertura automaticamente a cada push/PR na branch `main`.

## 🏗️ Arquitetura e Decisões

- **JWT em vez de sessão:** API *stateless*, facilitando a escalabilidade.
- **404 em recurso alheio (e não 403):** Evita IDOR (Insecure Direct Object Reference).
- **Flyway em vez de `ddl-auto: update`:** Garante que as alterações no banco sejam auditáveis, versionadas e seguras em produção.
- **Defesa em profundidade:** Segurança aplicada via `@PreAuthorize` no backend e `adminGuard` no frontend.
- **Regras de Negócio:** Bloqueio de rebaixamento do último ADMIN e restrição de alteração da própria role.
- **Environments no Angular:** URLs da API externalizadas para facilitar o build de produção.

## 📁 Estrutura de Pastas

```
note-api/
├── .github/workflows/ci.yml # Workflow do GitHub Actions
├── frontend/                # Aplicação Angular (components, guards, services)
├── src/
│   ├── main/
│   │   ├── java/notesapi/   # Código-fonte do backend (admin, auth, note, user...)
│   │   └── resources/
│   │       ├── db/migration # Scripts SQL do Flyway (V1, V2, V3)
│   │       └── application*.yaml
│   └── test/                # Testes unitários e de integração (IT)
├── docker-compose.yml       # Orquestração: app + postgres + adminer
├── Dockerfile               # Build multi-stage do backend
├── pom.xml
└── README.md
```

## 👤 Autor

**Paulo Henrique Coelho**

- GitHub: [@paulohscoelho](https://github.com/paulohscoelho)