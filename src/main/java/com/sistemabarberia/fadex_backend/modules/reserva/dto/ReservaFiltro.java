package com.sistemabarberia.fadex_backend.modules.reserva.dto;

import com.sistemabarberia.fadex_backend.modules.reserva.entity.EstadoReserva;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.TipoReserva;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReservaFiltro {
    private Integer clienteId;
    private Integer barberoId;
    private Long servicioId;
    private String clienteNombre;
    private String barberoNombre;
    private EstadoReserva estadoReserva;
    private TipoReserva tipoReserva;
    private LocalDate fecha;
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
}