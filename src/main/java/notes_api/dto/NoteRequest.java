package notes_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NoteRequest(
        @NotBlank
        @Size(max = 100, message = "O titulo deve ter no maximo 100 caracteres")
        String title,
        @NotBlank(message = "O conteúdo é obrigatório")
        String content
) {
}
