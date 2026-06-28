package com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.service;

import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.dto.FidelizacionMovimientoFiltro;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.dto.request.FidelizacionMovimientoRequestDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.dto.response.FidelizacionMovimientoResponseDTO;
import org.springframework.data.domain.Pageable;

public interface IFidelizacionMovimientoService {
    PageResponse<FidelizacionMovimientoResponseDTO> listarMovimientos(FidelizacionMovimientoFiltro filtro, Pageable pageable);
    FidelizacionMovimientoResponseDTO obtenerMovimientoPorId(Long id);
    FidelizacionMovimientoResponseDTO crearMovimiento(FidelizacionMovimientoRequestDTO dto);
    FidelizacionMovimientoResponseDTO actualizarMovimiento(Long id, FidelizacionMovimientoRequestDTO dto);
    void eliminarMovimiento(Long id);
}