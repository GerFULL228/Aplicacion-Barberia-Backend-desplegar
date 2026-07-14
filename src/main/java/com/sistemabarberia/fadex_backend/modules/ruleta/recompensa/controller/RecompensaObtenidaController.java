package com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.controller;
import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.entity.FidelizacionTarjeta;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.service.IFidelizacionTarjetaService;
import com.sistemabarberia.fadex_backend.modules.ruleta.engine.service.IRuletaEngineService;
import com.sistemabarberia.fadex_backend.modules.ruleta.item.dto.RuletaItemFiltro;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.dto.RecompensaObtenidaFiltro;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.dto.request.CambiarEstadoRecompensaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.dto.request.CanjearRecompensaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.dto.request.RecompensaObtenidaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.dto.response.RecompensaObtenidaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.dto.response.ResultadoGiroResponseDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.entity.RecompensaObtenida;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.mapper.RecompensaObtenidaMapper;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.service.IRecompensaObtenidaService;
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
@RequiredArgsConstructor
@RequestMapping("/api/v1/recompensas/obtenidas")
public class RecompensaObtenidaController {

    private final IRecompensaObtenidaService recompensaObtenidaService;
    private final IRuletaEngineService ruletaEngineService;
    private final RecompensaObtenidaMapper recompensaObtenidaMapper;
    private final IFidelizacionTarjetaService tarjetaService;

    @GetMapping
    @PreAuthorize("hasAuthority('RECOMPENSA_READ')")
    public ResponseEntity<ApiResponse<PageResponse<RecompensaObtenidaResponseDTO>>> listar (@Valid @ModelAttribute RecompensaObtenidaFiltro filtro, @PageableDefault(size = 10, page = 0) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok("Recompensas obtenidas correctamente.", recompensaObtenidaService.listarRecompensaConFiltro(filtro, pageable)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('RECOMPENSA_READ')")
    public ResponseEntity<ApiResponse<RecompensaObtenidaResponseDTO>> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Recompensa obtenida correctamente.", recompensaObtenidaService.obtenerRecompensaPorId(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('RECOMPENSA_CANJEAR')")
    public ResponseEntity<ApiResponse<RecompensaObtenidaResponseDTO>> crear(@Valid @RequestBody RecompensaObtenidaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Recompensa creada correctamente.", recompensaObtenidaService.crearRecompensa(dto)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('RECOMPENSA_CANJEAR')")
    public ResponseEntity<ApiResponse<RecompensaObtenidaResponseDTO>> actualizar(@PathVariable Long id, @Valid @RequestBody RecompensaObtenidaRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Recompensa actualizada correctamente.", recompensaObtenidaService.actualizarRecompensa(id, dto)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('RECOMPENSA_CANJEAR')")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        recompensaObtenidaService.eliminarRecompensa(id);return ResponseEntity.ok(ApiResponse.ok("Recompensa eliminada correctamente."));
    }

    @GetMapping("/mis-recompensas")
    @PreAuthorize("hasAuthority('FIDELIZACION_READ')")
    public ResponseEntity<ApiResponse<List<RecompensaObtenidaResponseDTO>>> obtenerMisRecompensas() {
        return ResponseEntity.ok(ApiResponse.ok("Recompensas obtenidas correctamente.", recompensaObtenidaService.obtenerMisRecompensas()));
    }

    @GetMapping("/mis-recompensas/{id}")
    @PreAuthorize("hasAuthority('FIDELIZACION_READ')")
    public ResponseEntity<ApiResponse<RecompensaObtenidaResponseDTO>> obtenerMiRecompensa(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Recompensa obtenida correctamente.", recompensaObtenidaService.obtenerMiRecompensa(id)));
    }

    @PatchMapping("/canjear")
    @PreAuthorize("hasAuthority('RECOMPENSA_CANJEAR')")
    public ResponseEntity<ApiResponse<RecompensaObtenidaResponseDTO>> canjear(@Valid @RequestBody CanjearRecompensaRequestDTO request) {
        return ResponseEntity.ok(ApiResponse.ok("Recompensa canjeada",recompensaObtenidaService.canjearRecompensa(request.getCodigoCanje())));
    }

    @PostMapping("/girar")
    @PreAuthorize("hasAuthority('FIDELIZACION_READ')")
    public ResponseEntity<ApiResponse<RecompensaObtenidaResponseDTO>> girar() {
        FidelizacionTarjeta tarjeta = tarjetaService.obtenerTarjetaConGiroDisponible();
        RecompensaObtenida recompensa = ruletaEngineService.ejecutarGiro(tarjeta);
        return ResponseEntity.ok(ApiResponse.ok("Giro realizado correctamente.", recompensaObtenidaMapper.toResponse(recompensa)));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAuthority('RECOMPENSA_CANJEAR')")
    public ResponseEntity<ApiResponse<RecompensaObtenidaResponseDTO>> cambiarEstado(@PathVariable Long id, @Valid @RequestBody CambiarEstadoRecompensaRequestDTO request) {
        return ResponseEntity.ok(ApiResponse.ok("Estado actualizado correctamente.", recompensaObtenidaService.cambiarEstado(id, request.getEstado(), request.getObservacion())));
    }
}