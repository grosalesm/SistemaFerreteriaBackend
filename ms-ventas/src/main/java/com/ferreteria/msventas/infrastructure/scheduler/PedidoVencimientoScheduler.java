package com.ferreteria.msventas.infrastructure.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ferreteria.msventas.application.port.usecase.PedidoUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PedidoVencimientoScheduler {

    private final PedidoUseCase pedidoUseCase;

    @Scheduled(fixedDelayString = "${venta.intervalo-vencimiento-ms}")
    public void vencerPedidos() {
        int vencidos = pedidoUseCase.vencerPedidos();
        if (vencidos > 0) {
            log.info("{} pedido(s) pasaron a VENCIDO", vencidos);
        }
    }
}
