package notesapi.admin.controller;

import lombok.RequiredArgsConstructor;
import notesapi.note.dto.NoteResponse;
import notesapi.note.service.NoteService;
import notesapi.user.dto.UserResponse;
import notesapi.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

  private final UserService userService;
  private final NoteService noteService;

  @GetMapping("/users")
  public ResponseEntity<Page<UserResponse>> listAllUsers(
      @RequestParam(required = false) String search,
      @PageableDefault(page = 0, size = 10, sort = "email") Pageable pageable) {

    Page<UserResponse> users = userService.getUsers(search, pageable);
    return ResponseEntity.ok(users);
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
  public ResponseEntity<Page<NoteResponse>> listAllNotes(
      @RequestParam(required = false) String search,
      @PageableDefault(page = 0, size = 10, sort = "title") Pageable pageable) {

    Page<NoteResponse> notes = noteService.getNotes(search, pageable);
    return ResponseEntity.ok(notes);
  }
}