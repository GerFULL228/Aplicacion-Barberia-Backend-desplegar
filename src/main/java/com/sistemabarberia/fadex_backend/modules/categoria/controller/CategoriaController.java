package com.sistemabarberia.fadex_backend.modules.categoria.controller;

import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import com.sistemabarberia.fadex_backend.modules.categoria.dto.CategoriaFiltro;
import com.sistemabarberia.fadex_backend.modules.categoria.dto.request.CategoriaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.categoria.dto.response.CategoriaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.categoria.service.ICategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final ICategoriaService categoriaService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoriaResponseDTO>>> listar(@ModelAttribute CategoriaFiltro filtro) {
        List<CategoriaResponseDTO> data = categoriaService.listarConFiltro(filtro);
        return ResponseEntity.ok(ApiResponse.ok("Categorías filtradas correctamente", data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoriaResponseDTO>> obtenerPorId(@PathVariable Long id) {
        CategoriaResponseDTO data = categoriaService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.ok("Categoría encontrada", data));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoriaResponseDTO>> crear(@Valid @RequestBody CategoriaRequestDTO dto) {
        CategoriaResponseDTO data = categoriaService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Categoría creada correctamente", data));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoriaResponseDTO>> actualizar(@PathVariable Long id, @Valid @RequestBody CategoriaRequestDTO dto) {
        CategoriaResponseDTO data = categoriaService.actualizar(id, dto);
        return ResponseEntity.ok(ApiResponse.ok("Categoría actualizada correctamente", data));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<ApiResponse<CategoriaResponseDTO>> cambiarEstado(@PathVariable Long id, @RequestParam Boolean estado) {
        CategoriaResponseDTO data = categoriaService.cambiarEstado(id, estado);
        return ResponseEntity.ok(ApiResponse.ok("Estado actualizado correctamente", data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        categoriaService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.ok("Categoría eliminada correctamente"));
    }
}