package com.ferreteria.mscotizaciones.infrastructure.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ferreteria.mscotizaciones.application.port.usecase.CotizacionUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class VencimientoScheduler {

    private final CotizacionUseCase cotizacionUseCase;

    @Scheduled(fixedDelayString = "${cotizacion.intervalo-vencimiento-ms}")
    public void vencerCotizaciones() {
        int vencidas = cotizacionUseCase.vencerCotizaciones();
        if (vencidas > 0) {
            log.info("{} cotización(es) pasaron a VENCIDA", vencidas);
        }
    }
}
