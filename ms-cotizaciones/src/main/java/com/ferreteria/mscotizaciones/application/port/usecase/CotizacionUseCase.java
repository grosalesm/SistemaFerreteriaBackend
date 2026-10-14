package com.ferreteria.mscotizaciones.application.port.usecase;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.ferreteria.mscotizaciones.domain.bean.Cotizacion;
import com.ferreteria.mscotizaciones.domain.bean.EstadoCotizacion;
import com.ferreteria.mscotizaciones.domain.bean.ItemCotizacion;

public interface CotizacionUseCase {

    List<Cotizacion> listar(EstadoCotizacion estado, Long clienteId, LocalDate desde, LocalDate hasta, String rol, Long clienteIdToken);
    Optional<Cotizacion> obtenerPorId(Long idCotizacion, String rol, Long clienteIdToken);
    Cotizacion crear(Cotizacion datos, Long usuarioId, String usuarioNombre, String rol, Long clienteIdToken);
    Cotizacion simular(Cotizacion datos, String rol, Long clienteIdToken);
    Optional<Cotizacion> editarItems(Long idCotizacion, List<ItemCotizacion> items, Long usuarioId, String rol, Long clienteIdToken);
    Optional<Cotizacion> enviar(Long idCotizacion, Long usuarioId, String usuarioNombre, String rol, Long clienteIdToken);
    Optional<Cotizacion> aprobar(Long idCotizacion, Long usuarioId, String usuarioNombre, String rol, Long clienteIdToken);
    Optional<Cotizacion> rechazar(Long idCotizacion, String motivo, Long usuarioId, String usuarioNombre, String rol, Long clienteIdToken);
    Optional<Cotizacion> reenviar(Long idCotizacion, Long usuarioId, String usuarioNombre, String rol, Long clienteIdToken);
    Optional<Cotizacion> duplicar(Long idCotizacion, Long usuarioId, String usuarioNombre, String rol, Long clienteIdToken);
    int vencerCotizaciones();
    boolean marcarConvertida(Long cotizacionId, Long pedidoId);
}
