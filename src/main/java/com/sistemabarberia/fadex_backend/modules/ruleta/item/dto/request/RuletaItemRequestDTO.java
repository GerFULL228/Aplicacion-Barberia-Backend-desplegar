package com.sistemabarberia.fadex_backend.modules.ruleta.item.dto.request;

import com.sistemabarberia.fadex_backend.modules.ruleta.item.entity.enums.TipoPremio;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuletaItemRequestDTO {

    @NotNull
    private Long ruletaId;

    @NotBlank
    private String nombre;

    private String descripcion;

    @NotNull
    private TipoPremio tipoPremio;

    private BigDecimal valor;

    @NotNull
    @DecimalMin("0.001")
    private BigDecimal probabilidad;

    @NotNull
    private Boolean esPremioMayor;

    private Integer stock;
    private Integer ordenDisplay;
    private String imagenUrl;
    private Long productoId;
    private Long servicioId;
    private Integer cantidadProducto;
    @NotNull
    private Boolean activo;

}