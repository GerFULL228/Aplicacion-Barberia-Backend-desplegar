package com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.service;

import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.dto.ConfiguracionFiltro;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.dto.request.ConfiguracionRequestDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.dto.response.ConfiguracionResponseDTO;

import com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.entity.FidelizacionConfiguracion;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IFidelizacionConfiguracionService {
    PageResponse<ConfiguracionResponseDTO> listarConfiguracionConFiltro(ConfiguracionFiltro filtro, Pageable pageable);
    ConfiguracionResponseDTO obtenerConfiguracionPorId(Long id);
    ConfiguracionResponseDTO crearConfiguracion(ConfiguracionRequestDTO dto);
    ConfiguracionResponseDTO actualizarConfiguracion(Long id, ConfiguracionRequestDTO dto);
    void eliminarConfiguracion(Long id);
    FidelizacionConfiguracion obtenerConfiguracionActiva(Long categoriaId);
}