package com.sistemabarberia.fadex_backend.modules.servicio.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CorteResponseDTO {

    private Long corteId;
    private String nombre;
    private BigDecimal precio;
    private Long categoriaId;
    private String categoriaNombre;
}