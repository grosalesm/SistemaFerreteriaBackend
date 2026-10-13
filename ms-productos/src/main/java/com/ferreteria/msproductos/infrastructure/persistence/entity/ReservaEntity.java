package com.ferreteria.msproductos.infrastructure.persistence.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.ferreteria.msproductos.domain.bean.EstadoReserva;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "reservas")
public class ReservaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Long idReserva;

    @Column(name = "cotizacion_id", nullable = false, unique = true)
    private Long cotizacionId;

    @Column(name = "id_almacen", nullable = false)
    private Long idAlmacen;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoReserva estado;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id_reserva", nullable = false)
    private List<ReservaDetalleEntity> detalles = new ArrayList<>();
}
