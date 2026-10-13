package com.ferreteria.msauth.infrastructure.client;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;

import feign.FeignException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ClienteClientFallback implements FallbackFactory<ClienteClient> {

    private final ObjectMapper objectMapper;

    @Override
    public ClienteClient create(Throwable causa) {
        // En ambos casos se lanza una excepción para que el registro se cancele y no se cree el usuario
        return request -> {
            FeignException feignException = buscarFeignException(causa);
            if (feignException != null && feignException.status() >= 400 && feignException.status() < 500) {
                // Error del usuario (400, 409 documento duplicado...): se devuelve tal cual
                throw new ResponseStatusException(HttpStatus.valueOf(feignException.status()),
                        leerMensaje(feignException));
            }
            // Timeout, conexión rechazada o circuit breaker abierto
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Servicio de clientes no disponible");
        };
    }

    private FeignException buscarFeignException(Throwable causa) {
        while (causa != null) {
            if (causa instanceof FeignException feignException) {
                return feignException;
            }
            causa = causa.getCause();
        }
        return null;
    }

    // ms-clientes responde {"status":..., "mensaje": "...", "fecha": ...}; si no, se usa el cuerpo completo
    private String leerMensaje(FeignException ex) {
        String cuerpo = ex.contentUTF8();
        try {
            return objectMapper.readTree(cuerpo).path("mensaje").asText(cuerpo);
        } catch (Exception e) {
            return cuerpo;
        }
    }
}
