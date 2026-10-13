package com.ferreteria.msproductos.domain.bean;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Almacen implements Serializable {

    private Long idAlmacen;
    private String codigo = "";
    private String nombre = "";
}
