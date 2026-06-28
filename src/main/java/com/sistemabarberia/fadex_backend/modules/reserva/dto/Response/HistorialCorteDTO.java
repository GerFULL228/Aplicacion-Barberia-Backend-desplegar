package com.sistemabarberia.fadex_backend.modules.reserva.dto.Response;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.EstadoReserva;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HistorialCorteDTO {
    private Long reservaId;
    private String clienteNombre;
    private String clienteApellido;
    private String servicioNombre;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Integer duracion;
    private BigDecimal precio;
    private EstadoReserva estado;

}
