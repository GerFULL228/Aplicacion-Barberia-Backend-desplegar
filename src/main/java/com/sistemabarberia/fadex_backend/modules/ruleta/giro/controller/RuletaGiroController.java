package com.sistemabarberia.fadex_backend.modules.ruleta.giro.controller;

import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.ruleta.giro.dto.RuletaGiroFiltro;
import com.sistemabarberia.fadex_backend.modules.ruleta.giro.dto.request.RuletaGiroRequestDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.giro.dto.response.RuletaGiroResponseDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.giro.service.IRuletaGiroService;
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
@RequestMapping("/api/v1/ruleta/giros")
public class RuletaGiroController {

    private final IRuletaGiroService giroService;

    @GetMapping
    @PreAuthorize("hasAuthority('RULETA_READ')")
    public ResponseEntity<ApiResponse<PageResponse<RuletaGiroResponseDTO>>> listar(@Valid @ModelAttribute RuletaGiroFiltro filtro,  @PageableDefault(size = 10, page = 0) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok("Giros obtenidos correctamente.", giroService.listarGiros(filtro, pageable)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('RULETA_READ')")
    public ResponseEntity<ApiResponse<RuletaGiroResponseDTO>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Giro obtenido correctamente.", giroService.obtenerGiroPorId(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('RULETA_MANAGE')")
    public ResponseEntity<ApiResponse<RuletaGiroResponseDTO>> crear(@Valid @RequestBody RuletaGiroRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Giro registrado correctamente.", giroService.crearGiro(dto)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('RULETA_MANAGE')")
    public ResponseEntity<ApiResponse<RuletaGiroResponseDTO>> actualizar(@PathVariable Long id, @Valid @RequestBody RuletaGiroRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Giro actualizado correctamente.", giroService.actualizarGiro(id, dto)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('RULETA_MANAGE')")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        giroService.eliminarGiro(id);
        return ResponseEntity.ok(ApiResponse.ok("Giro eliminado correctamente."));
    }
}