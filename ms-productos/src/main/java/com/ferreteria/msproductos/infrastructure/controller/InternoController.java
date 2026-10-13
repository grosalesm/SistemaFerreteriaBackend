package com.ferreteria.msproductos.infrastructure.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ferreteria.msproductos.application.port.usecase.ProductoUseCase;
import com.ferreteria.msproductos.application.port.usecase.ReservaUseCase;
import com.ferreteria.msproductos.domain.bean.Reserva;
import com.ferreteria.msproductos.infrastructure.dto.PrecioRequest;
import com.ferreteria.msproductos.infrastructure.dto.PrecioResponse;
import com.ferreteria.msproductos.infrastructure.dto.ReservaRequest;
import com.ferreteria.msproductos.infrastructure.dto.ReservaResponse;
import com.ferreteria.msproductos.infrastructure.mapper.ReservaWebMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
public class InternoController {

    private final ProductoUseCase productoUseCase;
    private final ReservaUseCase reservaUseCase;
    private final ReservaWebMapper reservaWebMapper;

    @PostMapping("/precios/calcular")
    public ResponseEntity<List<PrecioResponse>> calcularPrecios(@Valid @RequestBody PrecioRequest request) {
        return ResponseEntity.ok(productoUseCase.calcularPrecios(request));
    }

    @PostMapping("/reservas")
    public ResponseEntity<ReservaResponse> reservar(@Valid @RequestBody ReservaRequest request) {
        Reserva reserva = reservaUseCase.reservar(reservaWebMapper.toBean(request));
        return ResponseEntity.ok(reservaWebMapper.toResponse(reserva));
    }

    @DeleteMapping("/reservas/{cotizacionId}")
    public ResponseEntity<Void> liberar(@PathVariable Long cotizacionId) {
        if (reservaUseCase.liberar(cotizacionId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
