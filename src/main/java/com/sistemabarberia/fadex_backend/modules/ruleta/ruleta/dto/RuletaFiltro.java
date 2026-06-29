package com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.dto;

import com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.entity.enums.TipoRuleta;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuletaFiltro {
    private String nombre;
    private TipoRuleta tipo;
    private Boolean activa;
}