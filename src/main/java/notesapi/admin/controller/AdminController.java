package notesapi.admin.controller;

import lombok.RequiredArgsConstructor;
import notesapi.admin.dto.AdminUserResponse;
import notesapi.note.dto.NoteResponse;
import notesapi.note.service.NoteService;
import notesapi.user.dto.UserResponse;
import notesapi.user.repository.UserRepository;
import notesapi.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

  private final UserRepository userRepository;
  private final UserService userService;
  private final NoteService noteService;

  @GetMapping("/users")
  public List<AdminUserResponse> listAllUsers() {
    return userRepository.findAll().stream()
        .map(AdminUserResponse::from)
        .toList();
  }

  @GetMapping("/users/{uuid}")
  public ResponseEntity<UserResponse> getById(@PathVariable UUID uuid) {
    return ResponseEntity.ok(userService.getUserById(uuid));
  }

  @GetMapping("/users/search")
  public ResponseEntity<UserResponse> getByEmail(@RequestParam String email) {
    return ResponseEntity.ok(userService.getUserByEmail(email));
  }

  @DeleteMapping("/users/{uuid}")
  public ResponseEntity<Void> delete(@PathVariable UUID uuid) {
    userService.removeUser(uuid);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/notes")
  public List<NoteResponse> listAllNotes() {
    return noteService.findAll();
  }
}