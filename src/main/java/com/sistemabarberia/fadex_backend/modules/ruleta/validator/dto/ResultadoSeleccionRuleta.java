package com.sistemabarberia.fadex_backend.modules.ruleta.validator.dto;

import com.sistemabarberia.fadex_backend.modules.ruleta.item.entity.RuletaItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class ResultadoSeleccionRuleta {
    private final BigDecimal angulo;
    private final RuletaItem item;
    private final Integer numeroGiro;
    private final BigDecimal probabilidadFinal;
    private final BigDecimal probabilidadAplicada;
}