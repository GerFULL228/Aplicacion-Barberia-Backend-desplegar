package com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracionResponseDTO {
    private Long configuracionId;
    private Long categoriaId;
    private String categoriaNombre;
    private Boolean activa;
    private Integer meta;
    private Boolean mostrarSiempre;
    private Boolean crearTarjetaAutomatica;
    private Long ruletaId;
    private String ruletaNombre;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}