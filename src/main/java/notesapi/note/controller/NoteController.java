package notesapi.note.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import notesapi.note.dto.NoteRequest;
import notesapi.note.dto.NoteResponse;
import notesapi.note.service.NoteService;
import notesapi.user.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Notas", description = "CRUD de notas do usuário autenticado")
@RequiredArgsConstructor
@RequestMapping("/notes")
@RestController
public class NoteController {

    private final NoteService service;

    @Operation(summary = "Criar uma nova nota")
    @ApiResponse(responseCode = "201", description = "Nota criada com sucesso")
    @ApiResponse(responseCode = "400", description = "Body inválido (título ou conteúdo ausentes/malformados)")
    @PostMapping
    public ResponseEntity<NoteResponse> create(@Valid @RequestBody NoteRequest request,
                                               @AuthenticationPrincipal UserEntity user) {
        NoteResponse response = service.createNote(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Buscar nota por ID",
        description = "Retorna uma nota específica. USER vê apenas as próprias; ADMIN vê qualquer uma.")
    @ApiResponse(responseCode = "200", description = "Nota encontrada")
    @ApiResponse(responseCode = "404", description = "Nota não encontrada ou não pertence ao usuário")
    @GetMapping("/{id}")
    public ResponseEntity<NoteResponse> getById(@PathVariable Long id,
                                                @AuthenticationPrincipal UserEntity user) {
        NoteResponse response = service.findById(id, user);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar notas do usuário autenticado",
        description = "Retorna uma página com as notas do usuário. Aceita filtro por título/conteúdo (search).")
    @GetMapping
    public ResponseEntity<Page<NoteResponse>> getAllNotes(
        @RequestParam(required = false) String search,
        @PageableDefault(page = 0, size = 10, sort = "title") Pageable pageable,
        @AuthenticationPrincipal UserEntity user) {

        Page<NoteResponse> notes = service.getUserNotes(user.getUuid(), search, pageable);
        return ResponseEntity.ok(notes);
    }

    @Operation(summary = "Atualizar nota")
    @ApiResponse(responseCode = "200", description = "Nota atualizada com sucesso")
    @ApiResponse(responseCode = "400", description = "Body inválido")
    @ApiResponse(responseCode = "404", description = "Nota não encontrada ou não pertence ao usuário")
    @PutMapping("/{id}")
    public ResponseEntity<NoteResponse> update(@PathVariable("id") Long id,
                                               @Valid @RequestBody NoteRequest request,
                                               @AuthenticationPrincipal UserEntity user) {
        NoteResponse note = service.updateNote(id, request, user);
        return ResponseEntity.ok(note);
    }

    @Operation(summary = "Deletar nota")
    @ApiResponse(responseCode = "204", description = "Nota deletada com sucesso")
    @ApiResponse(responseCode = "404", description = "Nota não encontrada ou não pertence ao usuário")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @AuthenticationPrincipal UserEntity user) {
        service.deleteNote(id, user);
        return ResponseEntity.noContent().build();
    }
}