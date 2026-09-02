package notesapi.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import notesapi.user.dto.UserRequest;
import notesapi.user.dto.UserResponse;
import notesapi.user.dto.UserWithNotesResponse;
import notesapi.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request){
        UserResponse user = service.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @GetMapping("/{uuid}/notes")
    public ResponseEntity<UserWithNotesResponse> getWithNotes(@PathVariable UUID uuid){
        return ResponseEntity.ok(service.getUserWithNotes(uuid));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<UserResponse> getById(@PathVariable UUID uuid){
        return ResponseEntity.ok(service.getUserById(uuid));
    }

    @GetMapping("/search")
    public ResponseEntity<UserResponse> getByEmail(@RequestParam String email){
        return ResponseEntity.ok(service.getUserByEmail(email));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> delete(@PathVariable UUID uuid){
        service.removeUser(uuid);
        return ResponseEntity.noContent().build();
    }
}
