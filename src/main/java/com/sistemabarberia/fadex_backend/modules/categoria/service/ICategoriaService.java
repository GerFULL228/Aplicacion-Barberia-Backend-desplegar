package com.sistemabarberia.fadex_backend.modules.categoria.service;

import com.sistemabarberia.fadex_backend.modules.categoria.dto.CategoriaFiltro;
import com.sistemabarberia.fadex_backend.modules.categoria.dto.request.CategoriaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.categoria.dto.response.CategoriaResponseDTO;

import java.util.List;

public interface ICategoriaService {
    List<CategoriaResponseDTO> listarConFiltro(CategoriaFiltro filtro);
    CategoriaResponseDTO obtenerPorId(Long id);
    CategoriaResponseDTO crear(CategoriaRequestDTO dto);
    CategoriaResponseDTO actualizar(Long id, CategoriaRequestDTO dto);
    CategoriaResponseDTO cambiarEstado(Long id, Boolean estado);
    void eliminar(Long id);
}