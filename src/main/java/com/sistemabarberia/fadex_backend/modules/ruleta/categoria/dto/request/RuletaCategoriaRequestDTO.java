package com.sistemabarberia.fadex_backend.modules.ruleta.categoria.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RuletaCategoriaRequestDTO {

    @NotNull
    private Long idRuleta;

    @NotNull
    private Long idCategoria;
}