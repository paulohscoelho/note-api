package notesapi.note.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import notesapi.note.dto.NoteRequest;
import notesapi.note.dto.NoteResponse;
import notesapi.note.service.NoteService;
import notesapi.user.entity.UserEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/notes")
@RestController
public class NoteController {

    private final NoteService service;

    @PostMapping
    public ResponseEntity<NoteResponse> create(@Valid @RequestBody NoteRequest request,
                                               @AuthenticationPrincipal UserEntity user){
        NoteResponse response = service.createNote(request, user.getUuid());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteResponse> getById(@PathVariable Long id,
                                                @AuthenticationPrincipal UserEntity user){
        NoteResponse response = service.findByIdAndUserUuid(id, user.getUuid());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<NoteResponse>> getAllNotes(@AuthenticationPrincipal UserEntity user){
        List<NoteResponse> allNotes = service.findAllByUserUuid(user.getUuid());
        return ResponseEntity.ok(allNotes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteResponse> update(@PathVariable("id") Long id ,
                                               @Valid @RequestBody NoteRequest request,
                                               @AuthenticationPrincipal UserEntity user){
        NoteResponse note = service.updateNote(id,request,user.getUuid());
        return ResponseEntity.ok(note);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,@AuthenticationPrincipal UserEntity user){
        service.deleteNote(id,user.getUuid());
        return ResponseEntity.noContent().build();
    }
}
