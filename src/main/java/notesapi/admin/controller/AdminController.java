package notesapi.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import notesapi.admin.dto.AdminUserResponse;
import notesapi.admin.dto.RoleUpdateRequest;
import notesapi.note.dto.NoteResponse;
import notesapi.note.service.NoteService;
import notesapi.user.dto.UserResponse;
import notesapi.user.entity.UserEntity;
import notesapi.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Admin", description = "Endpoints administrativos (requer role ADMIN)")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

  private final UserService userService;
  private final NoteService noteService;

  @Operation(summary = "Alterar role de um usuário")
  @ApiResponse(responseCode = "200", description = "Role atualizada com sucesso")
  @ApiResponse(responseCode = "400", description = "Role inválida no body")
  @ApiResponse(responseCode = "404", description = "Usuário alvo não encontrado")
  @ApiResponse(responseCode = "409", description = "Tentativa de alterar a própria role")
  @PatchMapping("/users/{uuid}/role")
  public ResponseEntity<AdminUserResponse> updateRole(
      @PathVariable UUID uuid,
      @Valid @RequestBody RoleUpdateRequest request,
      @AuthenticationPrincipal UserEntity currentUser) {

    AdminUserResponse response = userService.updateRole(uuid, request.role(), currentUser);
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Listar todos os usuários",
      description = "Retorna uma página de usuários. Aceita filtro por email (search) e paginação.")
  @GetMapping("/users")
  public ResponseEntity<Page<UserResponse>> listAllUsers(
      @RequestParam(required = false) String search,
      @PageableDefault(page = 0, size = 10, sort = "email") Pageable pageable) {

    Page<UserResponse> users = userService.getUsers(search, pageable);
    return ResponseEntity.ok(users);
  }

  @Operation(summary = "Buscar usuário por UUID")
  @ApiResponse(responseCode = "200", description = "Usuário encontrado")
  @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
  @GetMapping("/users/{uuid}")
  public ResponseEntity<UserResponse> getUserById(@PathVariable UUID uuid) {
    return ResponseEntity.ok(userService.getUserById(uuid));
  }

  @Operation(summary = "Buscar usuário por email")
  @ApiResponse(responseCode = "200", description = "Usuário encontrado")
  @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
  @GetMapping("/users/search")
  public ResponseEntity<UserResponse> getByEmail(@RequestParam String email) {
    return ResponseEntity.ok(userService.getUserByEmail(email));
  }

  @Operation(summary = "Deletar usuário")
  @ApiResponse(responseCode = "204", description = "Usuário deletado com sucesso")
  @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
  @DeleteMapping("/users/{uuid}")
  public ResponseEntity<Void> delete(@PathVariable UUID uuid) {
    userService.removeUser(uuid);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Listar todas as notas",
      description = "Retorna uma página com todas as notas do sistema. Aceita filtro por título/conteúdo (search).")
  @GetMapping("/notes")
  public ResponseEntity<Page<NoteResponse>> listAllNotes(
      @RequestParam(required = false) String search,
      @PageableDefault(page = 0, size = 10, sort = "title") Pageable pageable) {

    Page<NoteResponse> notes = noteService.getNotes(search, pageable);
    return ResponseEntity.ok(notes);
  }
}