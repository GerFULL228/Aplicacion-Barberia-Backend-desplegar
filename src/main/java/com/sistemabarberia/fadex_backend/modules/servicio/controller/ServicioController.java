package com.sistemabarberia.fadex_backend.modules.servicio.controller;


import com.sistemabarberia.fadex_backend.modules.servicio.dto.request.ServicioRequestDTO;

import com.sistemabarberia.fadex_backend.modules.servicio.dto.response.ServicioResponseDTO;


import com.sistemabarberia.fadex_backend.modules.servicio.service.IServicioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/servicio")
@RequiredArgsConstructor
public class ServicioController {

    private final IServicioService corteService;

    @GetMapping
    public ResponseEntity<List<ServicioResponseDTO>> listar() {
        return ResponseEntity.ok(corteService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicioResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(corteService.obtenerPorId(id));
    }

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<ServicioResponseDTO>> listarPorCategoria(@PathVariable Long categoriaId) {
        return ResponseEntity.ok(corteService.listarPorCategoria(categoriaId));
    }

    @PreAuthorize("hasAuthority('SERVICIO_CREATE')")
    @PostMapping
    public ResponseEntity<ServicioResponseDTO> crear(@Valid @RequestBody ServicioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(corteService.crear(dto));
    }

    @PreAuthorize("hasAuthority('SERVICIO_UPDATE_ALL')")
    @PutMapping("/{id}")
    public ResponseEntity<ServicioResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ServicioRequestDTO dto) {
        return ResponseEntity.ok(corteService.actualizar(id, dto));
    }

    @PreAuthorize("hasAuthority('SERVICIO_DELETE_ALL')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        corteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}