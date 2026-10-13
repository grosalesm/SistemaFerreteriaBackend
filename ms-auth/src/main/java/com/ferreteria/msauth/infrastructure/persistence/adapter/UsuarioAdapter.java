package com.ferreteria.msauth.infrastructure.persistence.adapter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.ferreteria.msauth.application.port.outservice.UsuarioOutService;
import com.ferreteria.msauth.domain.bean.Usuario;
import com.ferreteria.msauth.infrastructure.mapper.UsuarioMapper;
import com.ferreteria.msauth.infrastructure.persistence.entity.UsuarioEntity;
import com.ferreteria.msauth.infrastructure.persistence.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UsuarioAdapter implements UsuarioOutService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    @Override
    public List<Usuario> listar() {
        return usuarioMapper.toBeans(usuarioRepository.findAll());
    }

    @Override
    public Optional<Usuario> obtenerPorId(Long idUsuario) {
        return usuarioRepository.findById(idUsuario).map(usuarioMapper::toBean);
    }

    @Override
    public Optional<Usuario> obtenerPorUsername(String username) {
        return usuarioRepository.findByUsername(username).map(usuarioMapper::toBean);
    }

    @Override
    public Optional<Usuario> obtenerPorEmail(String email) {
        return usuarioRepository.findByEmail(email).map(usuarioMapper::toBean);
    }

    @Override
    public Usuario insertar(Usuario usuario) {
        UsuarioEntity entity = usuarioMapper.toEntity(usuario);
        entity.setIdUsuario(null);
        entity.setActivo(true);
        entity.setFechaCreacion(LocalDateTime.now());
        return usuarioMapper.toBean(usuarioRepository.save(entity));
    }

    @Override
    public Optional<Usuario> actualizar(Long idUsuario, Usuario usuario) {
        return usuarioRepository.findById(idUsuario)
                .map(entity -> {
                    entity.setUsername(usuario.getUsername());
                    entity.setNombreCompleto(usuario.getNombreCompleto());
                    entity.setEmail(usuario.getEmail());
                    entity.setPassword(usuario.getPassword());
                    entity.setRol(usuario.getRol());
                    entity.setClienteId(usuario.getClienteId());
                    entity.setActivo(usuario.getActivo());
                    return usuarioMapper.toBean(usuarioRepository.save(entity));
                });
    }
}
