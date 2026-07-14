package com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FidelizacionTarjetaPatchRequestDTO {
    @NotBlank
    private String campo;

    @NotNull
    private Object valor;
}