package com.ferreteria.msventas.infrastructure.messaging;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.ferreteria.msventas.application.port.outservice.EventoOutService;
import com.ferreteria.msventas.domain.bean.Pedido;
import com.ferreteria.msventas.infrastructure.dto.VentaEvento;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitEventoAdapter implements EventoOutService {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publicarCambioEstado(Pedido pedido) {
        String routingKey = switch (pedido.getEstado()) {
            case PENDIENTE_PAGO -> "venta.creada";
            case PAGADO -> "venta.pagada";
            case ANULADO -> "venta.anulada";
            case VENCIDO -> "pedido.vencido";
            case DESPACHADO -> "pedido.despachado";
            case ENTREGADO -> "pedido.entregado";
            case EN_PREPARACION -> null;
        };
        if (routingKey == null) {
            return;
        }
        VentaEvento evento = new VentaEvento(pedido.getIdPedido(), pedido.getCotizacionId(), pedido.getCodigo(),
                pedido.getClienteId(), pedido.getClienteNombre(), pedido.getTotal(), pedido.getEstado().name());

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

    private void enviar(String routingKey, VentaEvento evento) {
        try {
            rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, routingKey, evento);
            log.info("Publicado {} del pedido {}", routingKey, evento.codigo());
        } catch (AmqpException e) {
            log.warn("No se pudo publicar {}: {}", routingKey, e.getMessage());
        }
    }
}
