package com.ferreteria.msauth.infrastructure.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (usuarioOutService.obtenerPorUsername("admin").isPresent()) {
            return;
        }
        // Personal de la ferretería (password = username + 123)
        crearInterno("admin", "Ricardo Manuel Salazar Vega", Rol.ADMIN);
        crearInterno("vendedor", "Jorge Luis Paredes Ramírez", Rol.VENDEDOR);
        crearInterno("cajero", "Milagros Ruiz Castañeda", Rol.CAJERO);
        crearInterno("almacenero", "Juan Carlos Huamaní Ccori", Rol.ALMACENERO);

        // Clientes de la app: clienteId 1 y 2 los crea el DataInitializer de ms-clientes
        crearCliente("usuario", "Luis Alberto Quispe Huamán", 1L);
        crearCliente("contratista", "Constructora Los Andes S.A.C.", 2L);
    }

    private void crearInterno(String username, String nombreCompleto, Rol rol) {
        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setNombreCompleto(nombreCompleto);
        usuario.setEmail(username + "@ferreteria.com");
        usuario.setPassword(username + "123");
        usuario.setRol(rol);
        usuarioUseCase.guardar(usuario); // encripta el password y lo deja activo
    }

    private void crearCliente(String username, String nombreCompleto, Long clienteId) {
        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setNombreCompleto(nombreCompleto);
        usuario.setEmail(username + "@email.com");
        usuario.setPassword(passwordEncoder.encode(username + "123"));
        usuario.setRol(Rol.CLIENTE);
        usuario.setClienteId(clienteId);
        usuarioOutService.insertar(usuario);
    }
}
