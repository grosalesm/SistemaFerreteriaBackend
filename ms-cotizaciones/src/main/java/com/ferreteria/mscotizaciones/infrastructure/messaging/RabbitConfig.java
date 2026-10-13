package com.ferreteria.mscotizaciones.infrastructure.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
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
    public static final String COLA = "cotizaciones.queue";
    public static final String VENTA_CREADA = "venta.creada";

    @Bean
    public TopicExchange ferreteriaExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue cotizacionesQueue() {
        return QueueBuilder.durable(COLA).build();
    }

    @Bean
    public Binding ventaCreadaBinding(Queue cotizacionesQueue, TopicExchange ferreteriaExchange) {
        return BindingBuilder.bind(cotizacionesQueue).to(ferreteriaExchange).with(VENTA_CREADA);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
