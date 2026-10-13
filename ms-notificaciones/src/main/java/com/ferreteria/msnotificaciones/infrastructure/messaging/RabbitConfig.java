package com.ferreteria.msnotificaciones.infrastructure.messaging;

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
    public static final String COLA = "notificaciones.queue";

    @Bean
    public TopicExchange ferreteriaExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue notificacionesQueue() {
        return QueueBuilder.durable(COLA).build();
    }

    @Bean
    public Declarables notificacionesBindings(Queue notificacionesQueue, TopicExchange ferreteriaExchange) {
        return new Declarables(
                BindingBuilder.bind(notificacionesQueue).to(ferreteriaExchange).with("cotizacion.*"),
                BindingBuilder.bind(notificacionesQueue).to(ferreteriaExchange).with("venta.*"),
                BindingBuilder.bind(notificacionesQueue).to(ferreteriaExchange).with("pedido.*"),
                BindingBuilder.bind(notificacionesQueue).to(ferreteriaExchange).with("stock.*"));
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
