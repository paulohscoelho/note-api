package notesapi.admin;
import notesapi.config.AbstractIntegrationTest;
import notesapi.user.entity.Role;
import notesapi.user.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("AdminController (integração)")
class AdminControllerIT extends AbstractIntegrationTest {

   private UserEntity admin;
   private UserEntity userComum;
   private String tokenAdmin;
   private String tokenUser;

   @BeforeEach
   void setUp() {
      admin = createUser("admin@test.com", Role.ADMIN);
      userComum = createUser("user@test.com", Role.USER);
      tokenAdmin = gerarToken(admin);
      tokenUser = gerarToken(userComum);
   }

   @Nested
   @DisplayName("GET /admin/users")
   class ListUsers {

      @Test
      @DisplayName("deve retornar 200 e página de usuários quando autenticado como ADMIN")
      void deveRetornar200_quandoAdmin() throws Exception {
         mockMvc.perform(get("/admin/users")
                 .header("Authorization", "Bearer " + tokenAdmin))
             .andExpect(status().isOk())
             .andExpect(jsonPath("$.content").isArray())
             .andExpect(jsonPath("$.content", hasSize(2)))
             .andExpect(jsonPath("$.totalElements").value(2));
      }

      @Test
      @DisplayName("deve filtrar usuários por termo de busca quando informado")
      void deveFiltrarUsuarios_quandoSearchInformado() throws Exception {
         mockMvc.perform(get("/admin/users")
                 .param("search", "user")
                 .header("Authorization", "Bearer " + tokenAdmin))
             .andExpect(status().isOk())
             .andExpect(jsonPath("$.content").isArray())
             .andExpect(jsonPath("$.content", hasSize(1)))
             .andExpect(jsonPath("$.content[0].email").value("user@test.com"));
      }

      @Test
      @DisplayName("deve retornar 403 quando autenticado como USER")
      void deveRetornar403_quandoUser() throws Exception {
         mockMvc.perform(get("/admin/users")
                 .header("Authorization", "Bearer " + tokenUser))
             .andExpect(status().isForbidden());
      }

      @Test
      @DisplayName("deve retornar 403 quando não autenticado")
      void deveRetornar403_quandoAnonimo() throws Exception {
         mockMvc.perform(get("/admin/users"))
             .andExpect(status().isForbidden());
      }
   }

   @Nested
   @DisplayName("GET /admin/users/{uuid}")
   class GetById {
      @Test
      @DisplayName("deve retornar 200 quando ADMIN busca qualquer user")
      void deveRetornar200_quandoAdmin() throws Exception {
         mockMvc.perform(get("/admin/users/" + userComum.getUuid())
                 .header("Authorization", "Bearer " + tokenAdmin))
             .andExpect(status().isOk())
             .andExpect(jsonPath("$.email").value("user@test.com"));
      }

      @Test
      @DisplayName("deve retornar 403 quando USER tenta buscar outro user")
      void deveRetornar403_quandoUser() throws Exception {
         mockMvc.perform(get("/admin/users/" + userComum.getUuid())
                 .header("Authorization", "Bearer " + tokenUser))
             .andExpect(status().isForbidden());
      }
   }

   @Nested
   @DisplayName("GET /admin/users/search")
   class GetByEmail {

      @Test
      @DisplayName("deve retornar 200 quando ADMIN busca por email existente")
      void deveRetornar200_quandoEmailExiste() throws Exception {
         mockMvc.perform(get("/admin/users/search")
                 .param("email", "user@test.com")
                 .header("Authorization", "Bearer " + tokenAdmin))
             .andExpect(status().isOk())
             .andExpect(jsonPath("$.email").value("user@test.com"));
      }

      @Test
      @DisplayName("deve retornar 404 quando email não existe")
      void deveRetornar404_quandoEmailNaoExiste() throws Exception {
         mockMvc.perform(get("/admin/users/search")
                 .param("email", "naoexiste@test.com")
                 .header("Authorization", "Bearer " + tokenAdmin))
             .andExpect(status().isNotFound());
      }
   }

   @Nested
   @DisplayName("DELETE /admin/users/{uuid}")
   class DeleteUser {

      @Test
      @DisplayName("deve retornar 204 quando ADMIN deleta user existente")
      void deveRetornar204_quandoAdmin() throws Exception {
         mockMvc.perform(delete("/admin/users/" + userComum.getUuid())
                 .header("Authorization", "Bearer " + tokenAdmin))
             .andExpect(status().isNoContent());
      }

      @Test
      @DisplayName("deve retornar 403 quando USER tenta deletar")
      void deveRetornar403_quandoUser() throws Exception {
         mockMvc.perform(delete("/admin/users/" + admin.getUuid())
                 .header("Authorization", "Bearer " + tokenUser))
             .andExpect(status().isForbidden());
      }
   }
}