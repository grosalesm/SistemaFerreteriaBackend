package com.ferreteria.msnotificaciones.infrastructure.websocket;

import java.util.Map;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final String ROL = "rol";
    private static final String CLIENTE_ID = "clienteId";

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .addInterceptors(new HandshakeInterceptor() {
                    @Override
                    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
                        String rol = request.getHeaders().getFirst("X-User-Role");
                        String clienteId = request.getHeaders().getFirst("X-Cliente-Id");
                        if (rol != null) {
                            attributes.put(ROL, rol);
                        }
                        if (clienteId != null) {
                            attributes.put(CLIENTE_ID, clienteId);
                        }
                        return true;
                    }

                    @Override
                    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                               WebSocketHandler wsHandler, Exception exception) {
                    }
                });
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())
                        && !puedeSuscribirse(accessor.getDestination(), accessor.getSessionAttributes())) {
                    throw new MessagingException("No tiene permiso para suscribirse a " + accessor.getDestination());
                }
                return message;
            }
        });
    }

    private boolean puedeSuscribirse(String destino, Map<String, Object> atributos) {
        Object rol = atributos == null ? null : atributos.get(ROL);
        if (rol == null || destino == null) {
            return false;
        }
        if ("CLIENTE".equals(rol)) {
            return destino.equals("/topic/cliente/" + atributos.get(CLIENTE_ID));
        }
        return destino.equals("/topic/rol/" + rol);
    }
}
