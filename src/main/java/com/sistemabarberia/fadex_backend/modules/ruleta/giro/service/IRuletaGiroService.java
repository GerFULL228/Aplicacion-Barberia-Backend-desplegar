package com.sistemabarberia.fadex_backend.modules.ruleta.giro.service;

import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.ruleta.giro.dto.RuletaGiroFiltro;
import com.sistemabarberia.fadex_backend.modules.ruleta.giro.dto.request.RuletaGiroRequestDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.giro.dto.response.RuletaGiroResponseDTO;
import org.springframework.data.domain.Pageable;

public interface IRuletaGiroService {
    PageResponse<RuletaGiroResponseDTO> listarGiros(RuletaGiroFiltro filtro, Pageable pageable);
    RuletaGiroResponseDTO obtenerGiroPorId(Long id);
    RuletaGiroResponseDTO crearGiro(RuletaGiroRequestDTO dto);
    RuletaGiroResponseDTO actualizarGiro(Long id, RuletaGiroRequestDTO dto);
    void eliminarGiro(Long id);
}