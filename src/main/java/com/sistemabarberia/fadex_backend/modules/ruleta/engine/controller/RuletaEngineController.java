package com.sistemabarberia.fadex_backend.modules.ruleta.engine.controller;


import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.entity.FidelizacionTarjeta;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.service.IFidelizacionTarjetaService;
import com.sistemabarberia.fadex_backend.modules.ruleta.engine.service.IRuletaEngineService;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.dto.response.RecompensaObtenidaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.entity.RecompensaObtenida;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.mapper.RecompensaObtenidaMapper;
import com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.dto.response.RuletaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.entity.Ruleta;
import com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.mapper.RuletaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ruleta")
@RequiredArgsConstructor
public class RuletaEngineController {

    private final IRuletaEngineService  ruletaEngineService;;
    private final IFidelizacionTarjetaService tarjetaService;
    private final RuletaMapper ruletaMapper;
    private final RecompensaObtenidaMapper recompensaMapper;

    @GetMapping("/mi-ruleta")
    @PreAuthorize("hasAuthority('FIDELIZACION_READ')")
    public ResponseEntity<ApiResponse<RuletaResponseDTO>> obtenerMiRuleta() {
        FidelizacionTarjeta tarjeta = tarjetaService.obtenerTarjetaConGiroDisponible();
        Ruleta ruleta = ruletaEngineService.obtenerRuletaDisponible(tarjeta);
        return ResponseEntity.ok(ApiResponse.ok("Ruleta obtenida correctamente.", ruletaMapper.toResponse(ruleta)));
    }

    @PostMapping("/girar")
    @PreAuthorize("hasAuthority('FIDELIZACION_READ')")
    public ResponseEntity<ApiResponse<RecompensaObtenidaResponseDTO>> girar() {
        FidelizacionTarjeta tarjeta = tarjetaService.obtenerTarjetaConGiroDisponible();
        RecompensaObtenida recompensa = ruletaEngineService.ejecutarGiro(tarjeta);
        return ResponseEntity.ok(ApiResponse.ok("Giro realizado correctamente.", recompensaMapper.toResponse(recompensa))
        );
    }

    @GetMapping("/mi-ruleta/{tarjetaId}")
    public ResponseEntity<ApiResponse<RuletaResponseDTO>> obtenerMiRuleta(@PathVariable Long tarjetaId) {
        FidelizacionTarjeta tarjeta = tarjetaService.obtenerMiTarjetaConGiro(tarjetaId);
        Ruleta ruleta = ruletaEngineService.obtenerRuletaDisponible(tarjeta);
        return ResponseEntity.ok(ApiResponse.ok("Ruleta obtenida correctamente.", ruletaMapper.toResponse(ruleta)));
    }

    @PostMapping("/girar/{tarjetaId}")
    @PreAuthorize("hasAuthority('FIDELIZACION_READ')")
    public ResponseEntity<ApiResponse<RecompensaObtenidaResponseDTO>> girar(@PathVariable Long tarjetaId) {
        FidelizacionTarjeta tarjeta = tarjetaService.obtenerMiTarjetaConGiro(tarjetaId);
        RecompensaObtenida recompensa = ruletaEngineService.ejecutarGiro(tarjeta);
        return ResponseEntity.ok(ApiResponse.ok("Giro realizado correctamente.", recompensaMapper.toResponse(recompensa)));
    }
}
