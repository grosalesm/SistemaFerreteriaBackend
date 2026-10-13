package com.ferreteria.mscotizaciones.infrastructure.client;

import java.util.List;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import feign.FeignException;

@Component
public class ProductoClientFallback implements FallbackFactory<ProductoClient> {

    @Override
    public ProductoClient create(Throwable causa) {
        RuntimeException error = traducir(causa);
        return new ProductoClient() {
            @Override
            public List<PrecioResponse> calcularPrecios(PrecioRequest request) {
                throw error;
            }

            @Override
            public ReservaResponse reservar(ReservaRequest request) {
                throw error;
            }

            @Override
            public void liberarReserva(Long cotizacionId) {
                throw error;
            }
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
                "No se pudo contactar a ms-productos, intente luego");
    }
}
