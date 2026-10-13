package com.ferreteria.msnotificaciones.infrastructure.persistence.adapter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.ferreteria.msnotificaciones.application.port.outservice.NotificacionOutService;
import com.ferreteria.msnotificaciones.domain.bean.Notificacion;
import com.ferreteria.msnotificaciones.infrastructure.mapper.NotificacionMapper;
import com.ferreteria.msnotificaciones.infrastructure.persistence.entity.EventoProcesadoEntity;
import com.ferreteria.msnotificaciones.infrastructure.persistence.repository.EventoProcesadoRepository;
import com.ferreteria.msnotificaciones.infrastructure.persistence.repository.NotificacionRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificacionAdapter implements NotificacionOutService {

    private final NotificacionRepository notificacionRepository;
    private final EventoProcesadoRepository eventoProcesadoRepository;
    private final NotificacionMapper notificacionMapper;

    @Override
    public Notificacion guardar(Notificacion notificacion) {
        return notificacionMapper.toBean(notificacionRepository.save(notificacionMapper.toEntity(notificacion)));
    }

    @Override
    public Optional<Notificacion> obtenerPorId(Long idNotificacion) {
        return notificacionRepository.findById(idNotificacion).map(notificacionMapper::toBean);
    }

    @Override
    public List<Notificacion> listarPorRol(String rol, boolean soloNoLeidas) {
        return notificacionMapper.toBeans(soloNoLeidas
                ? notificacionRepository.findTop50ByRolDestinoAndLeidaFalseOrderByFechaDesc(rol)
                : notificacionRepository.findTop50ByRolDestinoOrderByFechaDesc(rol));
    }

    @Override
    public List<Notificacion> listarPorCliente(Long clienteId, boolean soloNoLeidas) {
        return notificacionMapper.toBeans(soloNoLeidas
                ? notificacionRepository.findTop50ByClienteIdAndRolDestinoIsNullAndLeidaFalseOrderByFechaDesc(clienteId)
                : notificacionRepository.findTop50ByClienteIdAndRolDestinoIsNullOrderByFechaDesc(clienteId));
    }

    @Override
    public List<Notificacion> ultimasAlertasStock() {
        return notificacionMapper.toBeans(
                notificacionRepository.findTop5ByTipoAndRolDestinoOrderByFechaDesc("stock.critico", "ADMIN"));
    }

    @Override
    public boolean eventoProcesado(String clave) {
        return eventoProcesadoRepository.existsById(clave);
    }

    @Override
    public void registrarEvento(String clave) {
        eventoProcesadoRepository.save(new EventoProcesadoEntity(clave, LocalDateTime.now()));
    }
}
