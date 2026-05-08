package com.sistemabarberia.fadex_backend.modules.servicio.service;

import com.sistemabarberia.fadex_backend.modules.servicio.dto.request.CorteRequestDTO;
import com.sistemabarberia.fadex_backend.modules.servicio.dto.response.CorteResponseDTO;

import java.util.List;

public interface ICorteService {

    CorteResponseDTO crear(CorteRequestDTO dto);

    List<CorteResponseDTO> listar();

    List<CorteResponseDTO> listarPorCategoria(Long categoriaId);

    CorteResponseDTO obtenerPorId(Long id);

    CorteResponseDTO actualizar(Long id, CorteRequestDTO dto);

    void eliminar(Long id);
}