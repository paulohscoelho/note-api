package notes_api.exception;


import java.time.LocalDateTime;

public record ErroRespostaDTO(
        LocalDateTime localDateTime,
        Integer status,
        String error,
        String message
) {
}
