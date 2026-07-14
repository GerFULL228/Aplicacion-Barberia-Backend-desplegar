package com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.dto;

import com.sistemabarberia.fadex_backend.modules.ruleta.item.entity.RuletaItem;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoSeleccionRuleta {
    private RuletaItem item;
    private BigDecimal angulo;
    private Integer numeroGiro;
    private BigDecimal probabilidadFinal;
    private BigDecimal probabilidadAplicada;
}