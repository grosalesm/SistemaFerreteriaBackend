package com.ferreteria.msnotificaciones.infrastructure.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ferreteria.msnotificaciones.application.port.usecase.DashboardUseCase;
import com.ferreteria.msnotificaciones.infrastructure.dto.ResumenResponse;
import com.ferreteria.msnotificaciones.infrastructure.dto.TopClienteResponse;
import com.ferreteria.msnotificaciones.infrastructure.dto.TopProductoResponse;
import com.ferreteria.msnotificaciones.infrastructure.dto.VentaDiaResponse;
import com.ferreteria.msnotificaciones.infrastructure.mapper.NotificacionWebMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardUseCase dashboardUseCase;
    private final NotificacionWebMapper notificacionWebMapper;

    @GetMapping("/resumen")
    public ResponseEntity<ResumenResponse> resumen() {
        return ResponseEntity.ok(dashboardUseCase.resumen());
    }

    @GetMapping("/ventas")
    public ResponseEntity<List<VentaDiaResponse>> ventas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(notificacionWebMapper.toVentasDia(dashboardUseCase.ventasPorDia(desde, hasta)));
    }

    @GetMapping("/top-productos")
    public ResponseEntity<List<TopProductoResponse>> topProductos() {
        return ResponseEntity.ok(notificacionWebMapper.toTopProductos(dashboardUseCase.topProductos()));
    }

    @GetMapping("/top-clientes")
    public ResponseEntity<List<TopClienteResponse>> topClientes() {
        return ResponseEntity.ok(notificacionWebMapper.toTopClientes(dashboardUseCase.topClientes()));
    }
}
