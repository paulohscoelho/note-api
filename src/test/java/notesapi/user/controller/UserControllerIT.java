package notesapi.user.controller;

import notesapi.config.AbstractIntegrationTest;
import notesapi.user.entity.Role;
import notesapi.user.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("UserController (integração)")
class UserControllerIT extends AbstractIntegrationTest {

   private UserEntity jorge;
   private UserEntity joao;
   private UserEntity admin;
   private String tokenJorge;
   private String tokenJoao;
   private String tokenAdmin;

   @BeforeEach
   void setUp() {
      jorge = criarUser("jorge@test.com", Role.USER);
      joao = criarUser("joao@test.com", Role.USER);
      admin = criarUser("admin@test.com", Role.ADMIN);
      tokenJorge = gerarToken(jorge);
      tokenJoao = gerarToken(joao);
      tokenAdmin = gerarToken(admin);
   }

   @Nested
   @DisplayName("POST /users")
   class CreateUser {

      @Test
      @DisplayName("deve retornar 201 quando dados válidos")
      void deveRetornar201_quandoDadosValidos() throws Exception {
         String json = """
                    {
                      "email": "novo@test.com",
                      "password": "senha12345"
                    }
                    """;

         mockMvc.perform(post("/users")
                 .contentType(MediaType.APPLICATION_JSON)
                 .content(json))
             .andExpect(status().isCreated())
             .andExpect(jsonPath("$.email").value("novo@test.com"));
      }

      @Test
      @DisplayName("deve retornar 409 quando email já existe")
      void deveRetornar409_quandoEmailDuplicado() throws Exception {
         String json = """
                    {
                      "email": "jorge@test.com",
                      "password": "outrasenha123"
                    }
                    """;

         mockMvc.perform(post("/users")
                 .contentType(MediaType.APPLICATION_JSON)
                 .content(json))
             .andExpect(status().isConflict());
      }

      @Test
      @DisplayName("deve retornar 400 quando email inválido")
      void deveRetornar400_quandoEmailInvalido() throws Exception {
         String json = """
                    {
                      "email": "nao-e-email",
                      "password": "senha12345"
                    }
                    """;

         mockMvc.perform(post("/users")
                 .contentType(MediaType.APPLICATION_JSON)
                 .content(json))
             .andExpect(status().isBadRequest())
             .andExpect(jsonPath("$.fields[0].field").value("email"));
      }

      @Test
      @DisplayName("deve retornar 400 quando senha tem menos de 8 caracteres")
      void deveRetornar400_quandoSenhaCurta() throws Exception {
         String json = """
                    {
                      "email": "valido@test.com",
                      "password": "curta"
                    }
                    """;

         mockMvc.perform(post("/users")
                 .contentType(MediaType.APPLICATION_JSON)
                 .content(json))
             .andExpect(status().isBadRequest())
             .andExpect(jsonPath("$.fields[0].field").value("password"));
      }
   }

   @Nested
   @DisplayName("GET /users/{uuid}/notes")
   class GetWithNotes {

      @Test
      @DisplayName("deve retornar 200 quando user busca o próprio perfil")
      void deveRetornar200_quandoProprioUser() throws Exception {
         mockMvc.perform(get("/users/" + jorge.getUuid() + "/notes")
                 .header("Authorization", "Bearer " + tokenJorge))
             .andExpect(status().isOk())
             .andExpect(jsonPath("$.email").value("jorge@test.com"));
      }

      @Test
      @DisplayName("deve retornar 403 quando user busca outro user")
      void deveRetornar403_quandoOutroUser() throws Exception {
         mockMvc.perform(get("/users/" + joao.getUuid() + "/notes")
                 .header("Authorization", "Bearer " + tokenJorge))
             .andExpect(status().isForbidden());
      }

      @Test
      @DisplayName("deve retornar 200 quando ADMIN busca qualquer user")
      void deveRetornar200_quandoAdmin() throws Exception {
         mockMvc.perform(get("/users/" + jorge.getUuid() + "/notes")
                 .header("Authorization", "Bearer " + tokenAdmin))
             .andExpect(status().isOk())
             .andExpect(jsonPath("$.email").value("jorge@test.com"));
      }

      @Test
      @DisplayName("deve retornar 403 quando não autenticado")
      void deveRetornar403_quandoAnonimo() throws Exception {
         mockMvc.perform(get("/users/" + jorge.getUuid() + "/notes"))
             .andExpect(status().isForbidden());
      }
   }
}