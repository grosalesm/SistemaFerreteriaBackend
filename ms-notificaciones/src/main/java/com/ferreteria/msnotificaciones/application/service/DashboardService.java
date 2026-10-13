package com.ferreteria.msnotificaciones.application.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ferreteria.msnotificaciones.application.port.outservice.MetricaOutService;
import com.ferreteria.msnotificaciones.application.port.outservice.NotificacionOutService;
import com.ferreteria.msnotificaciones.application.port.usecase.DashboardUseCase;
import com.ferreteria.msnotificaciones.domain.bean.MetricaCliente;
import com.ferreteria.msnotificaciones.domain.bean.MetricaDiaria;
import com.ferreteria.msnotificaciones.domain.bean.MetricaProducto;
import com.ferreteria.msnotificaciones.infrastructure.dto.ResumenResponse;
import com.ferreteria.msnotificaciones.infrastructure.mapper.NotificacionWebMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService implements DashboardUseCase {

    private static final int MAXIMO_DIAS = 366;

    private final MetricaOutService metricaOutService;
    private final NotificacionOutService notificacionOutService;
    private final NotificacionWebMapper notificacionWebMapper;

    @Override
    @Transactional(readOnly = true)
    public ResumenResponse resumen() {
        LocalDate hoy = LocalDate.now();
        LocalDate inicioMes = hoy.withDayOfMonth(1);
        LocalDate inicioSemana = hoy.minusDays(6);
        List<MetricaDiaria> dias = metricaOutService.listarDias(
                inicioSemana.isBefore(inicioMes) ? inicioSemana : inicioMes, hoy);

        List<MetricaDiaria> deHoy = desde(dias, hoy);
        List<MetricaDiaria> semana = desde(dias, inicioSemana);
        List<MetricaDiaria> mes = desde(dias, inicioMes);
        int pedidosMes = sumar(mes, MetricaDiaria::getPedidosPagados);
        int enviadasMes = sumar(mes, MetricaDiaria::getCotizacionesEnviadas);
        BigDecimal tasa = enviadasMes == 0 ? BigDecimal.ZERO
                : BigDecimal.valueOf(pedidosMes * 100L).divide(BigDecimal.valueOf(enviadasMes), 2, RoundingMode.HALF_UP);

        return new ResumenResponse(monto(deHoy), sumar(deHoy, MetricaDiaria::getPedidosPagados),
                monto(semana), sumar(semana, MetricaDiaria::getPedidosPagados),
                monto(mes), pedidosMes, enviadasMes, tasa,
                notificacionWebMapper.toResponses(notificacionOutService.ultimasAlertasStock()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MetricaDiaria> ventasPorDia(LocalDate desde, LocalDate hasta) {
        LocalDate fin = hasta == null ? LocalDate.now() : hasta;
        LocalDate inicio = desde == null ? fin.minusDays(29) : desde;
        if (inicio.isAfter(fin) || ChronoUnit.DAYS.between(inicio, fin) >= MAXIMO_DIAS) {
            throw new IllegalArgumentException("El rango de fechas no es válido (máximo " + MAXIMO_DIAS + " días)");
        }
        Map<LocalDate, MetricaDiaria> porFecha = metricaOutService.listarDias(inicio, fin).stream()
                .collect(Collectors.toMap(MetricaDiaria::getFecha, Function.identity()));
        List<MetricaDiaria> resultado = new ArrayList<>();
        for (LocalDate fecha = inicio; !fecha.isAfter(fin); fecha = fecha.plusDays(1)) {
            resultado.add(porFecha.getOrDefault(fecha, new MetricaDiaria(fecha)));
        }
        return resultado;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MetricaProducto> topProductos() {
        return metricaOutService.topProductos();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MetricaCliente> topClientes() {
        return metricaOutService.topClientes();
    }

    private List<MetricaDiaria> desde(List<MetricaDiaria> dias, LocalDate inicio) {
        return dias.stream().filter(d -> !d.getFecha().isBefore(inicio)).toList();
    }

    private int sumar(List<MetricaDiaria> dias, Function<MetricaDiaria, Integer> campo) {
        return dias.stream().mapToInt(campo::apply).sum();
    }

    private BigDecimal monto(List<MetricaDiaria> dias) {
        return dias.stream().map(MetricaDiaria::getMontoVendido).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
