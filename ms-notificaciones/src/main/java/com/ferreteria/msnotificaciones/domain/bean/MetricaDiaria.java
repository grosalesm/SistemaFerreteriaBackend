package com.ferreteria.msnotificaciones.domain.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetricaDiaria implements Serializable {

    private Long idMetrica;
    private LocalDate fecha;
    private Integer cotizacionesEnviadas = 0;
    private Integer cotizacionesAprobadas = 0;
    private Integer cotizacionesRechazadas = 0;
    private Integer cotizacionesVencidas = 0;
    private Integer pedidosPagados = 0;
    private BigDecimal montoVendido = BigDecimal.ZERO;
    private Integer pedidosEntregados = 0;
    private Integer pedidosAnulados = 0;

    public MetricaDiaria(LocalDate fecha) {
        this.fecha = fecha;
    }
}
