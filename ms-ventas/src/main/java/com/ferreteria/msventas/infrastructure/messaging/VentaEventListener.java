package com.ferreteria.msventas.infrastructure.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.ferreteria.msventas.application.port.usecase.PedidoUseCase;
import com.ferreteria.msventas.infrastructure.dto.CotizacionAprobadaEvento;
import com.ferreteria.msventas.infrastructure.mapper.PedidoWebMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class VentaEventListener {

    private final PedidoUseCase pedidoUseCase;
    private final PedidoWebMapper pedidoWebMapper;

    @RabbitListener(queues = RabbitConfig.COLA)
    public void cotizacionAprobada(@Payload CotizacionAprobadaEvento evento) {
        if (evento.cotizacionId() == null || evento.items() == null || evento.items().isEmpty()) {
            log.warn("cotizacion.aprobada sin cotizacionId o sin items, se ignora");
            return;
        }
        boolean creado = pedidoUseCase.crearDesdeCotizacion(pedidoWebMapper.toBean(evento));
        log.info("cotizacion.aprobada {}: {}", evento.codigo(), creado ? "pedido creado" : "ignorado (ya tiene pedido)");
    }
}
