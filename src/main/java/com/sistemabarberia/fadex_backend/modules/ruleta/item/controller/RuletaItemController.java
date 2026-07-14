package com.sistemabarberia.fadex_backend.modules.ruleta.item.controller;

import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.ruleta.giro.dto.RuletaGiroFiltro;
import com.sistemabarberia.fadex_backend.modules.ruleta.item.dto.RuletaItemFiltro;
import com.sistemabarberia.fadex_backend.modules.ruleta.item.dto.request.RuletaItemRequestDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.item.dto.response.RuletaItemResponseDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.item.service.IRuletaItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ruleta/items")
public class RuletaItemController {

    private final IRuletaItemService ruletaItemService;

    @GetMapping
    @PreAuthorize("hasAuthority('RULETA_READ')")
    public ResponseEntity<ApiResponse<PageResponse<RuletaItemResponseDTO>>> listar(@Valid @ModelAttribute RuletaItemFiltro filtro, @PageableDefault(size = 10, page = 0) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok("Listado obtenido correctamente.", ruletaItemService.listarItemConFiltros(filtro, pageable)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('RULETA_READ')")
    public ResponseEntity<ApiResponse<RuletaItemResponseDTO>> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Item obtenido correctamente.", ruletaItemService.obtenerItemPorId(id)));
    }

    @GetMapping("activo/{id}")
    @PreAuthorize("hasAuthority('RULETA_READ')")
    public ResponseEntity<ApiResponse<PageResponse<RuletaItemResponseDTO>>> listarActivos(@PathVariable Long id, @PageableDefault(size = 10, page = 0) Pageable pageable) {
        RuletaItemFiltro filtro = new RuletaItemFiltro();
        filtro.setRuletaId(id);
        filtro.setActivo(true);
        return ResponseEntity.ok(ApiResponse.ok("Listado de items activos obtenido correctamente.", ruletaItemService.listarItemConFiltros(filtro, pageable)));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('RULETA_MANAGE')")
    public ResponseEntity<ApiResponse<RuletaItemResponseDTO>> crear(@RequestPart("data") @Valid RuletaItemRequestDTO dto, @RequestPart(value = "imagen", required = false) MultipartFile imagen) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Item creado correctamente.", ruletaItemService.crearItem(dto, imagen)));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('RULETA_MANAGE')")
    public ResponseEntity<ApiResponse<RuletaItemResponseDTO>> actualizar(@PathVariable Long id, @RequestPart("data") @Valid RuletaItemRequestDTO dto, @RequestPart(value = "imagen", required = false) MultipartFile imagen) {
        return ResponseEntity.ok(ApiResponse.ok("Item actualizado correctamente.", ruletaItemService.actualizarItem(id, dto, imagen)));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAuthority('RULETA_MANAGE')")
    public ResponseEntity<ApiResponse<RuletaItemResponseDTO>> cambiarEstado(@PathVariable Long id, @RequestParam Boolean activo) {
        RuletaItemResponseDTO data = ruletaItemService.cambiarEstado(id, activo);
        return ResponseEntity.ok(ApiResponse.ok("Estado del item actualizado correctamente.", data));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('RULETA_MANAGE')")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        ruletaItemService.eliminarItem(id);
        return  ResponseEntity.ok(ApiResponse.ok("Item eliminado correctamente." ));
    }
}
