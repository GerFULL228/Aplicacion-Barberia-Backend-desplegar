package com.sistemabarberia.fadex_backend.modules.reserva.controller;

import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import com.sistemabarberia.fadex_backend.modules.reserva.dto.Response.HistorialCorteDTO;
import com.sistemabarberia.fadex_backend.modules.reserva.service.BarberoHistorialService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/barbero/historial")
@RequiredArgsConstructor
public class BarberoHistorialController {

    private final BarberoHistorialService barberoHistorialService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<HistorialCorteDTO>>> getHistorial(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String desde,
            @RequestParam(required = false) String hasta,
            @RequestParam(required = false) String clienteNombre
    ) {
        List<HistorialCorteDTO> historial = barberoHistorialService.getHistorial(
                userDetails.getUsername(), desde, hasta, clienteNombre
        );
        return ResponseEntity.ok(ApiResponse.success(historial));
    }
}
