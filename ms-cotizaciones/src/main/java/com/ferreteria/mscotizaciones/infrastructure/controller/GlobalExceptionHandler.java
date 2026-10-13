package com.ferreteria.mscotizaciones.infrastructure.controller;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import com.ferreteria.mscotizaciones.infrastructure.dto.RespuestaMensaje;

import feign.FeignException;

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

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<?> manejarFeign(FeignException ex) {
        String cuerpo = ex.contentUTF8();
        if (cuerpo == null || cuerpo.isBlank()) {
            String mensaje = ex.status() == 404 ? "El recurso solicitado no existe" : "Error en un servicio externo";
            return responder(ex.status(), mensaje);
        }
        return ResponseEntity.status(ex.status()).contentType(MediaType.APPLICATION_JSON).body(cuerpo);
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
