package notesapi.auth;

import notesapi.config.AbstractIntegrationTest;
import notesapi.user.entity.Role;
import notesapi.user.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("AuthController (integração)")
class AuthControllerIT extends AbstractIntegrationTest {

   private UserEntity jorge;

   @BeforeEach
   void setUp() {
      jorge = criarUser("jorge@test.com", Role.USER);
   }

   @Test
   @DisplayName("deve retornar 200 e token quando credenciais válidas")
   void deveRetornar200_quandoCredenciaisValidas() throws Exception {
      String json = """
                {
                  "email": "jorge@test.com",
                  "password": "senha12345"
                }
                """;

      mockMvc.perform(post("/auth/login")
              .contentType(MediaType.APPLICATION_JSON)
              .content(json))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.token").isNotEmpty());
   }

   @Test
   @DisplayName("deve retornar 401 quando senha errada")
   void deveRetornar401_quandoSenhaErrada() throws Exception {
      String json = """
                {
                  "email": "jorge@test.com",
                  "password": "senha-errada"
                }
                """;

      mockMvc.perform(post("/auth/login")
              .contentType(MediaType.APPLICATION_JSON)
              .content(json))
          .andExpect(status().isUnauthorized());
   }

   @Test
   @DisplayName("deve retornar 401 quando user não existe")
   void deveRetornar401_quandoUserNaoExiste() throws Exception {
      String json = """
                {
                  "email": "naoexiste@test.com",
                  "password": "senha12345"
                }
                """;

      mockMvc.perform(post("/auth/login")
              .contentType(MediaType.APPLICATION_JSON)
              .content(json))
          .andExpect(status().isUnauthorized());
   }
}