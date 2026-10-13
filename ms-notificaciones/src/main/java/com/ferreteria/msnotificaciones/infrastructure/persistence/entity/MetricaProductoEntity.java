package com.ferreteria.msnotificaciones.infrastructure.persistence.entity;

import java.math.BigDecimal;

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
@Table(name = "metricas_productos")
public class MetricaProductoEntity {

    @Id
    @Column(name = "id_producto")
    private Long idProducto;

    @Column(name = "nombre", length = 150)
    private String nombre;

    @Column(name = "unidades_aprobadas", nullable = false)
    private Integer unidadesAprobadas;

    @Column(name = "monto_aprobado", nullable = false, precision = 14, scale = 2)
    private BigDecimal montoAprobado;
}
