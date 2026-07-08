package com.sistemabarberia.fadex_backend.modules.reserva.dto.Request;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record ReservaRequest(
        Integer clienteId,
        Integer barberoId,
        Long servicioId,
        LocalDate fecha,
        LocalTime horaInicio,
        String observacion,
        boolean esGratis,
        List<Long>recompensasAplicadas
) {}
