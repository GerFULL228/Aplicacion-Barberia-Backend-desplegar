package com.sistemabarberia.fadex_backend.modules.fidelizacion.regla.service;

import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.regla.dto.FidelizacionReglaFiltro;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.regla.dto.request.FidelizacionReglaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.regla.dto.response.FidelizacionReglaResponseDTO;
import org.springframework.data.domain.Pageable;

public interface IFidelizacionReglaService {
    PageResponse<FidelizacionReglaResponseDTO> listarReglaConFiltros(FidelizacionReglaFiltro filtro, Pageable pageable);
    FidelizacionReglaResponseDTO obtenerReglaPorId(Long id);
    FidelizacionReglaResponseDTO crearRegla(FidelizacionReglaRequestDTO dto);
    FidelizacionReglaResponseDTO actualizarRegla(Long id,FidelizacionReglaRequestDTO dto);
    void eliminarRegla(Long id);
}