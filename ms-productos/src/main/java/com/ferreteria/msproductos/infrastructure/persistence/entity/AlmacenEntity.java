package com.ferreteria.msproductos.infrastructure.persistence.entity;

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
@Table(name = "almacenes")
public class AlmacenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_almacen")
    private Long idAlmacen;

    @Column(name = "codigo", nullable = false, unique = true, length = 10)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;
}
