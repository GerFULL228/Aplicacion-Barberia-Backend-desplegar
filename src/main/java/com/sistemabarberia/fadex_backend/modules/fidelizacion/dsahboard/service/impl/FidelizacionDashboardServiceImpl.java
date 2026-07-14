package com.sistemabarberia.fadex_backend.modules.fidelizacion.dsahboard.service.impl;

import com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.service.IFidelizacionConfiguracionService;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.dsahboard.dto.response.FidelizacionDashboardAdminResponseDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.dsahboard.dto.response.FidelizacionDashboardClienteResponseDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.dsahboard.service.IFidelizacionDashboardService;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.dto.response.FidelizacionMovimientoResponseDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.dto.response.MovimientoPorSemanaDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.repository.FidelizacionMovimientoRepository;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.service.IFidelizacionMovimientoService;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.dto.response.FidelizacionTarjetaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.service.IFidelizacionTarjetaService;
import com.sistemabarberia.fadex_backend.modules.ruleta.giro.dto.response.GiroPorSemanaDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.giro.dto.response.RuletaGiroResponseDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.giro.repository.RuletaGiroRepository;
import com.sistemabarberia.fadex_backend.modules.ruleta.giro.service.IRuletaGiroService;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.dto.response.RecompensaObtenidaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.entity.enums.EstadoRecompensa;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.service.IRecompensaObtenidaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FidelizacionDashboardServiceImpl implements IFidelizacionDashboardService {

    private final IFidelizacionTarjetaService tarjetaService;
    private final IFidelizacionMovimientoService movimientoService;
    private final IRuletaGiroService giroService;
    private final IRecompensaObtenidaService recompensaService;
    private final IFidelizacionConfiguracionService configuracionService;
    private final FidelizacionMovimientoRepository movimientoRepository;
    private final RuletaGiroRepository giroRepository;


    @Override
    @Transactional(readOnly = true)
    public FidelizacionDashboardClienteResponseDTO obtenerDashboardCliente() {
        List<FidelizacionTarjetaResponseDTO> tarjetas = tarjetaService.obtenerMisTarjetas();
        List<FidelizacionMovimientoResponseDTO> movimientos = movimientoService.obtenerMisMovimientos();
        List<RuletaGiroResponseDTO> giros = giroService.obtenerMisGiros();
        List<RecompensaObtenidaResponseDTO> recompensas = recompensaService.obtenerMisRecompensas();
        int totalTarjetas = tarjetas.size();
        int girosDisponibles = tarjetas.stream().mapToInt(FidelizacionTarjetaResponseDTO::getGirosDisponibles).sum();
        int tarjetasConGiroDisponible = (int) tarjetas.stream().filter(t -> t.getGirosDisponibles() > 0).count();
        int recompensasPendientes = (int) recompensas.stream().filter(r -> r.getEstado() == EstadoRecompensa.PENDIENTE).count();
        return FidelizacionDashboardClienteResponseDTO.builder().totalTarjetas(totalTarjetas).girosDisponibles(girosDisponibles).tarjetasConGiroDisponible(tarjetasConGiroDisponible)
                .recompensasPendientes(recompensasPendientes).tarjetas(tarjetas).movimientosRecientes(movimientos.stream().limit(10).toList())
                .ultimosGiros(giros.stream().limit(10).toList()).recompensas(recompensas).build();
    }

    @Override
    @Transactional(readOnly = true)
    public FidelizacionDashboardAdminResponseDTO obtenerDashboardAdmin() {
        return FidelizacionDashboardAdminResponseDTO.builder().totalTarjetas(tarjetaService.contarTarjetas()).totalConfiguraciones(configuracionService.contarConfiguraciones())
                .totalGiros(giroService.contarGiros()).totalRecompensas(recompensaService.contarRecompensas()).movimientosRecientes(movimientoService.obtenerUltimosMovimientos(10))
                .ultimasRecompensas(recompensaService.obtenerUltimasRecompensas(10)).tarjetasPorCategoria(tarjetaService.obtenerTarjetasPorCategoria()).build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<GiroPorSemanaDTO> obtenerGirosPorSemana(LocalDate fechaInicio, LocalDate fechaFin) {
        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(23, 59, 59);
        return giroRepository.contarGirosPorSemanaRaw(inicio, fin).stream().map(row -> new GiroPorSemanaDTO(((java.sql.Date) row[0]).toLocalDate(), ((Number) row[1]).longValue())).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoPorSemanaDTO> obtenerMovimientosPorSemana(LocalDate fechaInicio, LocalDate fechaFin) {
        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(23, 59, 59);
        return movimientoRepository.contarMovimientosPorSemanaRaw(inicio, fin).stream().map(row -> new MovimientoPorSemanaDTO(((java.sql.Date) row[0]).toLocalDate(), ((Number) row[1]).longValue(), ((Number) row[2]).longValue())).toList();
    }
}