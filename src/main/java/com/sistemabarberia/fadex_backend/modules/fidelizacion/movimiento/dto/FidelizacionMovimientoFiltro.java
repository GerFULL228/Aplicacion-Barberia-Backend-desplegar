package com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.dto;

import com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.entity.enums.OrigenFidelizacion;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class FidelizacionMovimientoFiltro {
    private Long clienteId;
    private Long tarjetaId;
    private OrigenFidelizacion origen;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
}