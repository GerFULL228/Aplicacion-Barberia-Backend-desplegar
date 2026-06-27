package com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.controller;

import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.dto.FidelizacionTarjetaFiltro;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.dto.request.FidelizacionTarjetaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.dto.response.FidelizacionTarjetaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.service.IFidelizacionTarjetaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/fidelizacion-tarjetas")
public class FidelizacionTarjetaController {

    private final IFidelizacionTarjetaService service;

    @GetMapping
    @PreAuthorize("hasAuthority('FIDELIZACION_READ')")
    public ResponseEntity<ApiResponse<PageResponse<FidelizacionTarjetaResponseDTO>>> listar(@Valid @ModelAttribute FidelizacionTarjetaFiltro filtro, @PageableDefault(size = 10, page = 0) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok("Tarjetas obtenidas correctamente.", service.listarTarjetas(filtro, pageable)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('FIDELIZACION_READ')")
    public ResponseEntity<ApiResponse<FidelizacionTarjetaResponseDTO>> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Tarjeta obtenida correctamente.", service.obtenerTarjetaPorId(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('FIDELIZACION_MANAGE')")
    public ResponseEntity<ApiResponse<FidelizacionTarjetaResponseDTO>> crear(@RequestBody @Valid FidelizacionTarjetaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Tarjeta creada correctamente.", service.crearTarjeta(dto)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('FIDELIZACION_MANAGE')")
    public ResponseEntity<ApiResponse<FidelizacionTarjetaResponseDTO>> actualizar(@PathVariable Long id, @RequestBody @Valid FidelizacionTarjetaRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Tarjeta actualizada correctamente.", service.actualizarTarjeta(id, dto)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('FIDELIZACION_MANAGE')")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        service.eliminarTarjeta(id);
        return ResponseEntity.ok(ApiResponse.ok("Tarjeta eliminada correctamente."));
    }
}