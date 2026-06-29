package com.sistemabarberia.fadex_backend.modules.ruleta.item.dto;

import com.sistemabarberia.fadex_backend.modules.ruleta.item.entity.enums.TipoPremio;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuletaItemFiltro {
    private Long ruletaId;
    private String nombre;
    private TipoPremio tipoPremio;
    private Boolean activo;
}