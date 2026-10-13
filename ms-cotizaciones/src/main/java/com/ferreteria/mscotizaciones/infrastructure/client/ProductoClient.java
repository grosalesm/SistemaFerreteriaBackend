package com.ferreteria.mscotizaciones.infrastructure.client;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-productos", fallbackFactory = ProductoClientFallback.class)
public interface ProductoClient {

    @PostMapping("/internal/precios/calcular")
    List<PrecioResponse> calcularPrecios(@RequestBody PrecioRequest request);

    @PostMapping("/internal/reservas")
    ReservaResponse reservar(@RequestBody ReservaRequest request);

    @DeleteMapping("/internal/reservas/{cotizacionId}")
    void liberarReserva(@PathVariable("cotizacionId") Long cotizacionId);

    record Item(Long idProducto, Integer cantidad) {
    }

    record PrecioRequest(String tipoCliente, Long idAlmacen, List<Item> items) {
    }

    record PrecioResponse(Long idProducto, String sku, String nombre, BigDecimal precioUnitario,
                          BigDecimal porcentajeDescuento, BigDecimal subtotal, Integer disponible) {
    }

    record ReservaRequest(Long cotizacionId, Long idAlmacen, List<Item> items) {
    }

    record ReservaResponse(Long idReserva, Long cotizacionId, Long idAlmacen, String estado) {
    }
}
