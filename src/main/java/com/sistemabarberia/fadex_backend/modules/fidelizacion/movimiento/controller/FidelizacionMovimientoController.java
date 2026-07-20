package com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.controller;

import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.dto.FidelizacionMovimientoFiltro;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.dto.request.FidelizacionMovimientoRequestDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.dto.response.FidelizacionMovimientoResponseDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.service.IFidelizacionMovimientoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/fidelizacion/movimientos")
public class FidelizacionMovimientoController {

    private final IFidelizacionMovimientoService movimientoService;

    @GetMapping
    @PreAuthorize("hasAuthority('FIDELIZACION_READ')")
    public ResponseEntity<ApiResponse<PageResponse<FidelizacionMovimientoResponseDTO>>> listar(@Valid @ModelAttribute FidelizacionMovimientoFiltro filtro, @PageableDefault(size = 10, page = 0) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok("Movimientos obtenidos correctamente.", movimientoService.listarMovimientos(filtro, pageable)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('FIDELIZACION_READ')")
    public ResponseEntity<ApiResponse<FidelizacionMovimientoResponseDTO>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Movimiento obtenido correctamente.", movimientoService.obtenerMovimientoPorId(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('FIDELIZACION_MANAGE')")
    public ResponseEntity<ApiResponse<FidelizacionMovimientoResponseDTO>> crear(@Valid @RequestBody FidelizacionMovimientoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Movimiento creado correctamente.", movimientoService.crearMovimiento(dto)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('FIDELIZACION_MANAGE')")
    public ResponseEntity<ApiResponse<FidelizacionMovimientoResponseDTO>> actualizar(@PathVariable Long id, @Valid @RequestBody FidelizacionMovimientoRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Movimiento actualizado correctamente.", movimientoService.actualizarMovimiento(id, dto)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('FIDELIZACION_MANAGE')")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        movimientoService.eliminarMovimiento(id);
        return ResponseEntity.ok(ApiResponse.ok("Movimiento eliminado correctamente."));
    }

    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("hasAuthority('FIDELIZACION_READ')")
    public ResponseEntity<ApiResponse<List<FidelizacionMovimientoResponseDTO>>> listarPorCliente(@PathVariable Integer clienteId) {
        return ResponseEntity.ok(ApiResponse.ok("Movimientos obtenidos correctamente.", movimientoService.listarPorCliente(clienteId)));
    }

    @GetMapping("/mis-movimientos")
    @PreAuthorize("hasAuthority('FIDELIZACION_READ')")
    public ResponseEntity<ApiResponse<List<FidelizacionMovimientoResponseDTO>>> obtenerMisMovimientos(){
        return ResponseEntity.ok(ApiResponse.ok("Mis movimientos obtenidos correctamente.", movimientoService.obtenerMisMovimientos()));
    }

    @GetMapping("/ultimos")
    public ResponseEntity<ApiResponse<List<FidelizacionMovimientoResponseDTO>>> obtenerUltimosMovimientos(@RequestParam(defaultValue = "5") int limite) {
        return ResponseEntity.ok(ApiResponse.success(movimientoService.obtenerUltimosMovimientos(limite)));
    }
}