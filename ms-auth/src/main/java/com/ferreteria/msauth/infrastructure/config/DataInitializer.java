package com.ferreteria.msauth.infrastructure.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.ferreteria.msauth.application.port.outservice.UsuarioOutService;
import com.ferreteria.msauth.application.port.usecase.UsuarioUseCase;
import com.ferreteria.msauth.domain.bean.Rol;
import com.ferreteria.msauth.domain.bean.Usuario;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioOutService usuarioOutService;
    private final UsuarioUseCase usuarioUseCase;

    @Override
    public void run(String... args) {
        if (usuarioOutService.obtenerPorUsername("admin").isPresent()) {
            return;
        }
        Usuario admin = new Usuario();
        admin.setUsername("admin");
        admin.setNombreCompleto("Administrador");
        admin.setEmail("admin@ferreteria.com");
        admin.setPassword("Admin123");
        admin.setRol(Rol.ADMIN);
        usuarioUseCase.guardar(admin); // encripta el password y lo deja activo
    }
}
