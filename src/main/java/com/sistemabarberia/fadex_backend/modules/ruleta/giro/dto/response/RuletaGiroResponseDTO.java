package com.sistemabarberia.fadex_backend.modules.ruleta.giro.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class RuletaGiroResponseDTO {
    private Long id;
    private Long tarjetaId;
    private Long clienteId;
    private String clienteNombre;
    private Long ruletaId;
    private String ruletaNombre;
    private Long itemId;
    private String premio;
    private Integer numeroGiro;
    private BigDecimal probFinal;
    private BigDecimal probAplicada;
    private LocalDateTime fecha;
}