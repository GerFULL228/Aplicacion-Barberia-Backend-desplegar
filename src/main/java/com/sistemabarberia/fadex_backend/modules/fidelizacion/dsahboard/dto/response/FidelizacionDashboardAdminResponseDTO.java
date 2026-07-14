package com.sistemabarberia.fadex_backend.modules.fidelizacion.dsahboard.dto.response;

import com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.dto.response.FidelizacionMovimientoResponseDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.dto.response.TarjetasPorCategoriaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.dto.response.RecompensaObtenidaResponseDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FidelizacionDashboardAdminResponseDTO {
    private Integer totalTarjetas;
    private Integer totalConfiguraciones;
    private Integer totalGiros;
    private Integer totalRecompensas;
    private List<FidelizacionMovimientoResponseDTO> movimientosRecientes;
    private List<RecompensaObtenidaResponseDTO> ultimasRecompensas;
    private List<TarjetasPorCategoriaResponseDTO> tarjetasPorCategoria;
}