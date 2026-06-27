package com.sistemabarberia.fadex_backend.modules.fidelizacion.regla.dto;

import com.sistemabarberia.fadex_backend.modules.fidelizacion.regla.entity.enums.TipoAlcanceFidelizacion;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FidelizacionReglaFiltro {
    private Long categoriaId;
    private TipoAlcanceFidelizacion tipoAlcance;
    private Boolean activo;
}