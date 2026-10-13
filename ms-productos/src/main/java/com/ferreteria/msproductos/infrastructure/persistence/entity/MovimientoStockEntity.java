package com.ferreteria.msproductos.infrastructure.persistence.entity;

import java.time.LocalDateTime;

import com.ferreteria.msproductos.domain.bean.TipoMovimiento;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "movimientos_stock")
public class MovimientoStockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimiento")
    private Long idMovimiento;

    @Column(name = "id_producto", nullable = false)
    private Long idProducto;

    @Column(name = "id_almacen", nullable = false)
    private Long idAlmacen;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoMovimiento tipo;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "saldo_fisico", nullable = false)
    private Integer saldoFisico;

    @Column(name = "referencia", length = 200)
    private String referencia;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;
}
