package notesapi.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Autenticação", description = "Login e geração de token JWT")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager manager;
    private final JwtService jwtService;

    @Operation(summary = "Login do usuário",
        description = "Autentica o usuário com email e senha, e retorna um token JWT válido por 24h.")
    @ApiResponse(responseCode = "200", description = "Login bem-sucedido, token retornado")
    @ApiResponse(responseCode = "400", description = "Body inválido (email ou senha ausentes/malformados)")
    @ApiResponse(responseCode = "401", description = "Credenciais inválidas (email ou senha incorretos)")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            request.email(), request.password());

        Authentication authentication = manager.authenticate(authToken);
        UserEntity user = (UserEntity) authentication.getPrincipal();
        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new LoginResponse(token));
    }
}