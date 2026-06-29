package com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FidelizacionTarjetaResponseDTO {
    private Long id;
    private Long clienteId;
    private String clienteNombre;
    private Long categoriaId;
    private String categoriaNombre;
    private Integer progreso;
    private Integer girosDisponibles;
    private Integer totalGiros;
    private Boolean cicloActivo;
}