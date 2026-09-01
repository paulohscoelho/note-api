package notesapi.note.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import notesapi.note.dto.NoteRequest;
import notesapi.note.dto.NoteResponse;
import notesapi.note.service.NoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/notes")
@RestController
public class NoteController {

    private final NoteService service;

    @PostMapping
    public ResponseEntity<NoteResponse> create(@Valid @RequestBody NoteRequest request,
                                               @RequestHeader("X-User-Id") UUID userId){
        NoteResponse response = service.createNote(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteResponse> getById(@PathVariable Long id,
                                                @RequestHeader("X-User-Id") UUID userId){
        NoteResponse response = service.findByIdAndUserUuid(id, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<NoteResponse>> getAll(@RequestHeader("X-User-Id") UUID userId){
        List<NoteResponse> allNotes = service.findAllByUserUuid(userId);
        return ResponseEntity.ok(allNotes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteResponse> update(@PathVariable("id") Long id ,@Valid @RequestBody NoteRequest request,@RequestHeader("X-User-Id") UUID userId){
        NoteResponse note = service.updateNote(id,request,userId);
        return ResponseEntity.ok(note);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,@RequestHeader("X-User-Id") UUID userId){
        service.deleteNote(id,userId);
        return ResponseEntity.noContent().build();
    }
}
