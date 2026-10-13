package com.ferreteria.msauth.domain.bean;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Usuario implements Serializable {

    private Long idUsuario;
    private String username = "";
    private String nombreCompleto = "";
    private String email = "";
    private String password = "";
    private Rol rol;
    private Long clienteId;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
}
