package com.ferreteria.msauth.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-clientes", fallbackFactory = ClienteClientFallback.class)
public interface ClienteClient {

    @PostMapping("/internal/clientes")
    ClienteResponse crearCliente(@RequestBody ClienteRequest request);

    record ClienteRequest(
            String tipoDocumento,
            String numeroDocumento,
            String nombreRazonSocial,
            String telefono,
            String email) {
    }

    record ClienteResponse(Long id) {
    }
}
