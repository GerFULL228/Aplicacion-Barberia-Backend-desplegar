package com.sistemabarberia.fadex_backend.modules.ruleta.categoria.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RuletaCategoriaFiltro {
    private Long idRuleta;
    private Long idCategoria;
}