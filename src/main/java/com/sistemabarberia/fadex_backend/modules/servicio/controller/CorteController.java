package com.sistemabarberia.fadex_backend.modules.servicio.controller;

import com.sistemabarberia.fadex_backend.modules.servicio.dto.request.CorteRequestDTO;
import com.sistemabarberia.fadex_backend.modules.servicio.dto.response.CorteResponseDTO;
import com.sistemabarberia.fadex_backend.modules.servicio.service.ICorteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cortes")
@RequiredArgsConstructor
public class CorteController {

    private final ICorteService corteService;

    @GetMapping
    public ResponseEntity<List<CorteResponseDTO>> listar() {
        return ResponseEntity.ok(corteService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CorteResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(corteService.obtenerPorId(id));
    }

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<CorteResponseDTO>> listarPorCategoria(@PathVariable Long categoriaId) {
        return ResponseEntity.ok(corteService.listarPorCategoria(categoriaId));
    }

    @PostMapping
    public ResponseEntity<CorteResponseDTO> crear(@Valid @RequestBody CorteRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(corteService.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CorteResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CorteRequestDTO dto) {
        return ResponseEntity.ok(corteService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        corteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}