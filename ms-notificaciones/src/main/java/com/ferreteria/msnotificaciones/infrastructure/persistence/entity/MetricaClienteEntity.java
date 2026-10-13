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
@Table(name = "metricas_clientes")
public class MetricaClienteEntity {

    @Id
    @Column(name = "cliente_id")
    private Long clienteId;

    @Column(name = "nombre", length = 150)
    private String nombre;

    @Column(name = "compras", nullable = false)
    private Integer compras;

    @Column(name = "monto_comprado", nullable = false, precision = 14, scale = 2)
    private BigDecimal montoComprado;
}
