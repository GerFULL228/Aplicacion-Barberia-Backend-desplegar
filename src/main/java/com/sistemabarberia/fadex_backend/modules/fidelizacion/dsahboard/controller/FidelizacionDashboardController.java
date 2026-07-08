package com.sistemabarberia.fadex_backend.modules.fidelizacion.dsahboard.controller;

import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.dsahboard.dto.response.FidelizacionDashboardResponseDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.dsahboard.service.IFidelizacionDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/fidelizacion/dashboard")
public class FidelizacionDashboardController {

    private final IFidelizacionDashboardService dashboardService;

    @GetMapping
    @PreAuthorize("hasAuthority('FIDELIZACION_READ')")
    public ResponseEntity<ApiResponse<FidelizacionDashboardResponseDTO>> obtenerDashboard() {
        return ResponseEntity.ok(ApiResponse.ok("Dashboard obtenido correctamente.", dashboardService.obtenerMiDashboard()));
    }
}