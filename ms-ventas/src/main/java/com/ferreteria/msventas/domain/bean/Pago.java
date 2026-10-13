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
public class Pago implements Serializable {

    private Long idPago;
    private Long idPedido;
    private MetodoPago metodo;
    private BigDecimal monto;
    private String numeroOperacion;
    private Long cajeroId;
    private LocalDateTime fecha;
    private Boolean devuelto;
}
