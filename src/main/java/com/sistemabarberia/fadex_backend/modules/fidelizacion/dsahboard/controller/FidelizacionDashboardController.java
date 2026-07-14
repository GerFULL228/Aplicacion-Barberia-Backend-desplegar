package com.sistemabarberia.fadex_backend.modules.fidelizacion.dsahboard.controller;

import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.dsahboard.dto.response.FidelizacionDashboardAdminResponseDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.dsahboard.dto.response.FidelizacionDashboardClienteResponseDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.dsahboard.service.IFidelizacionDashboardService;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.dto.response.MovimientoPorSemanaDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.giro.dto.response.GiroPorSemanaDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/fidelizacion/dashboard")
public class FidelizacionDashboardController {

    private final IFidelizacionDashboardService dashboardService;

    @GetMapping("/cliente")
    @PreAuthorize("hasAuthority('FIDELIZACION_READ')")
    public ResponseEntity<ApiResponse<FidelizacionDashboardClienteResponseDTO>> dashboardCliente() {
        return ResponseEntity.ok(ApiResponse.ok("Dashboard obtenido correctamente.", dashboardService.obtenerDashboardCliente()));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('FIDELIZACION_MANAGE')")
    public ResponseEntity<ApiResponse<FidelizacionDashboardAdminResponseDTO>> dashboardAdmin() {
        return ResponseEntity.ok(ApiResponse.ok("Dashboard obtenido correctamente.", dashboardService.obtenerDashboardAdmin()));
    }

    @GetMapping("/admin/giros-por-semana")
    @PreAuthorize("hasAuthority('FIDELIZACION_MANAGE')")
    public ResponseEntity<ApiResponse<List<GiroPorSemanaDTO>>> girosPorSemana(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        return ResponseEntity.ok(ApiResponse.ok("Giros por semana obtenidos correctamente.", dashboardService.obtenerGirosPorSemana(fechaInicio, fechaFin)));
    }

    @GetMapping("/admin/movimientos-por-semana")
    @PreAuthorize("hasAuthority('FIDELIZACION_MANAGE')")
    public ResponseEntity<ApiResponse<List<MovimientoPorSemanaDTO>>> movimientosPorSemana(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        return ResponseEntity.ok(ApiResponse.ok("Movimientos por semana obtenidos correctamente.", dashboardService.obtenerMovimientosPorSemana(fechaInicio, fechaFin)));
    }
}