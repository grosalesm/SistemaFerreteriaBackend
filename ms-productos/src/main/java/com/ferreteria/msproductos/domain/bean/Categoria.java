package com.ferreteria.msproductos.domain.bean;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Categoria implements Serializable {

    private Long idCategoria;
    private String nombre = "";
    private Boolean activo;
}
