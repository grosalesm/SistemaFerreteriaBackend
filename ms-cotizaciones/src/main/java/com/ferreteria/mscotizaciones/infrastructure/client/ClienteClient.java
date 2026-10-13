package com.ferreteria.mscotizaciones.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-clientes", fallbackFactory = ClienteClientFallback.class)
public interface ClienteClient {

    @GetMapping("/internal/clientes/{id}/resumen")
    ClienteResumen obtenerResumen(@PathVariable("id") Long id);

    record ClienteResumen(Long id, String nombreRazonSocial, String tipoCliente, Boolean activo) {
    }
}
