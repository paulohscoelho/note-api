package notesapi.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest(
        @NotBlank
        @Size(max = 100)
        @Email(message = "O email deve ter um formato válido")
        String email,
        @NotBlank
        @Size(min = 8, max = 20, message = "A senha deve ter entre 8 e 20 caracteres")
        String password
) {
}
