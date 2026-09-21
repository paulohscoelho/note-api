package notesapi.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroRespostaDTO> tratarRecursoNaoEncontrado(
        RecursoNaoEncontradoException ex, HttpServletRequest request
    ) {
        ErroRespostaDTO erro = new ErroRespostaDTO(
            LocalDateTime.now(),
            HttpStatus.NOT_FOUND.value(),
            "Not Found",
            ex.getMessage(),
            request.getRequestURI(),
            List.of()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroRespostaDTO> tratarRegraDeNegocio(
        RegraNegocioException ex, HttpServletRequest request) {
        ErroRespostaDTO erro = new ErroRespostaDTO(
            LocalDateTime.now(),
            HttpStatus.CONFLICT.value(),
            "Conflict",
            ex.getMessage(),
            request.getRequestURI(),
            List.of()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroRespostaDTO> tratarErroValidacao(
        MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<ErroRespostaDTO.FieldErrorDTO> campos = ex.getBindingResult().getFieldErrors()
            .stream()
            .map(fe -> new ErroRespostaDTO.FieldErrorDTO(fe.getField(), fe.getDefaultMessage()))
            .toList();

        ErroRespostaDTO erro = new ErroRespostaDTO(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            "um ou mais campos estão inválidos.",
            request.getRequestURI(),
            campos
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroRespostaDTO> tratarErroJsonInvalido(
        HttpMessageNotReadableException ex, HttpServletRequest request) {
        ErroRespostaDTO erro = new ErroRespostaDTO(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            "o corpo da requisição está malformado ou ausente.",
            request.getRequestURI(),
            List.of()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroRespostaDTO> tratarErroTipoParametro(
        MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        ErroRespostaDTO erro = new ErroRespostaDTO(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            "o parâmetro '" + ex.getName() + "' tem formato inválido.",
            request.getRequestURI(),
            List.of()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErroRespostaDTO> tratarAcessoNegado(
        AccessDeniedException ex, HttpServletRequest request) {
        ErroRespostaDTO erro = new ErroRespostaDTO(
            LocalDateTime.now(),
            HttpStatus.FORBIDDEN.value(),
            "Forbidden",
            "você não tem permissão para acessar este recurso.",
            request.getRequestURI(),
            List.of()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(erro);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErroRespostaDTO> tratarFalhaAutenticacao(
        AuthenticationException ex, HttpServletRequest request) {
        ErroRespostaDTO erro = new ErroRespostaDTO(
            LocalDateTime.now(),
            HttpStatus.UNAUTHORIZED.value(),
            "Unauthorized",
            "email ou senha inválidos.",
            request.getRequestURI(),
            List.of()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(erro);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroRespostaDTO> tratarErroGenerico(
        Exception ex, HttpServletRequest request) {
        log.error("Erro não tratado em {}: ", request.getRequestURI(), ex);
        ErroRespostaDTO erro = new ErroRespostaDTO(
            LocalDateTime.now(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            "erro inesperado. tente novamente mais tarde.",
            request.getRequestURI(),
            List.of()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }


}