package com.ferreteria.msproductos.domain.model;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.ferreteria.msproductos.domain.bean.Producto;
import com.ferreteria.msproductos.domain.bean.TipoCliente;
import com.ferreteria.msproductos.domain.constraint.ProductoConstraints;

@Component
public class ProductoModel implements ProductoConstraints {

    private static final BigDecimal DESCUENTO_MAXIMO = new BigDecimal("50");

    @Override
    public boolean validarDatos(Producto producto) {
        return noVacio(producto.getSku())
                && noVacio(producto.getNombre())
                && esPositivo(producto.getPrecioMinorista())
                && esPositivo(producto.getPrecioContratista())
                && producto.getPrecioContratista().compareTo(producto.getPrecioMinorista()) <= 0
                && producto.getStockMinimo() != null && producto.getStockMinimo() >= 0
                && producto.getPorcentajeDescuento() != null
                && producto.getPorcentajeDescuento().compareTo(BigDecimal.ZERO) >= 0
                && producto.getPorcentajeDescuento().compareTo(DESCUENTO_MAXIMO) <= 0;
    }

    @Override
    public BigDecimal precioUnitario(Producto producto, TipoCliente tipoCliente) {
        if (tipoCliente == TipoCliente.CONTRATISTA) {
            return producto.getPrecioContratista();
        }
        return producto.getPrecioMinorista();
    }

    @Override
    public BigDecimal porcentajeAplicable(Producto producto, Integer cantidad) {
        if (producto.getCantidadMinimaDescuento() != null && cantidad >= producto.getCantidadMinimaDescuento()) {
            return producto.getPorcentajeDescuento();
        }
        return BigDecimal.ZERO;
    }

    @Override
    public boolean puedeListarInactivos(String rol) {
        return "ADMIN".equals(rol);
    }

    @Override
    public boolean puedeVerInactivo(String rol) {
        return rol != null && !"CLIENTE".equals(rol);
    }

    private boolean noVacio(String valor) {
        return valor != null && !valor.isBlank();
    }

    private boolean esPositivo(BigDecimal valor) {
        return valor != null && valor.compareTo(BigDecimal.ZERO) > 0;
    }
}
