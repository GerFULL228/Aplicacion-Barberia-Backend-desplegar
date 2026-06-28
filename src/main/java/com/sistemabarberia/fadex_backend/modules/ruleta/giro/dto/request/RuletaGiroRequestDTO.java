package com.sistemabarberia.fadex_backend.modules.ruleta.giro.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RuletaGiroRequestDTO {

    @NotNull
    private Long tarjetaId;

    @NotNull
    private Long clienteId;

    @NotNull
    private Long ruletaId;

    @NotNull
    private Long itemId;

    @NotNull
    private Integer numeroGiro;

    private BigDecimal probFinal;
    private BigDecimal probAplicada;

}