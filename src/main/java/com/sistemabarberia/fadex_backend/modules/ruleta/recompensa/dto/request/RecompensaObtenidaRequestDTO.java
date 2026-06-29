package com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.dto.request;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Getter
@Setter
public class RecompensaObtenidaRequestDTO {
    @NotNull
    private Long giroId;

    @NotNull
    private Long clienteId;

    @NotNull
    private Long itemId;

    private String observacion;
    private Long usuarioCanjeId;
    private LocalDateTime fechaVencimiento;
    private String codigoCanje;
}
