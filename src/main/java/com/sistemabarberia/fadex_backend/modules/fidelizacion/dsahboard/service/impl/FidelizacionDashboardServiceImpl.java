package com.sistemabarberia.fadex_backend.modules.fidelizacion.dsahboard.service.impl;

import com.sistemabarberia.fadex_backend.modules.fidelizacion.dsahboard.dto.response.FidelizacionDashboardResponseDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.dsahboard.service.IFidelizacionDashboardService;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.dto.response.FidelizacionMovimientoResponseDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.service.IFidelizacionMovimientoService;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.dto.response.FidelizacionTarjetaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.service.IFidelizacionTarjetaService;
import com.sistemabarberia.fadex_backend.modules.ruleta.giro.dto.response.RuletaGiroResponseDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.giro.service.IRuletaGiroService;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.dto.response.RecompensaObtenidaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.entity.enums.EstadoRecompensa;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.service.IRecompensaObtenidaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FidelizacionDashboardServiceImpl implements IFidelizacionDashboardService {

    private final IFidelizacionTarjetaService tarjetaService;
    private final IFidelizacionMovimientoService movimientoService;
    private final IRuletaGiroService giroService;
    private final IRecompensaObtenidaService recompensaService;

    @Override
    @Transactional(readOnly = true)
    public FidelizacionDashboardResponseDTO obtenerMiDashboard() {
        List<FidelizacionTarjetaResponseDTO> tarjetas = tarjetaService.obtenerMisTarjetas();
        List<FidelizacionMovimientoResponseDTO> movimientos = movimientoService.obtenerMisMovimientos();
        List<RuletaGiroResponseDTO> giros = giroService.obtenerMisGiros();
        List<RecompensaObtenidaResponseDTO> recompensas = recompensaService.obtenerMisRecompensas();
        int totalTarjetas = tarjetas.size();
        int girosDisponibles = tarjetas.stream().mapToInt(FidelizacionTarjetaResponseDTO::getGirosDisponibles).sum();
        int tarjetasConGiroDisponible = (int) tarjetas.stream().filter(t -> t.getGirosDisponibles() > 0).count();
        int recompensasPendientes = (int) recompensas.stream().filter(r -> r.getEstado() == EstadoRecompensa.PENDIENTE).count();
        return FidelizacionDashboardResponseDTO.builder().totalTarjetas(totalTarjetas).girosDisponibles(girosDisponibles).tarjetasConGiroDisponible(tarjetasConGiroDisponible)
                .recompensasPendientes(recompensasPendientes).tarjetas(tarjetas).movimientosRecientes(movimientos.stream().limit(10).toList())
                .ultimosGiros(giros.stream().limit(10).toList()).recompensas(recompensas).build();
    }
}