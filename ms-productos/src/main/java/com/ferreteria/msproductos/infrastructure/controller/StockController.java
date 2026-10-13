package com.ferreteria.msproductos.infrastructure.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ferreteria.msproductos.application.port.usecase.StockUseCase;
import com.ferreteria.msproductos.infrastructure.dto.AjusteRequest;
import com.ferreteria.msproductos.infrastructure.dto.AlmacenResponse;
import com.ferreteria.msproductos.infrastructure.dto.IngresoRequest;
import com.ferreteria.msproductos.infrastructure.dto.MovimientoResponse;
import com.ferreteria.msproductos.infrastructure.dto.StockResponse;
import com.ferreteria.msproductos.infrastructure.mapper.StockWebMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class StockController {

    private final StockUseCase stockUseCase;
    private final StockWebMapper stockWebMapper;

    @GetMapping("/almacenes")
    public ResponseEntity<List<AlmacenResponse>> listarAlmacenes() {
        return ResponseEntity.ok(stockWebMapper.toAlmacenResponses(stockUseCase.listarAlmacenes()));
    }

    @GetMapping("/stock")
    public ResponseEntity<List<StockResponse>> listar(@RequestParam(required = false) Long idAlmacen) {
        return ResponseEntity.ok(stockWebMapper.toResponses(stockUseCase.listar(idAlmacen)));
    }

    @GetMapping("/stock/criticos")
    public ResponseEntity<List<StockResponse>> listarCriticos() {
        return ResponseEntity.ok(stockWebMapper.toResponses(stockUseCase.listarCriticos()));
    }

    @PostMapping("/stock/ajustes")
    public ResponseEntity<StockResponse> ajustar(@Valid @RequestBody AjusteRequest request) {
        return ResponseEntity.ok(stockWebMapper.toResponse(stockUseCase.ajustar(
                request.idProducto(), request.idAlmacen(), request.nuevaCantidad(), request.motivo())));
    }

    @PostMapping("/stock/ingresos")
    public ResponseEntity<StockResponse> ingresar(@Valid @RequestBody IngresoRequest request) {
        return ResponseEntity.ok(stockWebMapper.toResponse(stockUseCase.ingresar(
                request.idProducto(), request.idAlmacen(), request.cantidad())));
    }

    @GetMapping("/productos/{id}/kardex")
    public ResponseEntity<List<MovimientoResponse>> kardex(@PathVariable Long id) {
        return ResponseEntity.ok(stockWebMapper.toMovimientoResponses(stockUseCase.kardex(id)));
    }
}
