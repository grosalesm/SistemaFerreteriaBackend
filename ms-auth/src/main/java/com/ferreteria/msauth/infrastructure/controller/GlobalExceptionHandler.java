package com.ferreteria.msauth.infrastructure.controller;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import com.ferreteria.msauth.infrastructure.dto.RespuestaMensaje;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<RespuestaMensaje> manejarIllegalArgument(IllegalArgumentException ex) {
        return responder(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<RespuestaMensaje> manejarResponseStatus(ResponseStatusException ex) {
        return responder(ex.getStatusCode().value(), ex.getReason());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespuestaMensaje> manejarValidacion(MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return responder(HttpStatus.BAD_REQUEST.value(), mensaje);
    }

    private ResponseEntity<RespuestaMensaje> responder(int status, String mensaje) {
        return ResponseEntity.status(status).body(new RespuestaMensaje(status, mensaje, LocalDateTime.now()));
    }
}
