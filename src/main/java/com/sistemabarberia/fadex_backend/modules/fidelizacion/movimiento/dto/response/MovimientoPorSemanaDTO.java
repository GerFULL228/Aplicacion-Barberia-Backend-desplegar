package com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.dto.response;

import java.time.LocalDate;

public record MovimientoPorSemanaDTO(LocalDate semanaInicio, Long positivos, Long negativos) {}