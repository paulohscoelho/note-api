package notesapi.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import notesapi.auth.dto.LoginRequest;
import notesapi.auth.dto.LoginResponse;
import notesapi.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager manager;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request){
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                request.email(), request.password());

        manager.authenticate(authToken);
        String token = jwtService.generateToken(request.email());
        return ResponseEntity.ok(new LoginResponse(token));
    }
}
