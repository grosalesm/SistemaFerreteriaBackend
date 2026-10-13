package com.ferreteria.msventas.infrastructure.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ferreteria.msventas.domain.bean.Comprobante;
import com.ferreteria.msventas.domain.bean.ItemPedido;
import com.ferreteria.msventas.domain.bean.Pago;
import com.ferreteria.msventas.domain.bean.Pedido;
import com.ferreteria.msventas.infrastructure.persistence.entity.ComprobanteEntity;
import com.ferreteria.msventas.infrastructure.persistence.entity.ItemPedidoEntity;
import com.ferreteria.msventas.infrastructure.persistence.entity.PagoEntity;
import com.ferreteria.msventas.infrastructure.persistence.entity.PedidoEntity;

@Mapper(componentModel = "spring")
public interface PedidoMapper {

    @Mapping(target = "pago", ignore = true)
    @Mapping(target = "comprobante", ignore = true)
    Pedido toBean(PedidoEntity entity);
    List<Pedido> toBeans(List<PedidoEntity> entities);
    PedidoEntity toEntity(Pedido pedido);
    ItemPedido toItem(ItemPedidoEntity entity);
    ItemPedidoEntity toItemEntity(ItemPedido item);
    Pago toPago(PagoEntity entity);
    PagoEntity toPagoEntity(Pago pago);
    Comprobante toComprobante(ComprobanteEntity entity);
    ComprobanteEntity toComprobanteEntity(Comprobante comprobante);
}
