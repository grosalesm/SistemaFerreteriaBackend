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
            for (String nombre : List.of("Cemento y agregados", "Fierro y acero", "Ladrillos", "Pinturas",
                    "Herramientas", "Electricidad", "Gasfitería", "Fijaciones")) {
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

        Long cemento = categorias.get("Cemento y agregados");
        Long fierro = categorias.get("Fierro y acero");
        Long ladrillos = categorias.get("Ladrillos");
        Long pinturas = categorias.get("Pinturas");
        Long herramientas = categorias.get("Herramientas");
        Long electricidad = categorias.get("Electricidad");
        Long gasfiteria = categorias.get("Gasfitería");
        Long fijaciones = categorias.get("Fijaciones");

        // sku, nombre, descripción, categoría, unidad, precio minorista, precio contratista,
        // cantidad mínima para descuento, % descuento, stock mínimo, stock mostrador, stock depósito
        crear("CEM-SOL-42", "Cemento Sol Tipo I 42.5 kg", "Cemento portland de uso general",
                cemento, "bolsa", "31.50", "29.90", 20, "4", 30, mostrador, 80, deposito, 600);
        crear("CEM-AND-42", "Cemento Andino Tipo V 42.5 kg", "Cemento resistente a sulfatos para cimientos",
                cemento, "bolsa", "34.90", "33.00", 20, "4", 20, mostrador, 40, deposito, 250);
        crear("CEM-APU-42", "Cemento Apu Tipo GU 42.5 kg", "Cemento de uso general para albañilería",
                cemento, "bolsa", "28.90", "27.50", 20, "4", 20, mostrador, 50, deposito, 300);
        crear("AGR-ARE-GRU", "Arena gruesa x m3", "Arena gruesa para concreto y tarrajeo",
                cemento, "m3", "75.00", "70.00", 5, "5", 5, mostrador, 12, deposito, 40);
        crear("AGR-PIE-12", "Piedra chancada 1/2\" x m3", "Piedra chancada para concreto",
                cemento, "m3", "85.00", "80.00", 5, "5", 5, mostrador, 10, deposito, 35);

        crear("FIE-38-9M", "Fierro corrugado 3/8\" x 9 m Aceros Arequipa", "Barra de construcción grado 60",
                fierro, "varilla", "24.90", "23.50", 50, "3", 50, mostrador, 120, deposito, 800);
        crear("FIE-12-9M", "Fierro corrugado 1/2\" x 9 m Aceros Arequipa", "Barra de construcción grado 60",
                fierro, "varilla", "43.50", "41.20", 50, "3", 40, mostrador, 90, deposito, 600);
        crear("FIE-58-9M", "Fierro corrugado 5/8\" x 9 m Aceros Arequipa", "Barra de construcción grado 60",
                fierro, "varilla", "68.90", "65.50", 30, "3", 20, mostrador, 25, deposito, 200);
        crear("ALA-REC-16", "Alambre recocido N.° 16 x kg", "Alambre negro para amarre de fierro",
                fierro, "kg", "6.50", "6.00", 25, "5", 30, mostrador, 60, deposito, 300);

        crear("LAD-KK-18H", "Ladrillo King Kong 18 huecos Lark", "Ladrillo para muros portantes",
                ladrillos, "millar", "1150.00", "1080.00", 3, "4", 3, mostrador, 6, deposito, 12);
        crear("LAD-PAN-TE", "Ladrillo pandereta Lark", "Ladrillo para tabiquería",
                ladrillos, "millar", "890.00", "840.00", 3, "4", 3, mostrador, 5, deposito, 10);

        crear("PIN-LAT-BL", "Pintura látex Vencedor blanco 1 gal", "Látex lavable para interiores",
                pinturas, "galón", "42.90", "39.90", 6, "5", 10, mostrador, 30, deposito, 80);
        crear("PIN-LAT-B4", "Pintura látex Vencedor blanco 4 gal", "Látex lavable para interiores, balde",
                pinturas, "balde", "155.00", "145.00", 3, "5", 5, mostrador, 8, deposito, 30);
        crear("PIN-ESM-NE", "Esmalte sintético CPP negro 1 gal", "Esmalte para metal y madera",
                pinturas, "galón", "58.50", "55.00", 4, "5", 6, mostrador, 7, deposito, 20);
        crear("PIN-IMP-1G", "Imprimante para muros 1 gal", "Base para pintura en muros nuevos",
                pinturas, "galón", "24.90", "22.90", 6, "5", 10, mostrador, 20, deposito, 50);
        crear("PIN-THI-1G", "Thinner acrílico 1 gal", "Disolvente para esmaltes y lacas",
                pinturas, "galón", "28.00", "26.00", 6, "5", 8, mostrador, 9, deposito, 15);

        crear("HER-MAR-16", "Martillo de uña 16 oz Stanley", "Mango de fibra de vidrio",
                herramientas, "unidad", "39.90", "37.00", 6, "5", 5, mostrador, 12, deposito, 20);
        crear("HER-WIN-8M", "Wincha 8 m Truper", "Cinta métrica con freno",
                herramientas, "unidad", "29.90", "27.50", 6, "5", 5, mostrador, 6, deposito, 10);
        crear("HER-CAR-BU", "Carretilla buggy 5.5 p3 Truper", "Carretilla con llanta neumática",
                herramientas, "unidad", "189.00", "175.00", 3, "4", 3, mostrador, 2, deposito, 8);
        crear("HER-PAL-CU", "Pala cuchara Tramontina", "Pala de acero con mango de madera",
                herramientas, "unidad", "35.90", "33.00", 6, "5", 5, mostrador, 10, deposito, 25);
        crear("HER-TAL-BOS", "Taladro percutor 1/2\" 750 W Bosch", "Taladro con percusión y velocidad variable",
                herramientas, "unidad", "299.00", "279.00", 2, "3", 2, mostrador, 4, deposito, 6);

        crear("ELE-CAB-14", "Cable THW 14 AWG x 100 m Indeco", "Rollo de cable unipolar",
                electricidad, "rollo", "139.90", "132.00", 5, "4", 5, mostrador, 8, deposito, 25);
        crear("ELE-CAB-12", "Cable THW 12 AWG x 100 m Indeco", "Rollo de cable unipolar",
                electricidad, "rollo", "209.00", "198.00", 5, "4", 5, mostrador, 6, deposito, 20);
        crear("ELE-INT-DO", "Interruptor doble Bticino", "Interruptor de empotrar con placa",
                electricidad, "unidad", "14.50", "13.20", 20, "5", 15, mostrador, 40, deposito, 100);
        crear("ELE-TOM-DO", "Tomacorriente doble con línea a tierra Bticino", "Tomacorriente de empotrar con placa",
                electricidad, "unidad", "16.90", "15.50", 20, "5", 15, mostrador, 35, deposito, 100);
        crear("ELE-FOC-12", "Foco LED 12 W luz blanca Philips", "Foco LED rosca E27",
                electricidad, "unidad", "9.90", "8.90", 24, "6", 20, mostrador, 50, deposito, 150);

        crear("GAS-TUB-12", "Tubo PVC agua 1/2\" x 5 m Pavco", "Tubo a presión clase 10",
                gasfiteria, "unidad", "11.90", "10.90", 20, "5", 20, mostrador, 45, deposito, 150);
        crear("GAS-TUB-4D", "Tubo PVC desagüe 4\" x 3 m Pavco", "Tubo para desagüe",
                gasfiteria, "unidad", "24.50", "22.90", 10, "5", 10, mostrador, 20, deposito, 80);
        crear("GAS-COD-12", "Codo PVC agua 1/2\" x 90°", "Accesorio roscado",
                gasfiteria, "unidad", "1.50", "1.30", 50, "8", 50, mostrador, 150, deposito, 400);
        crear("GAS-PEG-OAT", "Pegamento PVC Oatey 1/4 gal", "Soldadura líquida para tubería PVC",
                gasfiteria, "unidad", "32.00", "30.00", 6, "5", 5, mostrador, 6, deposito, 15);
        crear("GAS-LLA-12", "Llave de paso 1/2\" Vainsa", "Llave esférica de bronce",
                gasfiteria, "unidad", "29.90", "27.90", 10, "5", 8, mostrador, 12, deposito, 30);

        crear("FIJ-CLA-3P", "Clavo para madera 3\" x kg", "Clavo de acero con cabeza",
                fijaciones, "kg", "7.50", "6.90", 25, "5", 25, mostrador, 40, deposito, 200);
        crear("FIJ-CLA-CO", "Clavo para concreto 2 1/2\" x kg", "Clavo de acero templado",
                fijaciones, "kg", "12.90", "11.90", 10, "5", 10, mostrador, 18, deposito, 60);
        crear("FIJ-TAR-8", "Tarugo plástico N.° 8 x 100 unid.", "Tarugo con tornillo para muro",
                fijaciones, "bolsa", "8.90", "8.00", 10, "5", 10, mostrador, 25, deposito, 60);
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
