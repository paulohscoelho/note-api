# note-api

API de notas simples em Java + Spring Boot

Descrição

API para gerenciar usuários e suas notas, com autenticação JWT. Permite registro de usuários, login e operações CRUD sobre notas (apenas para o usuário autenticado).

Tecnologias

- Java 17+ (certifique-se de ter o JDK instalado)
- Spring Boot
- Maven (uso do wrapper `mvnw`/`mvnw.cmd` incluído)
- JWT para autenticação

Requisitos

- JDK 17 ou superior
- Maven (opcional, pode usar o wrapper)

Como executar

No Windows (usando o wrapper):

1. Compilar:

```
./mvnw.cmd clean package
```

2. Executar:

```
./mvnw.cmd spring-boot:run
```

Ou com Maven instalado:

```
mvn clean package
mvn spring-boot:run
```

A aplicação por padrão roda em http://localhost:8080 (verificar `src/main/resources/application.yaml` se necessário).

Endpoints principais

Autenticação

- POST /auth/login
  - Corpo (JSON):
    {
      "email": "usuario@exemplo.com",
      "password": "senha123"
    }
  - Retorno: { "token": "<JWT>" }
  - Usar o token nas requisições autenticadas no header: `Authorization: Bearer <JWT>`

Usuários

- POST /users
  - Cria um novo usuário
  - Corpo (JSON): { "email": "user@ex.com", "password": "senha123" }
  - Retorno: 201 Created com dados do usuário

- GET /users/{uuid}
  - Recupera usuário por UUID

- GET /users/search?email={email}
  - Recupera usuário pelo email

- GET /users/{uuid}/notes
  - Recupera usuário junto com suas notas

- DELETE /users/{uuid}
  - Remove usuário

Notas (requer autenticação)

- POST /notes
  - Cria nota para o usuário autenticado
  - Corpo (JSON): { "title": "Título", "content": "Conteúdo da nota" }
  - Retorno: 201 Created com a nota

- GET /notes
  - Lista todas as notas do usuário autenticado

- GET /notes/{id}
  - Recupera nota específica (pertencente ao usuário autenticado)

- PUT /notes/{id}
  - Atualiza uma nota
  - Corpo (JSON): { "title": "Novo título", "content": "Novo conteúdo" }

- DELETE /notes/{id}
  - Remove nota

Validações importantes

- Email deve ter formato válido
- Senha: mínimo 8 e máximo 20 caracteres
- Título da nota: máximo 100 caracteres
- Campos obrigatórios são validados e retornam mensagens de erro padronizadas

Exemplos curl

Registrar usuário:

```
curl -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{"email":"user@ex.com","password":"senha123"}'
```

Login e obter token:

```
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@ex.com","password":"senha123"}'
```

Criar nota (usar token retornado):

```
curl -X POST http://localhost:8080/notes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN>" \
  -d '{"title":"Minha nota","content":"Texto da nota"}'
```

Testes

Executar testes unitários com:

```
./mvnw.cmd test
```

Contribuição

Pull requests são bem-vindos. Favor abrir issue descrevendo a proposta antes de mudanças significativas.

Licença

Adicionar informação de licença conforme desejado.
