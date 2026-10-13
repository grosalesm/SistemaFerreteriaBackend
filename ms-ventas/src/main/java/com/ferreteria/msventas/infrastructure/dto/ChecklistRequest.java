package com.ferreteria.msventas.infrastructure.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;

public record ChecklistRequest(
        @NotEmpty(message = "Debe indicar al menos un item preparado")
        List<Long> itemsPreparados) {
}
