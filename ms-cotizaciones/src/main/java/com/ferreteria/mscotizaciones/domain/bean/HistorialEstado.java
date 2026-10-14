package com.ferreteria.mscotizaciones.domain.bean;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistorialEstado implements Serializable {

    private Long idHistorial;
    private EstadoCotizacion estadoAnterior;
    private EstadoCotizacion estadoNuevo;
    private Long usuarioId;
    private String usuarioNombre;
    private LocalDateTime fecha;
    private String comentario;
}
