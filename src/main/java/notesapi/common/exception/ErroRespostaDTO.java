package notesapi.common.exception;


import java.time.LocalDateTime;

public record ErroRespostaDTO(
        LocalDateTime localDateTime,
        Integer status,
        String error,
        String message
) {
}
