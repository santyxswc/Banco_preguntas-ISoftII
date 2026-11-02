package co.unicauca.iso2.taller09.questionservice.controller;

import co.unicauca.iso2.taller09.questionservice.domain.QuestionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;

/** Traduce los errores de negocio a respuestas JSON con el código HTTP correcto. */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(QuestionException.class)
    public ResponseEntity<Map<String, Object>> handle(QuestionException e) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", e.httpStatus());
        body.put("message", e.getMessage());
        if (!e.errors().isEmpty()) {
            body.put("errors", e.errors());
        }
        return ResponseEntity.status(e.httpStatus()).body(body);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<Map<String, Object>> badRequest(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("status", 400, "message", "Solicitud inválida: " + e.getMessage()));
    }
}
