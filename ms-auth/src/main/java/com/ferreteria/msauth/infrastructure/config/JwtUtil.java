package com.ferreteria.msauth.infrastructure.config;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ferreteria.msauth.domain.bean.Usuario;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private static final long EXPIRACION_MS = 8 * 60 * 60 * 1000L; // 8 horas

    private final SecretKey clave;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.clave = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generarToken(Usuario usuario) {
        Date ahora = new Date();
        return Jwts.builder()
                .subject(String.valueOf(usuario.getIdUsuario()))
                .claim("username", usuario.getUsername())
                .claim("nombre", usuario.getNombreCompleto())
                .claim("rol", usuario.getRol().name())
                .claim("clienteId", usuario.getClienteId())
                .issuedAt(ahora)
                .expiration(new Date(ahora.getTime() + EXPIRACION_MS))
                .signWith(clave, Jwts.SIG.HS256)
                .compact();
    }
}
