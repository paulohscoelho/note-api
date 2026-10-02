package notesapi.note.controller;

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

import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/notes")
@RestController
public class NoteController {

    private final NoteService service;

    @PostMapping
    public ResponseEntity<NoteResponse> create(@Valid @RequestBody NoteRequest request,
                                               @AuthenticationPrincipal UserEntity user){
        NoteResponse response = service.createNote(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteResponse> getById(@PathVariable Long id,
                                                @AuthenticationPrincipal UserEntity user){
        NoteResponse response = service.findById(id, user);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<NoteResponse>> getAllNotes(
        @RequestParam(required = false) String search,
        @PageableDefault(page = 0, size = 10, sort = "title") Pageable pageable,
        @AuthenticationPrincipal UserEntity user) {

        Page<NoteResponse> notes = service.getUserNotes(user.getUuid(), search, pageable);
        return ResponseEntity.ok(notes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteResponse> update(@PathVariable("id") Long id ,
                                               @Valid @RequestBody NoteRequest request,
                                               @AuthenticationPrincipal UserEntity user){
        NoteResponse note = service.updateNote(id,request,user);
        return ResponseEntity.ok(note);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,@AuthenticationPrincipal UserEntity user){
        service.deleteNote(id,user);
        return ResponseEntity.noContent().build();
    }
}