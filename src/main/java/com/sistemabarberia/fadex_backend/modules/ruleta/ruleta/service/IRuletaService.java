package com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.service;

import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.dto.RuletaFiltro;
import com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.dto.request.RuletaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.dto.response.RuletaResponseDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IRuletaService {
    PageResponse<RuletaResponseDTO> listarRuletasConFiltro(RuletaFiltro filtro, Pageable pageable);
    List<RuletaResponseDTO> listarActivas();
    RuletaResponseDTO obtenerRuletaPorId(Long id);
    RuletaResponseDTO crearRuleta(RuletaRequestDTO dto);
    RuletaResponseDTO actualizarRuleta(Long id, RuletaRequestDTO dto);
    void eliminarRuleta(Long id);
}