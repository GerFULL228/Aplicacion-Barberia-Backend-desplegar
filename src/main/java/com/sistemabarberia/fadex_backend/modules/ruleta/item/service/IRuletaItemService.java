package com.sistemabarberia.fadex_backend.modules.ruleta.item.service;

import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.ruleta.item.dto.RuletaItemFiltro;
import com.sistemabarberia.fadex_backend.modules.ruleta.item.dto.request.RuletaItemRequestDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.item.dto.response.RuletaItemResponseDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface IRuletaItemService {
    PageResponse<RuletaItemResponseDTO> listarItemConFiltros(RuletaItemFiltro filtro, Pageable pageable);
    RuletaItemResponseDTO obtenerItemPorId(Long id);
    RuletaItemResponseDTO crearItem(RuletaItemRequestDTO dto, MultipartFile imagen);
    RuletaItemResponseDTO actualizarItem(Long id, RuletaItemRequestDTO dto, MultipartFile imagen);
    void eliminarItem(Long id);
}