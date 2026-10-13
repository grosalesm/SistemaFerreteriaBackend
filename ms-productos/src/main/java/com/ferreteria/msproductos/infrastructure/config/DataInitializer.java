package com.ferreteria.msproductos.infrastructure.config;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.ferreteria.msproductos.application.port.outservice.CategoriaOutService;
import com.ferreteria.msproductos.application.port.outservice.ProductoOutService;
import com.ferreteria.msproductos.application.port.outservice.StockOutService;
import com.ferreteria.msproductos.application.port.usecase.ProductoUseCase;
import com.ferreteria.msproductos.application.port.usecase.StockUseCase;
import com.ferreteria.msproductos.domain.bean.Almacen;
import com.ferreteria.msproductos.domain.bean.Categoria;
import com.ferreteria.msproductos.domain.bean.Producto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final StockOutService stockOutService;
    private final CategoriaOutService categoriaOutService;
    private final ProductoOutService productoOutService;
    private final ProductoUseCase productoUseCase;
    private final StockUseCase stockUseCase;

    @Override
    public void run(String... args) {
        if (stockOutService.listarAlmacenes().isEmpty()) {
            stockOutService.insertarAlmacen(new Almacen(null, "MOS", "Mostrador"));
            stockOutService.insertarAlmacen(new Almacen(null, "DEP", "Depósito"));
        }
        if (categoriaOutService.listar().isEmpty()) {
            for (String nombre : List.of("Cemento", "Fierro", "Pintura", "Herramientas", "Eléctrico", "Sanitario")) {
                categoriaOutService.insertar(new Categoria(null, nombre, true));
            }
        }
        if (productoOutService.listar(null, "").isEmpty()) {
            crearProductos();
        }
    }

    private void crearProductos() {
        Map<String, Long> categorias = new HashMap<>();
        categoriaOutService.listar().forEach(c -> categorias.put(c.getNombre(), c.getIdCategoria()));
        Long mostrador = stockOutService.listarAlmacenes().get(0).getIdAlmacen();
        Long deposito = stockOutService.listarAlmacenes().get(1).getIdAlmacen();

        crear("CEM-SOL-42", "Cemento Sol Tipo I 42.5 kg", "Cemento portland de uso general",
                categorias.get("Cemento"), "bolsa", "32.50", "30.90", 10, "5", 20, mostrador, 60, deposito, 400);
        crear("CEM-AND-42", "Cemento Andino Tipo V 42.5 kg", "Cemento resistente a sulfatos",
                categorias.get("Cemento"), "bolsa", "34.90", "33.00", 10, "5", 15, mostrador, 30, deposito, 150);
        crear("FIE-12-9M", "Fierro corrugado 1/2\" x 9 m", "Barra de construcción Aceros Arequipa",
                categorias.get("Fierro"), "varilla", "42.90", "40.50", 50, "3", 40, mostrador, 80, deposito, 500);
        crear("FIE-38-9M", "Fierro corrugado 3/8\" x 9 m", "Barra de construcción Aceros Arequipa",
                categorias.get("Fierro"), "varilla", "24.50", "23.20", 50, "3", 40, mostrador, 100, deposito, 600);
        crear("PIN-LAT-BL", "Pintura látex blanco 1 galón", "Pintura látex lavable para interiores",
                categorias.get("Pintura"), "galón", "45.00", "42.00", 6, "5", 10, mostrador, 25, deposito, 60);
        crear("HER-MAR-16", "Martillo de uña 16 oz", "Martillo con mango de fibra de vidrio",
                categorias.get("Herramientas"), "unidad", "28.00", "26.00", 12, "5", 5, mostrador, 15, deposito, 30);
        crear("ELE-CAB-14", "Cable THW 14 AWG x 100 m", "Rollo de cable eléctrico unipolar",
                categorias.get("Eléctrico"), "rollo", "129.90", "122.00", 5, "4", 5, mostrador, 10, deposito, 25);
        crear("SAN-TUB-4", "Tubo PVC desagüe 4\" x 3 m", "Tubo de PVC para desagüe",
                categorias.get("Sanitario"), "unidad", "22.50", "21.00", 20, "5", 15, mostrador, 40, deposito, 120);
    }

    private void crear(String sku, String nombre, String descripcion, Long idCategoria, String unidad,
                       String precioMinorista, String precioContratista, int cantidadMinima, String porcentaje,
                       int stockMinimo, Long mostrador, int stockMostrador, Long deposito, int stockDeposito) {
        Producto producto = productoUseCase.guardar(new Producto(null, sku, nombre, descripcion, idCategoria, unidad,
                new BigDecimal(precioMinorista), new BigDecimal(precioContratista), cantidadMinima,
                new BigDecimal(porcentaje), stockMinimo, null, null));
        stockUseCase.ingresar(producto.getIdProducto(), mostrador, stockMostrador);
        stockUseCase.ingresar(producto.getIdProducto(), deposito, stockDeposito);
    }
}
