package com.sistemabarberia.fadex_backend.modules.ruleta.categoria.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RuletaCategoriaResponseDTO {
    private Long id;
    private Long idRuleta;
    private String nombreRuleta;
    private Long idCategoria;
    private String nombreCategoria;
}