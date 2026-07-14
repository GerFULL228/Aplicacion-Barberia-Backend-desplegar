package com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TarjetasPorCategoriaResponseDTO {
    private Long categoriaId;
    private String categoriaNombre;
    private Integer totalTarjetas;
    private Integer tarjetasConGiroDisponible;
    private Integer girosDisponibles;
}