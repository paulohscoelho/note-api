package notesapi.common.exception;


import java.time.LocalDateTime;
import java.util.List;

public record ErroRespostaDTO(
        LocalDateTime timestamp,
        Integer status,
        String error,
        String message,
        String path,
        List<FieldErrorDTO> fields
) {
  public record FieldErrorDTO(String field, String message){

  }
}
