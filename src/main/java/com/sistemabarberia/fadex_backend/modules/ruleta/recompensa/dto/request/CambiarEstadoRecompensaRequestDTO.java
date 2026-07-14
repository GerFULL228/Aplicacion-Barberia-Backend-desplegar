package com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.dto.request;

import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.entity.enums.EstadoRecompensa;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CambiarEstadoRecompensaRequestDTO {

    @NotNull(message = "El nuevo estado es obligatorio")
    private EstadoRecompensa estado;

    private String observacion;
}