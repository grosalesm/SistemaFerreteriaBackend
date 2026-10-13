package com.ferreteria.mscotizaciones.domain.bean;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemCotizacion implements Serializable {

    private Long idItem;
    private Long idProducto;
    private String sku;
    private String nombreProducto;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal porcentajeDescuento;
    private BigDecimal subtotal;
    private String aviso;
}
