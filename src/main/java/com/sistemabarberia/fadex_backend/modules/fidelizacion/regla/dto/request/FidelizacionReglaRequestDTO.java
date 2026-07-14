package com.sistemabarberia.fadex_backend.modules.fidelizacion.regla.dto.request;

import com.sistemabarberia.fadex_backend.modules.fidelizacion.regla.entity.enums.TipoAlcanceFidelizacion;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FidelizacionReglaRequestDTO {
    @NotNull
    private Long categoriaId;

    @NotNull
    private TipoAlcanceFidelizacion tipoAlcance;

    private Long servicioId;
    private Long productoId;

    @NotNull
    @Min(1)
    private Integer puntos;

    @NotNull
    private Boolean activo;
}