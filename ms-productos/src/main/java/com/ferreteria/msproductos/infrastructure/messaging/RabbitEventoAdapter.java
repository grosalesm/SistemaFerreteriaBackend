package com.ferreteria.msproductos.infrastructure.messaging;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.ferreteria.msproductos.application.port.outservice.EventoOutService;
import com.ferreteria.msproductos.domain.bean.Producto;
import com.ferreteria.msproductos.domain.bean.Stock;
import com.ferreteria.msproductos.infrastructure.dto.StockCriticoEvento;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitEventoAdapter implements EventoOutService {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publicarStockCritico(Producto producto, Stock stock) {
        StockCriticoEvento evento = new StockCriticoEvento(producto.getIdProducto(), producto.getNombre(),
                stock.getIdAlmacen(), stock.disponible(), producto.getStockMinimo());
        try {
            rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.STOCK_CRITICO, evento);
        } catch (AmqpException e) {
            log.warn("No se pudo publicar stock.critico del producto {}: {}", producto.getIdProducto(), e.getMessage());
        }
    }
}
