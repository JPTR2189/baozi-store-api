package com.baozistore.api.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

/**
 * Tratamento centralizado de erros da API.
 *
 * Sem esta classe, um recurso inexistente devolveria uma pagina de erro
 * generica e uma excecao vazaria stack trace no corpo da resposta. Com ela,
 * todo erro sai em JSON, num formato unico e previsivel - o que aparece
 * de forma limpa nos prints do Postman.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** Monta o corpo padrao de erro da API. */
    private Map<String, Object> corpo(HttpStatus status, String mensagem, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("erro", status.getReasonPhrase());
        body.put("mensagem", mensagem);
        body.put("caminho", request.getDescription(false).replace("uri=", ""));
        return body;
    }

    /** 404 - recurso inexistente. */
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> tratarNaoEncontrado(
            RecursoNaoEncontradoException ex, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(corpo(HttpStatus.NOT_FOUND, ex.getMessage(), request));
    }

    /** 409 - violacao de regra de negocio. */
    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<Map<String, Object>> tratarRegraDeNegocio(
            RegraDeNegocioException ex, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(corpo(HttpStatus.CONFLICT, ex.getMessage(), request));
    }

    /** 400 - corpo JSON invalido segundo as anotacoes de Bean Validation. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> tratarValidacao(
            MethodArgumentNotValidException ex, WebRequest request) {

        Map<String, String> campos = new LinkedHashMap<>();
        for (FieldError erro : ex.getBindingResult().getFieldErrors()) {
            campos.put(erro.getField(), erro.getDefaultMessage());
        }

        Map<String, Object> body = corpo(HttpStatus.BAD_REQUEST,
                "Um ou mais campos estao invalidos", request);
        body.put("campos", campos);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
