package com.ferreteria.msproductos.infrastructure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ferreteria.msproductos.domain.bean.Reserva;
import com.ferreteria.msproductos.domain.bean.ReservaDetalle;
import com.ferreteria.msproductos.infrastructure.dto.ReservaRequest;
import com.ferreteria.msproductos.infrastructure.dto.ReservaResponse;

@Mapper(componentModel = "spring")
public interface ReservaWebMapper {

    @Mapping(target = "idReserva", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "detalles", source = "items")
    Reserva toBean(ReservaRequest request);

    @Mapping(target = "idDetalle", ignore = true)
    ReservaDetalle toDetalle(ReservaRequest.Item item);
    ReservaResponse toResponse(Reserva reserva);
    ReservaResponse.Detalle toDetalleResponse(ReservaDetalle detalle);
}
