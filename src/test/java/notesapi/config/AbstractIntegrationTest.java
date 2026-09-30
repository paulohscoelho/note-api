package notesapi.config;

import notesapi.note.entity.NoteEntity;
import notesapi.note.repository.NoteRepository;
import notesapi.security.JwtService;
import notesapi.user.entity.Role;
import notesapi.user.entity.UserEntity;
import notesapi.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class AbstractIntegrationTest {

   @ServiceConnection
   static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");

   static {
      postgres.start();   //sobe manualmente.
   }

   @Autowired protected MockMvc mockMvc;
   @Autowired protected UserRepository userRepository;
   @Autowired protected NoteRepository noteRepository;
   @Autowired protected PasswordEncoder passwordEncoder;
   @Autowired protected JwtService jwtService;

   @BeforeEach
   void limparBanco() {
      noteRepository.deleteAll();
      userRepository.deleteAll();
   }

   protected UserEntity criarUser(String email, Role role) {
      UserEntity user = new UserEntity();
      user.setEmail(email);
      user.setPassword(passwordEncoder.encode("senha12345"));
      user.setRole(role);
      return userRepository.saveAndFlush(user);
   }

   protected NoteEntity criarNota(String title, String content, UserEntity owner) {
      NoteEntity nota = new NoteEntity();
      nota.setTitle(title);
      nota.setContent(content);
      nota.setUser(owner);
      return noteRepository.saveAndFlush(nota);
   }

   protected String gerarToken(UserEntity user) {
      return jwtService.generateToken(user);
   }
}