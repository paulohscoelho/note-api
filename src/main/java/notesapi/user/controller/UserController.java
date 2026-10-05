package notesapi.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import notesapi.user.dto.MeResponse;
import notesapi.user.dto.UserRequest;
import notesapi.user.dto.UserResponse;
import notesapi.user.dto.UserWithNotesResponse;
import notesapi.user.entity.UserEntity;
import notesapi.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Usuários", description = "Cadastro, dados do usuário autenticado e suas notas")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @Operation(summary = "Retornar dados do usuário autenticado",
        description = "Retorna uuid, email e role do usuário identificado pelo token JWT.")
    @ApiResponse(responseCode = "200", description = "Dados do usuário autenticado")
    @GetMapping("/me")
    public ResponseEntity<MeResponse> getMe(@AuthenticationPrincipal UserEntity currentUser) {
        return ResponseEntity.ok(service.getMe(currentUser.getUuid()));
    }

    @Operation(summary = "Criar nova conta de usuário",
        description = "Cadastro público. Cria um usuário com role USER por padrão.")
    @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Body inválido (email malformado ou senha fora do tamanho)")
    @ApiResponse(responseCode = "409", description = "Email já cadastrado")
    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
        UserResponse user = service.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @Operation(summary = "Retornar usuário com suas notas",
        description = "USER pode ver apenas o próprio perfil; ADMIN pode ver qualquer um.")
    @ApiResponse(responseCode = "200", description = "Usuário e suas notas retornados")
    @ApiResponse(responseCode = "403", description = "USER tentando acessar dados de outro usuário")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    @GetMapping("/{uuid}/notes")
    @PreAuthorize("#uuid == authentication.principal.uuid or hasRole('ADMIN')")
    public ResponseEntity<UserWithNotesResponse> getWithNotes(@PathVariable UUID uuid) {
        return ResponseEntity.ok(service.getUserWithNotes(uuid));
    }
}