package com.ferreteria.msproductos.domain.bean;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovimientoStock implements Serializable {

    private Long idMovimiento;
    private Long idProducto;
    private Long idAlmacen;
    private TipoMovimiento tipo;
    private Integer cantidad;
    private Integer saldoFisico;
    private String referencia = "";
    private LocalDateTime fecha;
}
