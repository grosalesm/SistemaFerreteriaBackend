package com.ferreteria.msnotificaciones.infrastructure.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.ferreteria.msnotificaciones.application.port.usecase.NotificacionUseCase;
import com.ferreteria.msnotificaciones.infrastructure.dto.EventoNegocio;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class NegocioEventListener {

    private final NotificacionUseCase notificacionUseCase;

    @RabbitListener(queues = RabbitConfig.COLA)
    public void recibir(@Payload EventoNegocio evento,
                        @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey) {
        boolean procesado = notificacionUseCase.procesar(routingKey, evento);
        log.info("Evento {} {}: {}", routingKey, evento.codigo() != null ? evento.codigo() : evento.nombre(),
                procesado ? "procesado" : "ignorado (ya fue procesado)");
    }
}
