package com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.dto.request;

import com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.entity.enums.OrigenFidelizacion;

import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FidelizacionMovimientoRequestDTO {

    @NotNull
    private Long tarjetaId;

    @NotNull
    private Long clienteId;

    @NotNull
    private OrigenFidelizacion origen;

    @NotNull
    private Long idOrigen;

    @NotNull
    private Integer puntos;

    private String descripcion;

}