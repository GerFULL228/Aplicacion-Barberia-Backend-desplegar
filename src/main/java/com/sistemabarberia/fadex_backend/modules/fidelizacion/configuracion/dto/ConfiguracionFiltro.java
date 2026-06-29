package com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracionFiltro {
    private Boolean activa;
    private Long categoriaId;
    private Long ruletaId;
    private String categoriaNombre;
    private String ruletaNombre;
    private Integer metaDesde;
    private Integer metaHasta;
}
