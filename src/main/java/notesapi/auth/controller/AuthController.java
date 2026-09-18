package notesapi.auth.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import notesapi.auth.dto.LoginRequest;
import notesapi.auth.dto.LoginResponse;
import notesapi.security.JwtService;
import notesapi.user.entity.UserEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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

        Authentication authentication = manager.authenticate(authToken);
        UserEntity user = (UserEntity) authentication.getPrincipal();
        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new LoginResponse(token));
    }
}
