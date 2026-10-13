package com.ferreteria.msnotificaciones.domain.bean;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notificacion implements Serializable {

    private Long idNotificacion;
    private String tipo;
    private String titulo;
    private String mensaje;
    private String rolDestino;
    private Long clienteId;
    private Long referenciaId;
    private Boolean leida;
    private LocalDateTime fecha;
}
