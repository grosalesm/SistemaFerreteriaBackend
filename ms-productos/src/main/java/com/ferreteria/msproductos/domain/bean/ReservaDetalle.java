package com.ferreteria.msproductos.domain.bean;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservaDetalle implements Serializable {

    private Long idDetalle;
    private Long idProducto;
    private Integer cantidad;
}
