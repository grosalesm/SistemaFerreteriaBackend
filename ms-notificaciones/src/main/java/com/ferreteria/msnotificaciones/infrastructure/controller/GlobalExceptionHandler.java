package com.ferreteria.msnotificaciones.infrastructure.controller;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import com.ferreteria.msnotificaciones.infrastructure.dto.RespuestaMensaje;

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

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<RespuestaMensaje> manejarParametro(MethodArgumentTypeMismatchException ex) {
        return responder(HttpStatus.BAD_REQUEST.value(), "El parámetro " + ex.getName() + " no es válido");
    }

    private ResponseEntity<RespuestaMensaje> responder(int status, String mensaje) {
        return ResponseEntity.status(status).body(new RespuestaMensaje(status, mensaje, LocalDateTime.now()));
    }
}
