package com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ResultadoGiroResponseDTO {
    private Double angulo;
    private RecompensaObtenidaResponseDTO recompensa;
}