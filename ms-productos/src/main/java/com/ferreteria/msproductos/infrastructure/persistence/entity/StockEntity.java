package com.ferreteria.msproductos.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "stock", uniqueConstraints = @UniqueConstraint(columnNames = {"id_producto", "id_almacen"}))
public class StockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_stock")
    private Long idStock;

    @Column(name = "id_producto", nullable = false)
    private Long idProducto;

    @Column(name = "id_almacen", nullable = false)
    private Long idAlmacen;

    @Column(name = "cantidad_fisica", nullable = false)
    private Integer cantidadFisica;

    @Column(name = "cantidad_reservada", nullable = false)
    private Integer cantidadReservada;
}
