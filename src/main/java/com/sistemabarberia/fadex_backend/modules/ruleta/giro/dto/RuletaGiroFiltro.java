package com.sistemabarberia.fadex_backend.modules.ruleta.giro.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class RuletaGiroFiltro {
    private Long clienteId;
    private Long tarjetaId;
    private Long ruletaId;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
}