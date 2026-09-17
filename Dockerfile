# ============================================
# Estágio 1 — Build
# ============================================
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copia primeiro só o pom.xml (aproveita cache de dependências)
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# Baixa as dependências (camada cacheada se pom.xml não mudar)
RUN ./mvnw dependency:go-offline -B

# Agora copia o código-fonte
COPY src src

# Gera o jar (sem rodar testes — eles rodam em CI, não no build da imagem)
RUN ./mvnw clean package -DskipTests

# ============================================
# Estágio 2 — Runtime
# ============================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Cria usuário não-root (boa prática de segurança)
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copia APENAS o jar do estágio anterior
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Comando de inicialização
ENTRYPOINT ["java", "-jar", "app.jar"]