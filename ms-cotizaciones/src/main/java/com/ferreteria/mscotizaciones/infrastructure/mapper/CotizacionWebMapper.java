package com.ferreteria.mscotizaciones.infrastructure.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.ferreteria.mscotizaciones.domain.bean.Cotizacion;
import com.ferreteria.mscotizaciones.domain.bean.HistorialEstado;
import com.ferreteria.mscotizaciones.domain.bean.ItemCotizacion;
import com.ferreteria.mscotizaciones.infrastructure.dto.CotizacionRequest;
import com.ferreteria.mscotizaciones.infrastructure.dto.CotizacionResponse;
import com.ferreteria.mscotizaciones.infrastructure.dto.HistorialResponse;
import com.ferreteria.mscotizaciones.infrastructure.dto.ItemRequest;
import com.ferreteria.mscotizaciones.infrastructure.dto.ItemResponse;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CotizacionWebMapper {

    Cotizacion toBean(CotizacionRequest request);
    ItemCotizacion toItem(ItemRequest request);
    List<ItemCotizacion> toItems(List<ItemRequest> requests);
    CotizacionResponse toResponse(Cotizacion cotizacion);
    List<CotizacionResponse> toResponses(List<Cotizacion> cotizaciones);
    ItemResponse toItemResponse(ItemCotizacion item);
    HistorialResponse toHistorialResponse(HistorialEstado historial);
}
