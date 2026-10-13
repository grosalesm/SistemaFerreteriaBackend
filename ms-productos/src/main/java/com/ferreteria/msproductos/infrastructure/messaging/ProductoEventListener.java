package com.ferreteria.msproductos.infrastructure.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.ferreteria.msproductos.application.port.usecase.ReservaUseCase;
import com.ferreteria.msproductos.infrastructure.dto.EventoStock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductoEventListener {

    private final ReservaUseCase reservaUseCase;

    @RabbitListener(queues = RabbitConfig.COLA)
    public void recibir(@Payload EventoStock evento,
                        @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey) {
        if (evento.cotizacionId() == null) {
            log.warn("Evento {} sin cotizacionId, se ignora", routingKey);
            return;
        }
        boolean procesado = "pedido.despachado".equals(routingKey)
                ? reservaUseCase.consumir(evento.cotizacionId())
                : reservaUseCase.liberar(evento.cotizacionId());
        log.info("Evento {} para COT-{}: {}", routingKey, evento.cotizacionId(),
                procesado ? "procesado" : "ignorado (sin reserva activa)");
    }
}
