package com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.service;

import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.dto.FidelizacionMovimientoFiltro;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.dto.request.FidelizacionMovimientoRequestDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.dto.response.FidelizacionMovimientoResponseDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.entity.enums.OrigenFidelizacion;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.entity.FidelizacionTarjeta;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IFidelizacionMovimientoService {
    PageResponse<FidelizacionMovimientoResponseDTO> listarMovimientos(FidelizacionMovimientoFiltro filtro, Pageable pageable);
    FidelizacionMovimientoResponseDTO obtenerMovimientoPorId(Long id);
    FidelizacionMovimientoResponseDTO crearMovimiento(FidelizacionMovimientoRequestDTO dto);
    FidelizacionMovimientoResponseDTO actualizarMovimiento(Long id, FidelizacionMovimientoRequestDTO dto);
    List<FidelizacionMovimientoResponseDTO> listarPorCliente(Integer clienteId);
    void eliminarMovimiento(Long id);
    void registrarMovimiento(FidelizacionTarjeta tarjeta, OrigenFidelizacion origen, Long idOrigen, int puntos, String descripcion);
    List<FidelizacionMovimientoResponseDTO> obtenerMisMovimientos();
    List<FidelizacionMovimientoResponseDTO> obtenerUltimosMovimientos(int limite);
}