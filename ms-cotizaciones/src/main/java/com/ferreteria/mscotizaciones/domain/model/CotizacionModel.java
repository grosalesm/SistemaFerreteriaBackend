package com.ferreteria.mscotizaciones.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ferreteria.mscotizaciones.domain.bean.Cotizacion;
import com.ferreteria.mscotizaciones.domain.bean.EstadoCotizacion;
import com.ferreteria.mscotizaciones.domain.bean.ItemCotizacion;
import com.ferreteria.mscotizaciones.domain.constraint.CotizacionConstraints;

@Component
public class CotizacionModel implements CotizacionConstraints {

    private static final int MAXIMO_ITEMS = 50;

    private final BigDecimal igv;

    public CotizacionModel(@Value("${cotizacion.igv}") BigDecimal igv) {
        this.igv = igv;
    }

    @Override
    public boolean validarItems(List<ItemCotizacion> items) {
        if (items == null || items.isEmpty() || items.size() > MAXIMO_ITEMS) {
            return false;
        }
        boolean datosValidos = items.stream()
                .allMatch(i -> i.getIdProducto() != null && i.getCantidad() != null && i.getCantidad() > 0);
        long productosDistintos = items.stream().map(ItemCotizacion::getIdProducto).distinct().count();
        return datosValidos && productosDistintos == items.size();
    }

    @Override
    public boolean puedeEditar(Cotizacion cotizacion) {
        return cotizacion.getEstado() == EstadoCotizacion.BORRADOR;
    }

    @Override
    public boolean puedeEnviar(Cotizacion cotizacion) {
        return cotizacion.getEstado() == EstadoCotizacion.BORRADOR && !cotizacion.getItems().isEmpty();
    }

    @Override
    public boolean puedeAprobar(Cotizacion cotizacion) {
        return cotizacion.getEstado() == EstadoCotizacion.ENVIADA
                && cotizacion.getFechaVencimiento() != null
                && cotizacion.getFechaVencimiento().isAfter(LocalDateTime.now());
    }

    @Override
    public boolean puedeRechazar(Cotizacion cotizacion) {
        return cotizacion.getEstado() == EstadoCotizacion.ENVIADA;
    }

    @Override
    public boolean puedeReenviar(Cotizacion cotizacion) {
        return cotizacion.getEstado() == EstadoCotizacion.VENCIDA || cotizacion.getEstado() == EstadoCotizacion.RECHAZADA;
    }

    @Override
    public boolean puedeAcceder(Cotizacion cotizacion, String rol, Long clienteIdToken) {
        if (rol == null) {
            return false;
        }
        if ("CLIENTE".equals(rol)) {
            return Objects.equals(cotizacion.getClienteId(), clienteIdToken);
        }
        return true;
    }

    @Override
    public void calcularTotales(Cotizacion cotizacion) {
        BigDecimal subtotal = cotizacion.getItems().stream()
                .map(i -> i.getPrecioUnitario().multiply(BigDecimal.valueOf(i.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = cotizacion.getItems().stream()
                .map(ItemCotizacion::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal valorVenta = total.divide(BigDecimal.ONE.add(igv), 2, RoundingMode.HALF_UP);
        cotizacion.setSubtotal(subtotal);
        cotizacion.setDescuentoTotal(subtotal.subtract(total));
        cotizacion.setTotal(total);
        cotizacion.setIgv(total.subtract(valorVenta));
    }
}
