package com.ferreteria.msproductos.infrastructure.messaging;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "ferreteria.events";
    public static final String COLA = "productos.queue";
    public static final String STOCK_CRITICO = "stock.critico";

    @Bean
    public TopicExchange ferreteriaExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue productosQueue() {
        return QueueBuilder.durable(COLA).build();
    }

    @Bean
    public Declarables productosBindings(Queue productosQueue, TopicExchange ferreteriaExchange) {
        return new Declarables(
                BindingBuilder.bind(productosQueue).to(ferreteriaExchange).with("cotizacion.vencida"),
                BindingBuilder.bind(productosQueue).to(ferreteriaExchange).with("cotizacion.rechazada"),
                BindingBuilder.bind(productosQueue).to(ferreteriaExchange).with("venta.anulada"),
                BindingBuilder.bind(productosQueue).to(ferreteriaExchange).with("pedido.vencido"),
                BindingBuilder.bind(productosQueue).to(ferreteriaExchange).with("pedido.despachado"));
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
