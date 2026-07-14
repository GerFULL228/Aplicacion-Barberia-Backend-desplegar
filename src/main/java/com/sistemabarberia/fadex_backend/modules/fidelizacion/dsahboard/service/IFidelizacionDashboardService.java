package com.sistemabarberia.fadex_backend.modules.fidelizacion.dsahboard.service;

import com.sistemabarberia.fadex_backend.modules.fidelizacion.dsahboard.dto.response.FidelizacionDashboardAdminResponseDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.dsahboard.dto.response.FidelizacionDashboardClienteResponseDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.dto.response.MovimientoPorSemanaDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.giro.dto.response.GiroPorSemanaDTO;

import java.time.LocalDate;
import java.util.List;

public interface IFidelizacionDashboardService {
    FidelizacionDashboardClienteResponseDTO obtenerDashboardCliente();
    FidelizacionDashboardAdminResponseDTO obtenerDashboardAdmin();
    List<GiroPorSemanaDTO> obtenerGirosPorSemana(LocalDate fechaInicio, LocalDate fechaFin);
    List<MovimientoPorSemanaDTO> obtenerMovimientosPorSemana(LocalDate fechaInicio, LocalDate fechaFin);
}