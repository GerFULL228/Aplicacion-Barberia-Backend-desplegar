package com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FidelizacionTarjetaFiltro {
    private Long clienteId;
    private Long categoriaId;
    private Boolean cicloActivo;
}