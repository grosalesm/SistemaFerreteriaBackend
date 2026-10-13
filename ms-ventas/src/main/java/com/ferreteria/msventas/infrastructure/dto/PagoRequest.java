package com.ferreteria.msventas.infrastructure.dto;

import java.math.BigDecimal;

import com.ferreteria.msventas.domain.bean.MetodoPago;
import com.ferreteria.msventas.domain.bean.TipoEntrega;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record PagoRequest(
        @NotNull(message = "El método de pago es obligatorio")
        MetodoPago metodo,

        @NotNull(message = "El monto es obligatorio")
        @Positive(message = "El monto debe ser mayor a 0")
        BigDecimal monto,

        @Size(max = 50, message = "El número de operación no puede superar 50 caracteres")
        String numeroOperacion,

        @NotNull(message = "El tipo de entrega es obligatorio")
        TipoEntrega tipoEntrega,

        @Size(max = 250, message = "La dirección no puede superar 250 caracteres")
        String direccionEntrega) {
}
