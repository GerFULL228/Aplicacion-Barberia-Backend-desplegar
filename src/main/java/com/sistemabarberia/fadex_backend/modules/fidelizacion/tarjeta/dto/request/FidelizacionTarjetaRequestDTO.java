package com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FidelizacionTarjetaRequestDTO {

    @NotNull
    private Long clienteId;

    @NotNull
    private Long categoriaId;
}