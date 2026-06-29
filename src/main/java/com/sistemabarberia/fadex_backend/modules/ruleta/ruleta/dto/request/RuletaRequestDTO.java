package com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.dto.request;

import com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.entity.enums.TipoRuleta;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuletaRequestDTO {

    @NotBlank
    @Size(max = 100)
    private String nombre;

    private String descripcion;

    @NotNull
    private TipoRuleta tipo;

    @NotNull
    private Boolean activa;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal incrementoPorGiro;
}