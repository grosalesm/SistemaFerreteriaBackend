package com.ferreteria.msproductos.domain.constraint;

import java.math.BigDecimal;

import com.ferreteria.msproductos.domain.bean.Producto;
import com.ferreteria.msproductos.domain.bean.TipoCliente;

public interface ProductoConstraints {

    boolean validarDatos(Producto producto);
    BigDecimal precioUnitario(Producto producto, TipoCliente tipoCliente);
    BigDecimal porcentajeAplicable(Producto producto, Integer cantidad);
    boolean puedeListarInactivos(String rol);
    boolean puedeVerInactivo(String rol);
}
