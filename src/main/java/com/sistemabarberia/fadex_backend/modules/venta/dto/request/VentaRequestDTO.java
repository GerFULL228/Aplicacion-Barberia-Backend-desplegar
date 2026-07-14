package com.sistemabarberia.fadex_backend.modules.venta.dto.request;

import com.sistemabarberia.fadex_backend.modules.pagos.entity.MetodoPago;
import com.sistemabarberia.fadex_backend.modules.venta.entity.TipoComprobante;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VentaRequestDTO {

    private Integer clienteId;
    private Integer barberoId;
    private LocalDateTime fecha;

    @NotNull(message = "Tipo comprobante obligatorio")
    private TipoComprobante tipoComprobante;

    @NotNull(message = "Método de pago obligatorio")
    private MetodoPago metodoPago;

    private List<DetalleVentaRequestDTO> detalles;

    private List<Long> recompensasAplicadas;

    private Long reservaId;
}