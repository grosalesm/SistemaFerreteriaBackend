package com.ferreteria.msventas.domain.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pedido implements Serializable {

    private Long idPedido;
    private String codigo;
    private Long cotizacionId;
    private String codigoCotizacion;
    private Long clienteId;
    private String clienteNombre;
    private Long idAlmacen;
    private EstadoPedido estado;
    private BigDecimal total;
    private TipoEntrega tipoEntrega;
    private String direccionEntrega;
    private String repartidor;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaLimitePago;
    private LocalDateTime fechaPago;
    private LocalDateTime fechaPreparacion;
    private LocalDateTime fechaDespacho;
    private LocalDateTime fechaEntrega;
    private Long almaceneroId;
    private String motivoAnulacion;
    private List<ItemPedido> items = new ArrayList<>();
    private Pago pago;
    private Comprobante comprobante;
}
