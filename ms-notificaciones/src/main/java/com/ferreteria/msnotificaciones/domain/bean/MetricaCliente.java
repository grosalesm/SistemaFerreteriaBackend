package com.ferreteria.msnotificaciones.domain.bean;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetricaCliente implements Serializable {

    private Long clienteId;
    private String nombre;
    private Integer compras = 0;
    private BigDecimal montoComprado = BigDecimal.ZERO;
}
