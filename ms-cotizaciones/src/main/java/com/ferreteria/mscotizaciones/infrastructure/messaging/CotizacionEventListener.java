package com.ferreteria.mscotizaciones.infrastructure.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.ferreteria.mscotizaciones.application.port.usecase.CotizacionUseCase;
import com.ferreteria.mscotizaciones.infrastructure.dto.VentaCreadaEvento;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CotizacionEventListener {

    private final CotizacionUseCase cotizacionUseCase;

    @RabbitListener(queues = RabbitConfig.COLA)
    public void ventaCreada(@Payload VentaCreadaEvento evento) {
        if (evento.cotizacionId() == null) {
            log.warn("venta.creada sin cotizacionId, se ignora");
            return;
        }
        boolean convertida = cotizacionUseCase.marcarConvertida(evento.cotizacionId(), evento.pedidoId());
        log.info("venta.creada para la cotización {}: {}", evento.cotizacionId(),
                convertida ? "CONVERTIDA" : "ignorado (no está APROBADA)");
    }
}
