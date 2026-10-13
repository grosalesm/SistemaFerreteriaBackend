package com.ferreteria.msnotificaciones.infrastructure.persistence.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "eventos_procesados")
public class EventoProcesadoEntity {

    @Id
    @Column(name = "clave", length = 80)
    private String clave;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;
}
