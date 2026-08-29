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

@RequiredArgsConstructor
@RequestMapping("/notes")
@RestController
public class NoteController {

    private final NoteService service;

    @PostMapping
    public ResponseEntity<NoteResponse> create(@Valid @RequestBody NoteRequest request){
        NoteResponse response = service.createNote(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteResponse> getById(@PathVariable Long id){
        NoteResponse response = service.getId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<NoteResponse>> getAll(@RequestParam(name = "title", required = false) String title){
        if (StringUtils.hasText(title)){
            List<NoteResponse> filteredNotes = service.findByTitle(title);
            return ResponseEntity.ok(filteredNotes);
        }
        List<NoteResponse> allNotes = service.findAll();
        return ResponseEntity.ok(allNotes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteResponse> update(@PathVariable("id") Long id ,@Valid @RequestBody NoteRequest request){
        NoteResponse note = service.updateNote(id,request);
        return ResponseEntity.ok(note);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.deleteNote(id);
        return ResponseEntity.noContent().build();
    }



}
