package com.sistemabarberia.fadex_backend.modules.venta.controller;

import com.sistemabarberia.fadex_backend.modules.venta.dto.request.VentaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.venta.dto.response.*;
import com.sistemabarberia.fadex_backend.modules.venta.service.IVentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final IVentaService ventaService;

    // LISTAR VENTAS
    @GetMapping
    public ResponseEntity<List<VentaResponseDTO>> listar() {
        return ResponseEntity.ok(ventaService.listar());
    }

    // OBTENER POR ID
    @GetMapping("/{id}")
    public ResponseEntity<VentaResponseDTO> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(ventaService.obtenerPorId(id));
    }

    // CREAR VENTA
    @PostMapping
    public ResponseEntity<VentaResponseDTO> crear(@Valid @RequestBody VentaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ventaService.crear(dto));
    }

    // ELIMINAR VENTA
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        ventaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // DETALLES DE UNA VENTA
    @GetMapping("/{id}/detalles")
    public ResponseEntity<List<DetalleVentaResponseDTO>> listarDetalles(@PathVariable Integer id) {
        return ResponseEntity.ok(ventaService.listarDetalles(id));
    }

    // HISTORIAL DE UNA VENTA
    @GetMapping("/{id}/historial")
    public ResponseEntity<List<HistorialVentaResponseDTO>> listarHistorial(@PathVariable Integer id) {
        return ResponseEntity.ok(ventaService.listarHistorial(id));
    }
}