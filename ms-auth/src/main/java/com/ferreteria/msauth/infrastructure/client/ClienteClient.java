package com.ferreteria.msauth.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-clientes", fallbackFactory = ClienteClientFallback.class)
public interface ClienteClient {

    @PostMapping("/internal/clientes")
    ClienteResponse crearCliente(@RequestBody ClienteRequest request);

    @GetMapping("/internal/clientes/documento/{numeroDocumento}")
    ClienteResponse obtenerPorDocumento(@PathVariable("numeroDocumento") String numeroDocumento);

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
