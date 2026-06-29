package com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.controller;

import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.dto.RuletaFiltro;
import com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.dto.request.RuletaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.dto.response.RuletaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.service.IRuletaService;
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
@RequestMapping("/api/v1/ruletas")
@RequiredArgsConstructor
public class RuletaController {

    private final IRuletaService ruletaService;

    @PreAuthorize("hasAuthority('RULETA_READ')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<RuletaResponseDTO>>> listar(@Valid @ModelAttribute RuletaFiltro filtro, @PageableDefault(size = 10, page = 0) Pageable pageable) {
        PageResponse<RuletaResponseDTO> data = ruletaService.listarRuletasConFiltro(filtro, pageable);
        return ResponseEntity.ok(ApiResponse.ok("Ruletas obtenidas correctamente.", data));
    }

    @PreAuthorize("hasAuthority('RULETA_READ')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RuletaResponseDTO>> obtenerPorId(@PathVariable Long id) {
        RuletaResponseDTO data = ruletaService.obtenerRuletaPorId(id);
        return ResponseEntity.ok(ApiResponse.ok("Ruleta encontrada.", data));
    }

    @PreAuthorize("hasAuthority('RULETA_MANAGE')")
    @PostMapping
    public ResponseEntity<ApiResponse<RuletaResponseDTO>> crear(@Valid @RequestBody RuletaRequestDTO dto) {
        RuletaResponseDTO data = ruletaService.crearRuleta(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Ruleta creada correctamente.", data));
    }

    @PreAuthorize("hasAuthority('RULETA_MANAGE')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RuletaResponseDTO>> actualizar(@PathVariable Long id, @Valid @RequestBody RuletaRequestDTO dto) {
        RuletaResponseDTO data = ruletaService.actualizarRuleta(id, dto);
        return ResponseEntity.ok(ApiResponse.ok("Ruleta actualizada correctamente.", data));
    }

    @PreAuthorize("hasAuthority('RULETA_MANAGE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        ruletaService.eliminarRuleta(id);
        return ResponseEntity.ok(ApiResponse.ok("Ruleta eliminada correctamente."));
    }

    @GetMapping("/activas")
    public ResponseEntity<ApiResponse<List<RuletaResponseDTO>>> listarActivas() {
        List<RuletaResponseDTO> data = ruletaService.listarActivas();
        return ResponseEntity.ok(ApiResponse.ok("Ruletas activas.", data));
    }
}