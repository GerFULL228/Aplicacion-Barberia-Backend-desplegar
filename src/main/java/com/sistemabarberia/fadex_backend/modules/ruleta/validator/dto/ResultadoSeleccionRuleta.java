package com.sistemabarberia.fadex_backend.modules.ruleta.validator.dto;

import com.sistemabarberia.fadex_backend.modules.ruleta.item.entity.RuletaItem;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResultadoSeleccionRuleta {
    private final double angulo;
    private final RuletaItem item;
}