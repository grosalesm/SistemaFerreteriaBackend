package com.ferreteria.msproductos.domain.bean;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Stock implements Serializable {

    private Long idStock;
    private Long idProducto;
    private Long idAlmacen;
    private Integer cantidadFisica;
    private Integer cantidadReservada;
    private Boolean productoActivo;

    public int disponible() {
        return cantidadFisica - cantidadReservada;
    }
}
