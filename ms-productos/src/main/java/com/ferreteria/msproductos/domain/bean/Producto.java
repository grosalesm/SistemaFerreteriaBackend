package com.ferreteria.msproductos.domain.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Producto implements Serializable {

    private Long idProducto;
    private String sku = "";
    private String nombre = "";
    private String descripcion = "";
    private Long idCategoria;
    private String unidadMedida = "";
    private BigDecimal precioMinorista;
    private BigDecimal precioContratista;
    private Integer cantidadMinimaDescuento;
    private BigDecimal porcentajeDescuento;
    private Integer stockMinimo;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
}
