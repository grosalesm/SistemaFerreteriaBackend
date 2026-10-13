package com.ferreteria.msventas.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ferreteria.msventas.domain.bean.Comprobante;
import com.ferreteria.msventas.domain.bean.EstadoPedido;
import com.ferreteria.msventas.domain.bean.MetodoPago;
import com.ferreteria.msventas.domain.bean.Pedido;
import com.ferreteria.msventas.domain.bean.TipoComprobante;
import com.ferreteria.msventas.domain.bean.TipoEntrega;
import com.ferreteria.msventas.domain.constraint.PedidoConstraints;

@Component
public class PedidoModel implements PedidoConstraints {

    private final BigDecimal igv;

    public PedidoModel(@Value("${venta.igv}") BigDecimal igv) {
        this.igv = igv;
    }

    @Override
    public boolean puedePagar(Pedido pedido) {
        return pedido.getEstado() == EstadoPedido.PENDIENTE_PAGO;
    }

    @Override
    public boolean montoValido(Pedido pedido, BigDecimal monto) {
        return monto != null && monto.compareTo(pedido.getTotal()) == 0;
    }

    @Override
    public boolean pagoValido(MetodoPago metodo, String numeroOperacion) {
        return metodo == MetodoPago.EFECTIVO || (numeroOperacion != null && !numeroOperacion.isBlank());
    }

    @Override
    public boolean puedeEmitirComprobante(Pedido pedido) {
        return pedido.getPago() != null && !Boolean.TRUE.equals(pedido.getPago().getDevuelto());
    }

    @Override
    public boolean documentoValido(TipoComprobante tipo, String documento) {
        if (tipo == null || documento == null) {
            return false;
        }
        return tipo == TipoComprobante.BOLETA ? documento.matches("\\d{8}") : documento.matches("\\d{11}");
    }

    @Override
    public boolean puedePreparar(Pedido pedido) {
        return pedido.getEstado() == EstadoPedido.PAGADO;
    }

    @Override
    public boolean puedeDespachar(Pedido pedido) {
        boolean todoPreparado = pedido.getItems().stream().allMatch(i -> Boolean.TRUE.equals(i.getPreparado()));
        boolean conRepartidor = pedido.getTipoEntrega() != TipoEntrega.DELIVERY
                || (pedido.getRepartidor() != null && !pedido.getRepartidor().isBlank());
        return pedido.getEstado() == EstadoPedido.EN_PREPARACION && todoPreparado && conRepartidor;
    }

    @Override
    public boolean puedeEntregar(Pedido pedido) {
        return pedido.getEstado() == EstadoPedido.DESPACHADO;
    }

    @Override
    public boolean puedeAnular(Pedido pedido) {
        return pedido.getEstado() == EstadoPedido.PENDIENTE_PAGO || pedido.getEstado() == EstadoPedido.PAGADO;
    }

    @Override
    public boolean puedeAcceder(Pedido pedido, String rol, Long clienteIdToken) {
        if (rol == null) {
            return false;
        }
        if ("CLIENTE".equals(rol)) {
            return Objects.equals(pedido.getClienteId(), clienteIdToken);
        }
        return true;
    }

    @Override
    public void calcularImportes(Comprobante comprobante, BigDecimal total) {
        BigDecimal subtotal = total.divide(BigDecimal.ONE.add(igv), 2, RoundingMode.HALF_UP);
        comprobante.setTotal(total);
        comprobante.setSubtotal(subtotal);
        comprobante.setIgv(total.subtract(subtotal));
    }
}
