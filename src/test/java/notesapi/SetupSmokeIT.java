package notesapi;
import notesapi.config.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Setup de testes de integração")
public class SetupSmokeIT extends AbstractIntegrationTest {
   @Test
   @DisplayName("deve subir contexto Spring e responder health check como UP")
   void contextLoads() throws Exception {
      mockMvc.perform(get("/actuator/health"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value("UP"));
   }
}
