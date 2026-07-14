package com.sistemabarberia.fadex_backend.modules.fidelizacion.regla.dto.response;

import com.sistemabarberia.fadex_backend.modules.fidelizacion.regla.entity.enums.TipoAlcanceFidelizacion;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FidelizacionReglaResponseDTO {
    private TipoAlcanceFidelizacion tipoAlcance;
    private Long reglaId;
    private Long categoriaId;
    private String categoriaNombre;
    private Long servicioId;
    private String servicioNombre;
    private Long productoId;
    private String productoNombre;
    private Integer puntos;
    private Boolean activo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}