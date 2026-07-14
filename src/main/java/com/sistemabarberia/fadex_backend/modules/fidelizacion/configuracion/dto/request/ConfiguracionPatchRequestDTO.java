package com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfiguracionPatchRequestDTO {
    @NotBlank
    private String campo;
    @NotNull
    private Boolean valor;
}