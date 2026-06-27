package com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.controller;

import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.dto.ConfiguracionFiltro;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.dto.request.ConfiguracionRequestDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.dto.response.ConfiguracionResponseDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.service.IFidelizacionConfiguracionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/fidelizacion/configuracion")
@RequiredArgsConstructor
public class FidelizacionConfiguracionController {

    private final IFidelizacionConfiguracionService configuracionService;

    @PreAuthorize("hasAuthority('FIDELIZACION_READ')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ConfiguracionResponseDTO>>> listar(@Valid @ModelAttribute ConfiguracionFiltro filtro, @PageableDefault(size = 10, page = 0) Pageable pageable) {
        PageResponse<ConfiguracionResponseDTO> data = configuracionService.listarConfiguracionConFiltro(filtro, pageable);
        return ResponseEntity.ok(ApiResponse.ok("Configuraciones obtenidas correctamente", data));
    }

    @PreAuthorize("hasAuthority('FIDELIZACION_READ')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ConfiguracionResponseDTO>> obtenerPorId(@PathVariable Long id) {
        ConfiguracionResponseDTO data = configuracionService.obtenerConfiguracionPorId(id);
        return ResponseEntity.ok(ApiResponse.ok("Configuración encontrada", data));
    }

    @PreAuthorize("hasAuthority('FIDELIZACION_MANAGE')")
    @PostMapping
    public ResponseEntity<ApiResponse<ConfiguracionResponseDTO>> crear(@Valid @RequestBody ConfiguracionRequestDTO dto) {
        ConfiguracionResponseDTO data = configuracionService.crearConfiguracion(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Configuración creada correctamente", data));
    }

    @PreAuthorize("hasAuthority('FIDELIZACION_MANAGE')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ConfiguracionResponseDTO>> actualizar(@PathVariable Long id, @Valid @RequestBody ConfiguracionRequestDTO dto) {
        ConfiguracionResponseDTO data = configuracionService.actualizarConfiguracion(id, dto);
        return ResponseEntity.ok(ApiResponse.ok("Configuración actualizada correctamente", data));
    }

    @PreAuthorize("hasAuthority('FIDELIZACION_MANAGE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        configuracionService.eliminarConfiguracion(id);
        return ResponseEntity.ok(ApiResponse.ok("Configuración eliminada correctamente"));
    }
}