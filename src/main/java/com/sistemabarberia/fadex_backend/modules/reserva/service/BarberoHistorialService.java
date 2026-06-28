package com.sistemabarberia.fadex_backend.modules.reserva.service;
import com.sistemabarberia.fadex_backend.modules.reserva.dto.Response.HistorialCorteDTO;

import java.time.LocalDate;
import java.util.List;
public interface BarberoHistorialService {
    List<HistorialCorteDTO> getHistorial(String username, LocalDate desde, LocalDate hasta, String clienteNombre);
}
