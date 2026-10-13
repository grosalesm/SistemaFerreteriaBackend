package com.ferreteria.msclientes.domain.bean;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cliente implements Serializable {

    private Long idCliente;
    private TipoDocumento tipoDocumento;
    private String numeroDocumento = "";
    private String nombreRazonSocial = "";
    private TipoCliente tipoCliente;
    private String telefono = "";
    private String email = "";
    private String direccion = "";
    private Boolean activo;
    private LocalDateTime fechaRegistro;
}
