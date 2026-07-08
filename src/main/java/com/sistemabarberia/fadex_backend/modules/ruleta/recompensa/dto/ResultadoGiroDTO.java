package com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.dto;

import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.dto.response.RecompensaObtenidaResponseDTO;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoGiroDTO {
    private Double angulo;
    private RecompensaObtenidaResponseDTO recompensa;
}