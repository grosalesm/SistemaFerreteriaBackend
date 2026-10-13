package com.ferreteria.msnotificaciones.infrastructure.persistence.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "metricas_diarias")
public class MetricaDiariaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_metrica")
    private Long idMetrica;

    @Column(name = "fecha", nullable = false, unique = true)
    private LocalDate fecha;

    @Column(name = "cotizaciones_enviadas", nullable = false)
    private Integer cotizacionesEnviadas;

    @Column(name = "cotizaciones_aprobadas", nullable = false)
    private Integer cotizacionesAprobadas;

    @Column(name = "cotizaciones_rechazadas", nullable = false)
    private Integer cotizacionesRechazadas;

    @Column(name = "cotizaciones_vencidas", nullable = false)
    private Integer cotizacionesVencidas;

    @Column(name = "pedidos_pagados", nullable = false)
    private Integer pedidosPagados;

    @Column(name = "monto_vendido", nullable = false, precision = 14, scale = 2)
    private BigDecimal montoVendido;

    @Column(name = "pedidos_entregados", nullable = false)
    private Integer pedidosEntregados;

    @Column(name = "pedidos_anulados", nullable = false)
    private Integer pedidosAnulados;
}
