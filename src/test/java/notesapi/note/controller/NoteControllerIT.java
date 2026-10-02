package notesapi.note.controller;

import notesapi.config.AbstractIntegrationTest;
import notesapi.note.entity.NoteEntity;
import notesapi.user.entity.Role;
import notesapi.user.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@DisplayName("NoteController (integração)")
public class NoteControllerIT extends AbstractIntegrationTest {

   private UserEntity jorge;
   private UserEntity joao;
   private UserEntity admin;
   private String tokenJorge;
   private String tokenJoao;
   private String tokenAdmin;
   private NoteEntity notaDoJorge;
   private NoteEntity notaDoJoao;

   @BeforeEach
   void setUp() {
      jorge = createUser("jorge@test.com", Role.USER);
      joao = createUser("joao@test.com", Role.USER);
      admin = createUser("admin@test.com", Role.ADMIN);
      tokenJorge = gerarToken(jorge);
      tokenJoao = gerarToken(joao);
      tokenAdmin = gerarToken(admin);

      notaDoJorge = createNote("Nota do Jorge", "conteúdo do jorge", jorge);
      notaDoJoao = createNote("Nota do Joao", "conteúdo do joao", joao);
   }

   @Nested
   @DisplayName("GET /notes")
   class FindAll {

      @Test
      @DisplayName("deve retornar apenas as notas do user autenticado")
      void deveRetornarApenasNotasDoUser() throws Exception {
         mockMvc.perform(get("/notes")
                 .header("Authorization", "Bearer " + tokenJorge))
             .andExpect(status().isOk())
             .andExpect(jsonPath("$.content").isArray())
             .andExpect(jsonPath("$.content.length()").value(1))
             .andExpect(jsonPath("$.content[0].title").value("Nota do Jorge"))
             .andExpect(jsonPath("$.totalElements").value(1));
      }


      @Test
      @DisplayName("deve retornar 403 quando não autenticado")
      void deveRetornar403_quandoAnonimo() throws Exception {
         mockMvc.perform(get("/notes"))
             .andExpect(status().isForbidden());
      }
   }

   @Nested
   @DisplayName("GET /notes/{id}")
   class FindById {

      @Test
      @DisplayName("deve retornar 200 quando USER busca a própria nota")
      void deveRetornar200_quandoPropriaNota() throws Exception {
         mockMvc.perform(get("/notes/" + notaDoJorge.getId())
                 .header("Authorization", "Bearer " + tokenJorge))
             .andExpect(status().isOk())
             .andExpect(jsonPath("$.title").value("Nota do Jorge"));
      }

      @Test
      @DisplayName("deve retornar 404 quando USER busca nota de outro")
      void deveRetornar404_quandoNotaDeOutro() throws Exception {
         mockMvc.perform(get("/notes/" + notaDoJoao.getId())
                 .header("Authorization", "Bearer " + tokenJorge))
             .andExpect(status().isNotFound());
      }

      @Test
      @DisplayName("deve retornar 200 quando ADMIN busca nota de qualquer user")
      void deveRetornar200_quandoAdmin() throws Exception {
         mockMvc.perform(get("/notes/" + notaDoJoao.getId())
                 .header("Authorization", "Bearer " + tokenAdmin))
             .andExpect(status().isOk())
             .andExpect(jsonPath("$.title").value("Nota do Joao"));
      }
   }

   @Nested
   @DisplayName("POST /notes")
   class Create {

      @Test
      @DisplayName("deve retornar 201 e associar a nota ao user autenticado")
      void deveRetornar201_quandoUserAutenticado() throws Exception {
         String json = """
                    {
                      "title": "Nova nota",
                      "content": "Conteúdo novo"
                    }
                    """;

         mockMvc.perform(post("/notes")
                 .contentType(MediaType.APPLICATION_JSON)
                 .content(json)
                 .header("Authorization", "Bearer " + tokenJorge))
             .andExpect(status().isCreated())
             .andExpect(jsonPath("$.title").value("Nova nota"))
             .andExpect(jsonPath("$.user.email").value("jorge@test.com"));
      }
   }

   @Nested
   @DisplayName("PUT /notes/{id}")
   class Update {

      @Test
      @DisplayName("deve retornar 200 quando USER atualiza a própria nota")
      void deveRetornar200_quandoPropriaNota() throws Exception {
         String json = """
                    {
                      "title": "Título atualizado",
                      "content": "Conteúdo atualizado"
                    }
                    """;

         mockMvc.perform(put("/notes/" + notaDoJorge.getId())
                 .contentType(MediaType.APPLICATION_JSON)
                 .content(json)
                 .header("Authorization", "Bearer " + tokenJorge))
             .andExpect(status().isOk())
             .andExpect(jsonPath("$.title").value("Título atualizado"));
      }

      @Test
      @DisplayName("deve retornar 404 quando USER tenta atualizar nota de outro")
      void deveRetornar404_quandoNotaDeOutro() throws Exception {
         String json = """
                    {
                      "title": "Tentativa",
                      "content": "Não deveria funcionar"
                    }
                    """;

         mockMvc.perform(put("/notes/" + notaDoJoao.getId())
                 .contentType(MediaType.APPLICATION_JSON)
                 .content(json)
                 .header("Authorization", "Bearer " + tokenJorge))
             .andExpect(status().isNotFound());
      }
   }

   @Nested
   @DisplayName("DELETE /notes/{id}")
   class Delete {

      @Test
      @DisplayName("deve retornar 204 quando USER deleta a própria nota")
      void deveRetornar204_quandoPropriaNota() throws Exception {
         mockMvc.perform(delete("/notes/" + notaDoJorge.getId())
                 .header("Authorization", "Bearer " + tokenJorge))
             .andExpect(status().isNoContent());
      }

      @Test
      @DisplayName("deve retornar 404 quando USER tenta deletar nota de outro")
      void deveRetornar404_quandoNotaDeOutro() throws Exception {
         mockMvc.perform(delete("/notes/" + notaDoJoao.getId())
                 .header("Authorization", "Bearer " + tokenJorge))
             .andExpect(status().isNotFound());
      }
   }



}
