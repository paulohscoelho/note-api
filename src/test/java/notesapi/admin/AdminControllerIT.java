package notesapi.admin;

import notesapi.config.AbstractIntegrationTest;
import notesapi.user.entity.Role;
import notesapi.user.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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
      admin = criarUser("admin@test.com", Role.ADMIN);
      userComum = criarUser("user@test.com", Role.USER);
      tokenAdmin = gerarToken(admin);
      tokenUser = gerarToken(userComum);
   }

   @Nested
   @DisplayName("GET /admin/users")
   class ListUsers {

      @Test
      @DisplayName("deve retornar 200 quando autenticado como ADMIN")
      void deveRetornar200_quandoAdmin() throws Exception {
         mockMvc.perform(get("/admin/users")
                 .header("Authorization", "Bearer " + tokenAdmin))
             .andExpect(status().isOk())
             .andExpect(jsonPath("$").isArray());
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
   class GetById{
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
