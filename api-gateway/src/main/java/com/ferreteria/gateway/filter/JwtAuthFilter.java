package com.ferreteria.gateway.filter;

import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private static final List<String> RUTAS_PUBLICAS = List.of(
            "/api/auth/login",
            "/api/auth/registro",
            "/fallback/**");

    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final SecretKey clave;

    public JwtAuthFilter(@Value("${jwt.secret}") String secret) {
        this.clave = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Nunca se aceptan estas cabeceras desde el cliente: solo las pone el gateway
        ServerHttpRequest request = exchange.getRequest().mutate()
                .headers(h -> {
                    h.remove("X-User-Id");
                    h.remove("X-User-Role");
                    h.remove("X-Cliente-Id");
                })
                .build();
        String path = request.getPath().value();

        if (request.getMethod() == HttpMethod.OPTIONS || esPublica(path)) {
            return chain.filter(exchange.mutate().request(request).build());
        }

        String token = obtenerToken(request, path);
        if (token == null || token.isBlank()) {
            return responder(exchange, HttpStatus.UNAUTHORIZED, "Token no enviado");
        }

        Claims claims;
        try {
            claims = Jwts.parser().verifyWith(clave).build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            return responder(exchange, HttpStatus.UNAUTHORIZED, "Token inválido o expirado");
        }

        String rol = claims.get("rol", String.class);
        if (!tienePermiso(path, request.getMethod(), rol)) {
            return responder(exchange, HttpStatus.FORBIDDEN, "No tiene permisos para este recurso");
        }

        Object clienteId = claims.get("clienteId");
        ServerHttpRequest requestConUsuario = request.mutate()
                .headers(h -> {
                    h.set("X-User-Id", claims.getSubject());
                    h.set("X-User-Role", rol);
                    if (clienteId != null) {
                        h.set("X-Cliente-Id", clienteId.toString());
                    }
                })
                .build();
        return chain.filter(exchange.mutate().request(requestConUsuario).build());
    }

    @Override
    public int getOrder() {
        return -1;
    }

    // El WebSocket del navegador no puede enviar cabeceras: en /ws/** el token llega como ?token=
    private String obtenerToken(ServerHttpRequest request, String path) {
        if (pathMatcher.match("/ws/**", path)) {
            return request.getQueryParams().getFirst("token");
        }
        String header = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }
        return header.substring(7);
    }

    // Reglas de acceso por rol. Las rutas que no aparecen aquí solo necesitan un token válido.
    private boolean tienePermiso(String path, HttpMethod metodo, String rol) {
        if (pathMatcher.match("/api/usuarios/**", path)) {
            return rolEn(rol, "ADMIN");
        }
        if (pathMatcher.match("/api/clientes/me", path)) {
            return rolEn(rol, "CLIENTE");
        }
        if (pathMatcher.match("/api/clientes/**", path)) {
            if (metodo == HttpMethod.GET) {
                return rolEn(rol, "ADMIN", "VENDEDOR", "CLIENTE");
            }
            if (metodo == HttpMethod.POST || metodo == HttpMethod.PUT) {
                return rolEn(rol, "ADMIN", "VENDEDOR");
            }
            if (metodo == HttpMethod.PATCH
                    && (pathMatcher.match("/api/clientes/*/tipo", path)
                        || pathMatcher.match("/api/clientes/*/estado", path))) {
                return rolEn(rol, "ADMIN");
            }
            return false; // cualquier otra operación sobre clientes se niega
        }
        if (pathMatcher.match("/api/productos/*/kardex", path)) {
            return rolEn(rol, "ADMIN", "ALMACENERO");
        }
        if (pathMatcher.match("/api/stock/**", path)) {
            if (metodo == HttpMethod.GET && pathMatcher.match("/api/stock", path)) {
                return rolEn(rol, "ADMIN", "ALMACENERO", "VENDEDOR");
            }
            return rolEn(rol, "ADMIN", "ALMACENERO");
        }
        if (pathMatcher.match("/api/cotizaciones/**", path)) {
            return rolEn(rol, "ADMIN", "VENDEDOR", "CLIENTE");
        }
        boolean esCatalogo = pathMatcher.match("/api/productos/**", path)
                || pathMatcher.match("/api/categorias/**", path);
        if (esCatalogo || pathMatcher.match("/api/almacenes/**", path)) {
            if (metodo == HttpMethod.GET) {
                return true;
            }
            if (esCatalogo && (metodo == HttpMethod.POST || metodo == HttpMethod.PUT || metodo == HttpMethod.PATCH)) {
                return rolEn(rol, "ADMIN");
            }
            return false;
        }
        return true;
    }

    private boolean rolEn(String rol, String... permitidos) {
        return rol != null && List.of(permitidos).contains(rol);
    }

    private boolean esPublica(String path) {
        return RUTAS_PUBLICAS.stream().anyMatch(ruta -> pathMatcher.match(ruta, path));
    }

    private Mono<Void> responder(ServerWebExchange exchange, HttpStatus status, String mensaje) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String json = "{\"status\":" + status.value() + ",\"mensaje\":\"" + mensaje + "\"}";
        DataBuffer buffer = response.bufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}
