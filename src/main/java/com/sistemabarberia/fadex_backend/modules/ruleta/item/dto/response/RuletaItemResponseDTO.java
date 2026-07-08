package com.sistemabarberia.fadex_backend.modules.ruleta.item.dto.response;

import com.sistemabarberia.fadex_backend.modules.ruleta.item.entity.enums.TipoPremio;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuletaItemResponseDTO {
    private Long itemId;
    private Long ruletaId;
    private String nombre;
    private String descripcion;
    private TipoPremio tipoPremio;
    private BigDecimal valor;
    private BigDecimal probabilidad;
    private Boolean esPremioMayor;
    private Integer stock;
    private Integer cantidadProducto;
    private Integer ordenDisplay;
    private String imagenUrl;
    private Long productoId;
    private Long servicioId;
    private Boolean activo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}