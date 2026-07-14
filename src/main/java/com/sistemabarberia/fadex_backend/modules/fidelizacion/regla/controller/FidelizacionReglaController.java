package com.sistemabarberia.fadex_backend.modules.fidelizacion.regla.controller;

import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.regla.dto.FidelizacionReglaFiltro;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.regla.dto.request.FidelizacionReglaPatchRequestDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.regla.dto.request.FidelizacionReglaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.regla.dto.response.FidelizacionReglaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.regla.service.IFidelizacionReglaService;
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
@RequestMapping("/api/v1/fidelizacion/reglas")
public class FidelizacionReglaController {

    private final IFidelizacionReglaService reglaService;

    @GetMapping
    @PreAuthorize("hasAuthority('FIDELIZACION_READ')")
    public ResponseEntity<ApiResponse<PageResponse<FidelizacionReglaResponseDTO>>> listar(@Valid @ModelAttribute FidelizacionReglaFiltro filtro, @PageableDefault(size = 10, page = 0) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok("Reglas obtenidas correctamente.", reglaService.listarReglaConFiltros(filtro, pageable)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('FIDELIZACION_READ')")
    public ResponseEntity<ApiResponse<FidelizacionReglaResponseDTO>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Regla obtenida correctamente.", reglaService.obtenerReglaPorId(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('FIDELIZACION_MANAGE')")
    public ResponseEntity<ApiResponse<FidelizacionReglaResponseDTO>> crear(@RequestBody @Valid FidelizacionReglaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Regla creada correctamente.", reglaService.crearRegla(dto)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('FIDELIZACION_MANAGE')")
    public ResponseEntity<ApiResponse<FidelizacionReglaResponseDTO>> actualizar(@PathVariable Long id, @RequestBody @Valid FidelizacionReglaRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Regla actualizada correctamente.", reglaService.actualizarRegla(id, dto)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('FIDELIZACION_MANAGE')")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        reglaService.eliminarRegla(id);
        return ResponseEntity.ok(ApiResponse.ok("Regla eliminada correctamente."));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('FIDELIZACION_MANAGE')")
    public ResponseEntity<ApiResponse<FidelizacionReglaResponseDTO>> actualizarParcial(@PathVariable Long id, @RequestBody @Valid FidelizacionReglaPatchRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Regla actualizada correctamente.", reglaService.actualizarParcial(id, dto)));
    }
}