package com.sistemabarberia.fadex_backend.modules.ruleta.giro.dto.response;

import java.time.LocalDate;

public record GiroPorSemanaDTO(LocalDate semanaInicio, Long total) {}