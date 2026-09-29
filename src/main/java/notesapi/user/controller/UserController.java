package notesapi.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import notesapi.user.dto.UserRequest;
import notesapi.user.dto.UserResponse;
import notesapi.user.dto.UserWithNotesResponse;
import notesapi.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
        UserResponse user = service.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @GetMapping("/{uuid}/notes")
    @PreAuthorize("#uuid == authentication.principal.uuid or hasRole('ADMIN')")
    public ResponseEntity<UserWithNotesResponse> getWithNotes(@PathVariable UUID uuid) {
        return ResponseEntity.ok(service.getUserWithNotes(uuid));
    }
}