package notesapi.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Notes API",
        version = "v1",
        description = "API REST para gerenciamento de notas por usuário, com autenticação JWT e autorização por roles.",
        contact = @Contact(
            name = "Paulo Coelho",
            url = "https://github.com/paulohscoelho/note-api"
        )
    )
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "Autenticação via JWT. Faça login em POST /auth/login e use o token retornado."
)
public class OpenApiConfig {
}