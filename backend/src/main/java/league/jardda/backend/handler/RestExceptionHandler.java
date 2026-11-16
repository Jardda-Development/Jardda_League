package league.jardda.backend.handler;

import league.jardda.backend.exception.ExceptionDetails;
import league.jardda.backend.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionDetails> notFoundExceptionHandler(NotFoundException notFound) {
        ExceptionDetails exceptionDetails = new ExceptionDetails(
                "Recurso não encontrado",
                HttpStatus.NOT_FOUND.value(),
                notFound.getMessage(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(
                exceptionDetails,
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDetails> tratarErroInterno(Exception ex) {
        log.error("Erro inesperado", ex);

        ExceptionDetails corpo = new ExceptionDetails(
                "Erro interno",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Ocorreu um erro inesperado",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(corpo);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, @Nullable Object body, HttpHeaders headers,
            HttpStatusCode statusCode, WebRequest request) {

        String details = (body instanceof ProblemDetail problema && problema.getDetail() != null) ? problema.getDetail() : "Não foi possível processar a requisição";

        ExceptionDetails corpo = new ExceptionDetails(
                HttpStatus.valueOf(statusCode.value()).getReasonPhrase(),
                statusCode.value(),
                details,
                LocalDateTime.now()
        );

        return ResponseEntity.status(statusCode).headers(headers).body(corpo);
    }
}
