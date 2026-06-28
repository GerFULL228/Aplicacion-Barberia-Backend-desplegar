package com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.dto.response;


import com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.entity.enums.OrigenFidelizacion;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class FidelizacionMovimientoResponseDTO {
    private Long id;
    private Long tarjetaId;
    private Long clienteId;
    private String clienteNombre;
    private OrigenFidelizacion origen;
    private Long idOrigen;
    private Integer puntos;
    private String descripcion;
    private LocalDateTime createdAt;
}