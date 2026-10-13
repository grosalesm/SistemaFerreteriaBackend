package com.ferreteria.msventas.infrastructure.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.ferreteria.msventas.domain.bean.Comprobante;
import com.ferreteria.msventas.domain.bean.ItemPedido;
import com.ferreteria.msventas.domain.bean.Pago;
import com.ferreteria.msventas.domain.bean.Pedido;
import com.ferreteria.msventas.infrastructure.dto.ComprobanteRequest;
import com.ferreteria.msventas.infrastructure.dto.ComprobanteResponse;
import com.ferreteria.msventas.infrastructure.dto.CotizacionAprobadaEvento;
import com.ferreteria.msventas.infrastructure.dto.ItemPedidoResponse;
import com.ferreteria.msventas.infrastructure.dto.PagoRequest;
import com.ferreteria.msventas.infrastructure.dto.PedidoResponse;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PedidoWebMapper {

    @Mapping(target = "codigo", ignore = true)
    @Mapping(target = "codigoCotizacion", source = "codigo")
    Pedido toBean(CotizacionAprobadaEvento evento);
    ItemPedido toItem(CotizacionAprobadaEvento.Item item);
    Pago toPago(PagoRequest request);
    Comprobante toComprobante(ComprobanteRequest request);
    PedidoResponse toResponse(Pedido pedido);
    List<PedidoResponse> toResponses(List<Pedido> pedidos);
    ItemPedidoResponse toItemResponse(ItemPedido item);
    PedidoResponse.PagoResponse toPagoResponse(Pago pago);
    @Mapping(target = "numeroComprobante", expression = "java(comprobante.getSerie() + \"-\" + comprobante.getNumero())")
    ComprobanteResponse toComprobanteResponse(Comprobante comprobante);
}
