package com.sistemabarberia.fadex_backend.modules.reserva.controller;

import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import com.sistemabarberia.fadex_backend.modules.reserva.dto.reponse.ReservaDTO;
import com.sistemabarberia.fadex_backend.modules.reserva.dto.reponse.ResumenDiarioDTO;
import com.sistemabarberia.fadex_backend.modules.reserva.dto.reponse.ResumenSemanalDTO;
import com.sistemabarberia.fadex_backend.modules.reserva.service.IGestionAtencionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reservas")
@RequiredArgsConstructor
public class ReservaController {


    private final IGestionAtencionService gestionAtencionService;

    // Dashboard diario completo
    @GetMapping("/barbero/{barberoId}/hoy")
    public ResponseEntity<ApiResponse<ResumenDiarioDTO>> resumenDiario(
            @PathVariable Integer barberoId) {
        return ResponseEntity.ok(ApiResponse.ok("Resumen diario obtenido correctamente",
                        gestionAtencionService.obtenerResumenDiario(barberoId)
                )
        );
    }

    // PENDIENTE → EN_PROCESO
    @PutMapping("/{id}/iniciar")
    public ResponseEntity<ApiResponse<ReservaDTO>> iniciar(
            @PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok("Atención iniciada correctamente",
                        gestionAtencionService.iniciarAtencion(id)
                )
        );
    }

    // EN_PROCESO → COMPLETADO
    @PutMapping("/{id}/finalizar")
    public ResponseEntity<ApiResponse<ReservaDTO>> finalizar(
            @PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok("Atención finalizada correctamente",
                        gestionAtencionService.finalizarAtencion(id)
                )
        );
    }

    // Resumen semanal para gráficas
    @GetMapping("/barbero/{barberoId}/semanal")
    public ResponseEntity<ApiResponse<ResumenSemanalDTO>> resumenSemanal(
            @PathVariable Integer barberoId) {
        return ResponseEntity.ok( ApiResponse.ok("Resumen semanal obtenido correctamente",
                        gestionAtencionService.obtenerResumenSemanal(barberoId)
                )
        );
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<ApiResponse<ReservaDTO>> cancelar(
            @PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok("Reserva cancelada correctamente",
                        gestionAtencionService.cancelarReserva(id)
                )
        );
    }
}
