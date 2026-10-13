package com.ferreteria.mscotizaciones.infrastructure.client;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import feign.FeignException;

@Component
public class ClienteClientFallback implements FallbackFactory<ClienteClient> {

    @Override
    public ClienteClient create(Throwable causa) {
        RuntimeException error = traducir(causa);
        return id -> {
            throw error;
        };
    }

    private RuntimeException traducir(Throwable causa) {
        while (causa != null) {
            if (causa instanceof FeignException ex && ex.status() >= 400 && ex.status() < 500) {
                return ex;
            }
            causa = causa.getCause();
        }
        return new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                "No se pudo contactar a ms-clientes, intente luego");
    }
}
