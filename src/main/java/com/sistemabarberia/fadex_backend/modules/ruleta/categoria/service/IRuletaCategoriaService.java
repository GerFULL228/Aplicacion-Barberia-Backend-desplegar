package com.sistemabarberia.fadex_backend.modules.ruleta.categoria.service;

import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.ruleta.categoria.dto.RuletaCategoriaFiltro;
import com.sistemabarberia.fadex_backend.modules.ruleta.categoria.dto.request.RuletaCategoriaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.categoria.dto.response.RuletaCategoriaResponseDTO;
import org.springframework.data.domain.Pageable;

public interface IRuletaCategoriaService {
    PageResponse<RuletaCategoriaResponseDTO> listarCategoriaConFiltros(RuletaCategoriaFiltro filtro, Pageable pageable);
    RuletaCategoriaResponseDTO obtenerCategoriaPorId(Long id);
    RuletaCategoriaResponseDTO crearCategoria(RuletaCategoriaRequestDTO dto);
    RuletaCategoriaResponseDTO actualizarCategoria(Long id, RuletaCategoriaRequestDTO dto);
    void eliminarCategoria(Long id);

}