package com.ferreteria.msnotificaciones.application.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.ferreteria.msnotificaciones.application.port.outservice.AvisoOutService;
import com.ferreteria.msnotificaciones.application.port.outservice.MetricaOutService;
import com.ferreteria.msnotificaciones.application.port.outservice.NotificacionOutService;
import com.ferreteria.msnotificaciones.application.port.usecase.NotificacionUseCase;
import com.ferreteria.msnotificaciones.domain.bean.MetricaCliente;
import com.ferreteria.msnotificaciones.domain.bean.MetricaDiaria;
import com.ferreteria.msnotificaciones.domain.bean.MetricaProducto;
import com.ferreteria.msnotificaciones.domain.bean.Notificacion;
import com.ferreteria.msnotificaciones.domain.constraint.NotificacionConstraints;
import com.ferreteria.msnotificaciones.infrastructure.dto.EventoNegocio;
import com.ferreteria.msnotificaciones.infrastructure.dto.ItemEvento;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificacionService implements NotificacionUseCase {

    private final NotificacionOutService notificacionOutService;
    private final MetricaOutService metricaOutService;
    private final AvisoOutService avisoOutService;
    private final NotificacionConstraints notificacionConstraints;

    @Override
    @Transactional
    public boolean procesar(String routingKey, EventoNegocio evento) {
        if (notificacionConstraints.debeContarse(routingKey)) {
            String clave = notificacionConstraints.claveEvento(routingKey, evento);
            if (clave != null) {
                if (notificacionOutService.eventoProcesado(clave)) {
                    return false;
                }
                notificacionOutService.registrarEvento(clave);
            }
        }

        Notificacion plantilla = notificacionConstraints.armarMensaje(routingKey, evento);
        for (String destino : notificacionConstraints.destinos(routingKey)) {
            boolean esCliente = NotificacionConstraints.CLIENTE.equals(destino);
            if (esCliente && evento.clienteId() == null) {
                continue;
            }
            Notificacion notificacion = new Notificacion(null, plantilla.getTipo(), plantilla.getTitulo(),
                    plantilla.getMensaje(), esCliente ? null : destino, esCliente ? evento.clienteId() : null,
                    plantilla.getReferenciaId(), false, LocalDateTime.now());
            avisoOutService.enviar(notificacionOutService.guardar(notificacion));
        }

        actualizarMetricas(routingKey, evento);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notificacion> listar(boolean soloNoLeidas, String rol, Long clienteIdToken) {
        if (rol == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuario no identificado");
        }
        if (NotificacionConstraints.CLIENTE.equals(rol)) {
            if (clienteIdToken == null) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El usuario no está asociado a un cliente");
            }
            return notificacionOutService.listarPorCliente(clienteIdToken, soloNoLeidas);
        }
        return notificacionOutService.listarPorRol(rol, soloNoLeidas);
    }

    @Override
    @Transactional
    public Optional<Notificacion> marcarLeida(Long idNotificacion, String rol, Long clienteIdToken) {
        return notificacionOutService.obtenerPorId(idNotificacion).map(notificacion -> {
            if (!notificacionConstraints.puedeVer(notificacion, rol, clienteIdToken)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La notificación no es suya");
            }
            notificacion.setLeida(true);
            return notificacionOutService.guardar(notificacion);
        });
    }

    private void actualizarMetricas(String routingKey, EventoNegocio evento) {
        BigDecimal total = evento.total() == null ? BigDecimal.ZERO : evento.total();
        switch (routingKey) {
            case "cotizacion.enviada" -> actualizarDia(d -> d.setCotizacionesEnviadas(d.getCotizacionesEnviadas() + 1));
            case "cotizacion.rechazada" -> actualizarDia(d -> d.setCotizacionesRechazadas(d.getCotizacionesRechazadas() + 1));
            case "cotizacion.vencida" -> actualizarDia(d -> d.setCotizacionesVencidas(d.getCotizacionesVencidas() + 1));
            case "cotizacion.aprobada" -> {
                actualizarDia(d -> d.setCotizacionesAprobadas(d.getCotizacionesAprobadas() + 1));
                if (evento.items() != null) {
                    evento.items().forEach(this::sumarProducto);
                }
            }
            case "venta.pagada" -> {
                actualizarDia(d -> {
                    d.setPedidosPagados(d.getPedidosPagados() + 1);
                    d.setMontoVendido(d.getMontoVendido().add(total));
                });
                sumarCliente(evento, total);
            }
            case "pedido.entregado" -> actualizarDia(d -> d.setPedidosEntregados(d.getPedidosEntregados() + 1));
            case "venta.anulada" -> actualizarDia(d -> d.setPedidosAnulados(d.getPedidosAnulados() + 1));
            default -> {
            }
        }
    }

    private void actualizarDia(Consumer<MetricaDiaria> cambio) {
        LocalDate hoy = LocalDate.now();
        MetricaDiaria metrica = metricaOutService.obtenerDia(hoy).orElseGet(() -> new MetricaDiaria(hoy));
        cambio.accept(metrica);
        metricaOutService.guardarDia(metrica);
    }

    private void sumarProducto(ItemEvento item) {
        MetricaProducto metrica = metricaOutService.obtenerProducto(item.idProducto())
                .orElseGet(() -> new MetricaProducto(item.idProducto(), null, 0, BigDecimal.ZERO));
        metrica.setNombre(item.nombreProducto());
        metrica.setUnidadesAprobadas(metrica.getUnidadesAprobadas() + item.cantidad());
        metrica.setMontoAprobado(metrica.getMontoAprobado().add(item.subtotal() == null ? BigDecimal.ZERO : item.subtotal()));
        metricaOutService.guardarProducto(metrica);
    }

    private void sumarCliente(EventoNegocio evento, BigDecimal total) {
        if (evento.clienteId() == null) {
            return;
        }
        MetricaCliente metrica = metricaOutService.obtenerCliente(evento.clienteId())
                .orElseGet(() -> new MetricaCliente(evento.clienteId(), null, 0, BigDecimal.ZERO));
        metrica.setNombre(evento.clienteNombre());
        metrica.setCompras(metrica.getCompras() + 1);
        metrica.setMontoComprado(metrica.getMontoComprado().add(total));
        metricaOutService.guardarCliente(metrica);
    }
}
