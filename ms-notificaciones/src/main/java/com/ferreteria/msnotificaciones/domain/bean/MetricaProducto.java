package com.ferreteria.msnotificaciones.domain.bean;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetricaProducto implements Serializable {

    private Long idProducto;
    private String nombre;
    private Integer unidadesAprobadas = 0;
    private BigDecimal montoAprobado = BigDecimal.ZERO;
}
