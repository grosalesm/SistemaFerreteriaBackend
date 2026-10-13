package com.ferreteria.mscotizaciones.infrastructure.messaging;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.ferreteria.mscotizaciones.application.port.outservice.EventoOutService;
import com.ferreteria.mscotizaciones.domain.bean.Cotizacion;
import com.ferreteria.mscotizaciones.domain.bean.EstadoCotizacion;
import com.ferreteria.mscotizaciones.infrastructure.dto.CotizacionAprobadaEvento;
import com.ferreteria.mscotizaciones.infrastructure.dto.CotizacionEvento;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitEventoAdapter implements EventoOutService {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publicarCambioEstado(Cotizacion cotizacion) {
        String routingKey = "cotizacion." + cotizacion.getEstado().name().toLowerCase();
        Object evento = cotizacion.getEstado() == EstadoCotizacion.APROBADA
                ? aprobadaEvento(cotizacion)
                : new CotizacionEvento(cotizacion.getIdCotizacion(), cotizacion.getCodigo(), cotizacion.getClienteId(),
                        cotizacion.getClienteNombre(), cotizacion.getTotal(), cotizacion.getEstado().name());

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    enviar(routingKey, evento);
                }
            });
        } else {
            enviar(routingKey, evento);
        }
    }

    private CotizacionAprobadaEvento aprobadaEvento(Cotizacion cotizacion) {
        return new CotizacionAprobadaEvento(cotizacion.getIdCotizacion(), cotizacion.getCodigo(),
                cotizacion.getClienteId(), cotizacion.getClienteNombre(), cotizacion.getIdAlmacen(), cotizacion.getTotal(),
                cotizacion.getItems().stream()
                        .map(i -> new CotizacionAprobadaEvento.Item(i.getIdProducto(), i.getSku(), i.getNombreProducto(),
                                i.getCantidad(), i.getPrecioUnitario(), i.getSubtotal()))
                        .toList());
    }

    private void enviar(String routingKey, Object evento) {
        try {
            rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, routingKey, evento);
            log.info("Publicado {}", routingKey);
        } catch (AmqpException e) {
            log.warn("No se pudo publicar {}: {}", routingKey, e.getMessage());
        }
    }
}
