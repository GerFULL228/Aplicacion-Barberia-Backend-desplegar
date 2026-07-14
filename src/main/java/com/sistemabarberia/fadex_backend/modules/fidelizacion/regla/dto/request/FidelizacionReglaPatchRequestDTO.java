package com.sistemabarberia.fadex_backend.modules.fidelizacion.regla.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FidelizacionReglaPatchRequestDTO {
    @NotBlank
    private String campo;

    @NotNull
    private Object valor;
}