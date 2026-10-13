package com.ferreteria.msventas.domain.constraint;

import java.math.BigDecimal;

import com.ferreteria.msventas.domain.bean.Comprobante;
import com.ferreteria.msventas.domain.bean.MetodoPago;
import com.ferreteria.msventas.domain.bean.Pedido;
import com.ferreteria.msventas.domain.bean.TipoComprobante;

public interface PedidoConstraints {

    boolean puedePagar(Pedido pedido);
    boolean montoValido(Pedido pedido, BigDecimal monto);
    boolean pagoValido(MetodoPago metodo, String numeroOperacion);
    boolean puedeEmitirComprobante(Pedido pedido);
    boolean documentoValido(TipoComprobante tipo, String documento);
    boolean puedePreparar(Pedido pedido);
    boolean puedeDespachar(Pedido pedido);
    boolean puedeEntregar(Pedido pedido);
    boolean puedeAnular(Pedido pedido);
    boolean puedeAcceder(Pedido pedido, String rol, Long clienteIdToken);
    void calcularImportes(Comprobante comprobante, BigDecimal total);
}
