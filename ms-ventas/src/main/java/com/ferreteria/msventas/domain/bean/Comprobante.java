package com.ferreteria.msventas.domain.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comprobante implements Serializable {

    private Long idComprobante;
    private Long idPedido;
    private TipoComprobante tipo;
    private String serie;
    private Integer numero;
    private String documentoCliente;
    private String razonSocial;
    private BigDecimal subtotal;
    private BigDecimal igv;
    private BigDecimal total;
    private LocalDateTime fechaEmision;
}
