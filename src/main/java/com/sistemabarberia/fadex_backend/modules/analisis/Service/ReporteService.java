package com.sistemabarberia.fadex_backend.modules.analisis.Service;


import com.sistemabarberia.fadex_backend.modules.analisis.dto.ResumenDiaDTO;

import java.time.LocalDate;
import java.util.List;

public interface ReporteService {
    List<ResumenDiaDTO> getResumenSemanal(LocalDate desde, LocalDate hasta);
    byte[] generarReservasPdf(LocalDate desde, LocalDate hasta);
    byte[] generarReservasExcel(LocalDate desde, LocalDate hasta);
    byte[] generarVentasPdf(LocalDate desde, LocalDate hasta);
    byte[] generarVentasExcel(LocalDate desde, LocalDate hasta);
    byte[] generarClientesPdf(LocalDate desde, LocalDate hasta);
    byte[] generarClientesExcel(LocalDate desde, LocalDate hasta);
    byte[] generarBarberosPdf(LocalDate desde, LocalDate hasta);
    byte[] generarBarberosExcel(LocalDate desde, LocalDate hasta);
}
