package com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.dto.response;

import com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.entity.enums.TipoRuleta;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuletaResponseDTO {
    private Long ruletaId;
    private String nombre;
    private String descripcion;
    private TipoRuleta tipo;
    private Boolean activa;
    private BigDecimal incrementoPorGiro;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}