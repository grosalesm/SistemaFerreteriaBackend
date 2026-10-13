package com.ferreteria.msnotificaciones.infrastructure.websocket;

import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.ferreteria.msnotificaciones.application.port.outservice.AvisoOutService;
import com.ferreteria.msnotificaciones.domain.bean.Notificacion;
import com.ferreteria.msnotificaciones.infrastructure.dto.NotificacionResponse;
import com.ferreteria.msnotificaciones.infrastructure.mapper.NotificacionWebMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompAvisoAdapter implements AvisoOutService {

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificacionWebMapper notificacionWebMapper;

    @Override
    public void enviar(Notificacion notificacion) {
        String destino = notificacion.getRolDestino() != null
                ? "/topic/rol/" + notificacion.getRolDestino()
                : "/topic/cliente/" + notificacion.getClienteId();
        NotificacionResponse aviso = notificacionWebMapper.toResponse(notificacion);

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    publicar(destino, aviso);
                }
            });
        } else {
            publicar(destino, aviso);
        }
    }

    private void publicar(String destino, NotificacionResponse aviso) {
        try {
            messagingTemplate.convertAndSend(destino, aviso);
        } catch (MessagingException e) {
            log.warn("No se pudo enviar el aviso a {}: {}", destino, e.getMessage());
        }
    }
}
