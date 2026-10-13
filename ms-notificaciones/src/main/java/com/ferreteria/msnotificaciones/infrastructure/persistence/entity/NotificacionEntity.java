package com.ferreteria.msnotificaciones.infrastructure.persistence.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "notificaciones", indexes = {
        @Index(name = "idx_notificaciones_rol", columnList = "rol_destino, fecha"),
        @Index(name = "idx_notificaciones_cliente", columnList = "cliente_id, fecha")})
public class NotificacionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacion")
    private Long idNotificacion;

    @Column(name = "tipo", nullable = false, length = 40)
    private String tipo;

    @Column(name = "titulo", nullable = false, length = 100)
    private String titulo;

    @Column(name = "mensaje", nullable = false, length = 300)
    private String mensaje;

    @Column(name = "rol_destino", length = 20)
    private String rolDestino;

    @Column(name = "cliente_id")
    private Long clienteId;

    @Column(name = "referencia_id")
    private Long referenciaId;

    @Column(name = "leida", nullable = false)
    private Boolean leida;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;
}
