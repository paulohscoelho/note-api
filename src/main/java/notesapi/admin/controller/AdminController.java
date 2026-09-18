package notesapi.admin.controller;

import lombok.RequiredArgsConstructor;
import notesapi.admin.dto.AdminUserResponse;
import notesapi.user.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
  private final UserRepository userRepository;


  @GetMapping("/users")
  @PreAuthorize("hasRole('ADMIN')")
  public List<AdminUserResponse> listAllUsers(){
    return userRepository.findAll()
        .stream()
        .map(AdminUserResponse::from)
        .toList();
  }
}
