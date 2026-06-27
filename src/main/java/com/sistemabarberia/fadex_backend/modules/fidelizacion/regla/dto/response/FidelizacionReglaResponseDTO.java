package com.sistemabarberia.fadex_backend.modules.fidelizacion.regla.dto.response;

import com.sistemabarberia.fadex_backend.modules.fidelizacion.regla.entity.enums.TipoAlcanceFidelizacion;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FidelizacionReglaResponseDTO {
    private Long reglaId;
    private Long categoriaId;
    private TipoAlcanceFidelizacion tipoAlcance;
    private Long servicioId;
    private Long productoId;
    private Integer puntos;
    private Boolean activo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}