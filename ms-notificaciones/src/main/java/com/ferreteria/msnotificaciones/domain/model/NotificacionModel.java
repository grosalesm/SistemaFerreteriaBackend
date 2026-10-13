package com.ferreteria.msnotificaciones.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.ferreteria.msnotificaciones.domain.bean.Notificacion;
import com.ferreteria.msnotificaciones.domain.constraint.NotificacionConstraints;
import com.ferreteria.msnotificaciones.infrastructure.dto.EventoNegocio;

@Component
public class NotificacionModel implements NotificacionConstraints {

    @Override
    public List<String> destinos(String routingKey) {
        return switch (routingKey) {
            case "cotizacion.enviada", "cotizacion.rechazada", "cotizacion.vencida" -> List.of("VENDEDOR", "ADMIN", CLIENTE);
            case "cotizacion.aprobada" -> List.of("VENDEDOR", "CAJERO", "ADMIN", CLIENTE);
            case "venta.creada" -> List.of("CAJERO");
            case "venta.pagada" -> List.of("ALMACENERO", "ADMIN", CLIENTE);
            case "venta.anulada" -> List.of("CAJERO", "ALMACENERO", "ADMIN", CLIENTE);
            case "pedido.vencido" -> List.of("CAJERO", "ADMIN");
            case "pedido.despachado", "pedido.entregado" -> List.of("ADMIN", CLIENTE);
            case "stock.critico" -> List.of("ADMIN", "ALMACENERO");
            default -> List.of();
        };
    }

    @Override
    public Notificacion armarMensaje(String routingKey, EventoNegocio e) {
        String monto = "S/ " + (e.total() == null ? BigDecimal.ZERO : e.total()).setScale(2, RoundingMode.HALF_UP);
        String cliente = e.clienteNombre() == null ? "el cliente" : e.clienteNombre();
        String[] texto = switch (routingKey) {
            case "cotizacion.enviada" -> new String[]{"Cotización enviada",
                    "Cotización " + e.codigo() + " enviada a " + cliente + " - " + monto};
            case "cotizacion.aprobada" -> new String[]{"Cotización aprobada",
                    "Cotización " + e.codigo() + " aprobada por " + cliente + " - " + monto + " - se generará el pedido"};
            case "cotizacion.rechazada" -> new String[]{"Cotización rechazada",
                    "Cotización " + e.codigo() + " rechazada por " + cliente};
            case "cotizacion.vencida" -> new String[]{"Cotización vencida",
                    "Cotización " + e.codigo() + " de " + cliente + " venció sin respuesta"};
            case "venta.creada" -> new String[]{"Nuevo pedido por cobrar",
                    "Pedido " + e.codigo() + " de " + cliente + " pendiente de pago - " + monto};
            case "venta.pagada" -> new String[]{"Pedido pagado",
                    "Pedido " + e.codigo() + " pagado - " + monto + " - listo para preparar"};
            case "venta.anulada" -> new String[]{"Pedido anulado",
                    "Pedido " + e.codigo() + " de " + cliente + " anulado - " + monto};
            case "pedido.vencido" -> new String[]{"Pedido vencido",
                    "Pedido " + e.codigo() + " venció sin pago - se liberó la reserva"};
            case "pedido.despachado" -> new String[]{"Pedido despachado",
                    "Pedido " + e.codigo() + " despachado - en camino o listo para recoger"};
            case "pedido.entregado" -> new String[]{"Pedido entregado",
                    "Pedido " + e.codigo() + " entregado a " + cliente};
            case "stock.critico" -> new String[]{"Stock crítico",
                    e.nombre() + ": quedan " + e.disponible() + " unidades en el almacén " + e.idAlmacen()
                            + " (mínimo " + e.stockMinimo() + ")"};
            default -> new String[]{"Evento " + routingKey, "Se recibió el evento " + routingKey};
        };
        Notificacion notificacion = new Notificacion();
        notificacion.setTipo(routingKey);
        notificacion.setTitulo(recortar(texto[0], 100));
        notificacion.setMensaje(recortar(texto[1], 300));
        notificacion.setReferenciaId(referencia(routingKey, e));
        return notificacion;
    }

    @Override
    public boolean debeContarse(String routingKey) {
        return !"stock.critico".equals(routingKey);
    }

    @Override
    public String claveEvento(String routingKey, EventoNegocio evento) {
        Long id = referencia(routingKey, evento);
        return id == null ? null : routingKey + ":" + id;
    }

    @Override
    public boolean puedeVer(Notificacion notificacion, String rol, Long clienteIdToken) {
        if (rol == null) {
            return false;
        }
        if (CLIENTE.equals(rol)) {
            return notificacion.getRolDestino() == null && Objects.equals(notificacion.getClienteId(), clienteIdToken);
        }
        return rol.equals(notificacion.getRolDestino());
    }

    private Long referencia(String routingKey, EventoNegocio e) {
        if (routingKey.startsWith("cotizacion.")) {
            return e.cotizacionId();
        }
        if (routingKey.startsWith("stock.")) {
            return e.idProducto();
        }
        return e.pedidoId();
    }

    private String recortar(String texto, int maximo) {
        return texto.length() <= maximo ? texto : texto.substring(0, maximo);
    }
}
