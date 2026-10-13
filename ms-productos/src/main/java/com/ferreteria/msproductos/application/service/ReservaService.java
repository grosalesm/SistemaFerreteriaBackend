package com.ferreteria.msproductos.application.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ferreteria.msproductos.application.port.outservice.ProductoOutService;
import com.ferreteria.msproductos.application.port.outservice.ReservaOutService;
import com.ferreteria.msproductos.application.port.outservice.StockOutService;
import com.ferreteria.msproductos.application.port.usecase.ReservaUseCase;
import com.ferreteria.msproductos.application.port.usecase.StockUseCase;
import com.ferreteria.msproductos.domain.bean.EstadoReserva;
import com.ferreteria.msproductos.domain.bean.MovimientoStock;
import com.ferreteria.msproductos.domain.bean.Producto;
import com.ferreteria.msproductos.domain.bean.Reserva;
import com.ferreteria.msproductos.domain.bean.ReservaDetalle;
import com.ferreteria.msproductos.domain.bean.Stock;
import com.ferreteria.msproductos.domain.bean.TipoMovimiento;
import com.ferreteria.msproductos.domain.exception.StockInsuficienteException;
import com.ferreteria.msproductos.domain.exception.StockInsuficienteException.Faltante;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservaService implements ReservaUseCase {

    private final ReservaOutService reservaOutService;
    private final StockOutService stockOutService;
    private final ProductoOutService productoOutService;
    private final StockUseCase stockUseCase;

    @Override
    @Transactional
    public Reserva reservar(Reserva reserva) {
        Optional<Reserva> existente = reservaOutService.obtenerPorCotizacionId(reserva.getCotizacionId());
        if (existente.isPresent()) {
            return existente.get();
        }
        if (stockOutService.obtenerAlmacen(reserva.getIdAlmacen()).isEmpty()) {
            throw new IllegalArgumentException("El almacén no existe");
        }

        List<Faltante> faltantes = new ArrayList<>();
        for (ReservaDetalle detalle : reserva.getDetalles()) {
            Producto producto = productoOutService.obtenerPorId(detalle.getIdProducto())
                    .filter(p -> Boolean.TRUE.equals(p.getActivo()))
                    .orElseThrow(() -> new IllegalArgumentException(
                            "El producto " + detalle.getIdProducto() + " no existe o está inactivo"));
            int filas = stockOutService.reservar(detalle.getIdProducto(), reserva.getIdAlmacen(), detalle.getCantidad());
            if (filas == 0) {
                int disponible = stockOutService.obtener(detalle.getIdProducto(), reserva.getIdAlmacen())
                        .map(Stock::disponible).orElse(0);
                faltantes.add(new Faltante(producto.getIdProducto(), producto.getNombre(), detalle.getCantidad(), disponible));
            }
        }
        if (!faltantes.isEmpty()) {
            throw new StockInsuficienteException(faltantes);
        }

        Reserva guardada = reservaOutService.insertar(reserva);
        for (ReservaDetalle detalle : guardada.getDetalles()) {
            registrarMovimiento(TipoMovimiento.RESERVA, guardada, detalle);
            stockUseCase.verificarStockCritico(detalle.getIdProducto(), guardada.getIdAlmacen());
        }
        return guardada;
    }

    @Override
    @Transactional
    public boolean liberar(Long cotizacionId) {
        Reserva reserva = reservaOutService.obtenerPorCotizacionId(cotizacionId).orElse(null);
        if (reserva == null || !reservaOutService.cambiarEstadoSiActiva(reserva.getIdReserva(), EstadoReserva.LIBERADA)) {
            return false;
        }
        for (ReservaDetalle detalle : reserva.getDetalles()) {
            stockOutService.liberar(detalle.getIdProducto(), reserva.getIdAlmacen(), detalle.getCantidad());
            registrarMovimiento(TipoMovimiento.LIBERACION, reserva, detalle);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean consumir(Long cotizacionId) {
        Reserva reserva = reservaOutService.obtenerPorCotizacionId(cotizacionId).orElse(null);
        if (reserva == null || !reservaOutService.cambiarEstadoSiActiva(reserva.getIdReserva(), EstadoReserva.CONSUMIDA)) {
            return false;
        }
        for (ReservaDetalle detalle : reserva.getDetalles()) {
            stockOutService.consumir(detalle.getIdProducto(), reserva.getIdAlmacen(), detalle.getCantidad());
            registrarMovimiento(TipoMovimiento.SALIDA_VENTA, reserva, detalle);
            stockUseCase.verificarStockCritico(detalle.getIdProducto(), reserva.getIdAlmacen());
        }
        return true;
    }

    private void registrarMovimiento(TipoMovimiento tipo, Reserva reserva, ReservaDetalle detalle) {
        stockOutService.registrarMovimiento(new MovimientoStock(null, detalle.getIdProducto(), reserva.getIdAlmacen(),
                tipo, detalle.getCantidad(), null, "COT-" + reserva.getCotizacionId(), null));
    }
}
