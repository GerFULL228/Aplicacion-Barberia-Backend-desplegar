package com.sistemabarberia.fadex_backend.modules.ruleta.categoria.controller;

import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.categoria.dto.CategoriaFiltro;
import com.sistemabarberia.fadex_backend.modules.ruleta.categoria.dto.RuletaCategoriaFiltro;
import com.sistemabarberia.fadex_backend.modules.ruleta.categoria.dto.request.RuletaCategoriaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.categoria.dto.response.RuletaCategoriaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.categoria.service.IRuletaCategoriaService;
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
@RequestMapping("/api/v1/ruleta-categorias")
public class RuletaCategoriaController {

    private final IRuletaCategoriaService ruletaCategoriaService;

    @GetMapping
    @PreAuthorize("hasAuthority('RULETA_MANAGE')")
    public ResponseEntity<ApiResponse<PageResponse<RuletaCategoriaResponseDTO>>> listar(@Valid @ModelAttribute RuletaCategoriaFiltro  filtro, @PageableDefault(size = 10, page = 0) Pageable pageable){
        return ResponseEntity.ok(ApiResponse.ok("Listado obtenido correctamente.", ruletaCategoriaService.listarCategoriaConFiltros(filtro,pageable)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('RULETA_MANAGE')")
    public ResponseEntity<ApiResponse<RuletaCategoriaResponseDTO>> obtenerPorId(@PathVariable Long id){
        return ResponseEntity.ok(ApiResponse.ok("Relación obtenida correctamente.", ruletaCategoriaService.obtenerCategoriaPorId(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('RULETA_MANAGE')")
    public ResponseEntity<ApiResponse<RuletaCategoriaResponseDTO>> crear(@Valid @RequestBody RuletaCategoriaRequestDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Relación creada correctamente.", ruletaCategoriaService.crearCategoria(dto)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('RULETA_MANAGE')")
    public ResponseEntity<ApiResponse<RuletaCategoriaResponseDTO>> actualizar(@PathVariable Long id, @Valid @RequestBody RuletaCategoriaRequestDTO dto){
        return ResponseEntity.ok(ApiResponse.ok("Relación actualizada correctamente.", ruletaCategoriaService.actualizarCategoria(id,dto)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('RULETA_MANAGE')")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id){
        ruletaCategoriaService.eliminarCategoria(id);
        return ResponseEntity.ok(ApiResponse.ok("Relación eliminada correctamente."));
    }
}