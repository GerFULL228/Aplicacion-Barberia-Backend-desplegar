package com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.controller;
import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.ruleta.item.dto.RuletaItemFiltro;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.dto.RecompensaObtenidaFiltro;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.dto.request.RecompensaObtenidaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.dto.response.RecompensaObtenidaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.service.IRecompensaObtenidaService;
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
@RequestMapping("/api/v1/recompensas/obtenidas")
public class RecompensaObtenidaController {

    private final IRecompensaObtenidaService recompensaObtenidaService;

    @GetMapping
    @PreAuthorize("hasAuthority('RECOMPENSA_READ')")
    public ResponseEntity<ApiResponse<PageResponse<RecompensaObtenidaResponseDTO>>> listar (@Valid @ModelAttribute RecompensaObtenidaFiltro filtro, @PageableDefault(size = 10, page = 0) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok("Recompensas obtenidas correctamente.", recompensaObtenidaService.listar(filtro, pageable)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('RECOMPENSA_READ')")
    public ResponseEntity<ApiResponse<RecompensaObtenidaResponseDTO>> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Recompensa obtenida correctamente.", recompensaObtenidaService.obtenerPorId(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('RECOMPENSA_CANJEAR')")
    public ResponseEntity<ApiResponse<RecompensaObtenidaResponseDTO>> crear(@Valid @RequestBody RecompensaObtenidaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Recompensa creada correctamente.", recompensaObtenidaService.crear(dto)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('RECOMPENSA_CANJEAR')")
    public ResponseEntity<ApiResponse<RecompensaObtenidaResponseDTO>> actualizar(@PathVariable Long id, @Valid @RequestBody RecompensaObtenidaRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Recompensa actualizada correctamente.", recompensaObtenidaService.actualizar(id, dto)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('RECOMPENSA_CANJEAR')")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        recompensaObtenidaService.eliminar(id);return ResponseEntity.ok(ApiResponse.ok("Recompensa eliminada correctamente."));
    }
}