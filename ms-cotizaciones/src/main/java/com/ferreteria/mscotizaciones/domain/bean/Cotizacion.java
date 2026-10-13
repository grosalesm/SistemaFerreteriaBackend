package com.ferreteria.mscotizaciones.domain.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cotizacion implements Serializable {

    private Long idCotizacion;
    private String codigo;
    private Long clienteId;
    private String clienteNombre;
    private String tipoCliente;
    private Long vendedorId;
    private Canal canal;
    private Long idAlmacen;
    private EstadoCotizacion estado;
    private BigDecimal subtotal;
    private BigDecimal descuentoTotal;
    private BigDecimal igv;
    private BigDecimal total;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaVencimiento;
    private LocalDateTime fechaAprobacion;
    private String motivoRechazo;
    private Long cotizacionOrigenId;
    private List<ItemCotizacion> items = new ArrayList<>();
    private List<HistorialEstado> historial = new ArrayList<>();
}
