package com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.service;

import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.dto.FidelizacionTarjetaFiltro;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.dto.request.FidelizacionTarjetaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.dto.response.FidelizacionTarjetaResponseDTO;
import org.springframework.data.domain.Pageable;

public interface IFidelizacionTarjetaService {
    PageResponse<FidelizacionTarjetaResponseDTO> listarTarjetas(FidelizacionTarjetaFiltro filtro, Pageable pageable);
    FidelizacionTarjetaResponseDTO obtenerTarjetaPorId(Long id);
    FidelizacionTarjetaResponseDTO crearTarjeta(FidelizacionTarjetaRequestDTO dto);
    FidelizacionTarjetaResponseDTO actualizarTarjeta(Long id, FidelizacionTarjetaRequestDTO dto);
    void eliminarTarjeta(Long id);
}