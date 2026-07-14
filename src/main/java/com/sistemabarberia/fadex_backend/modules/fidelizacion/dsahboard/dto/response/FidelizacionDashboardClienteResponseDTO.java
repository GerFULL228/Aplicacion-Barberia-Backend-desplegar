package com.sistemabarberia.fadex_backend.modules.fidelizacion.dsahboard.dto.response;

import com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.dto.response.FidelizacionMovimientoResponseDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.dto.response.FidelizacionTarjetaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.giro.dto.response.RuletaGiroResponseDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.dto.response.RecompensaObtenidaResponseDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FidelizacionDashboardClienteResponseDTO {
    private Integer totalTarjetas;
    private Integer girosDisponibles;
    private Integer tarjetasConGiroDisponible;
    private Integer recompensasPendientes;
    private List<FidelizacionTarjetaResponseDTO> tarjetas;
    private List<FidelizacionMovimientoResponseDTO> movimientosRecientes;
    private List<RuletaGiroResponseDTO> ultimosGiros;
    private List<RecompensaObtenidaResponseDTO> recompensas;
}