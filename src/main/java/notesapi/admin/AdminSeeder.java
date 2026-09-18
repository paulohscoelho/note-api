package notesapi.admin;
import lombok.RequiredArgsConstructor;
import notesapi.user.entity.Role;
import notesapi.user.entity.UserEntity;
import notesapi.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Value("${app.admin.email:}")
  private String adminEmail;

  @Value("${app.admin.password:}")
  private String adminPassword;

  @Override
  public void run(String... args) throws Exception {
    if (adminEmail.isBlank() || adminPassword.isBlank()){
      System.out.println("[AdminSeeder] ADMIN_EMAIL/ADMIN_PASSWORD não configurados");
    return;
  }
    if (userRepository.findByEmail(adminEmail).isPresent()){
      System.out.println("[AdminSeeder] Admin já existe: "+adminEmail);
      return;
    }

    UserEntity admin = new UserEntity();
    admin.setEmail(adminEmail);
    admin.setPassword(passwordEncoder.encode(adminPassword));
    admin.setRole(Role.ADMIN);
    userRepository.save(admin);

    System.out.println("[AdminSeeder] Admin criado: " + adminEmail);
}
}
