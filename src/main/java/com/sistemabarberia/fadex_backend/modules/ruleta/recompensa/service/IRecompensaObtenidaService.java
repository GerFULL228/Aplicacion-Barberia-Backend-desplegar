package com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.service;

import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.dto.RecompensaObtenidaFiltro;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.dto.request.RecompensaObtenidaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.dto.response.RecompensaObtenidaResponseDTO;
import org.springframework.data.domain.Pageable;

public interface IRecompensaObtenidaService {
    PageResponse<RecompensaObtenidaResponseDTO> listar(RecompensaObtenidaFiltro filtro, Pageable pageable);
    RecompensaObtenidaResponseDTO obtenerPorId(Long id);
    RecompensaObtenidaResponseDTO crear(RecompensaObtenidaRequestDTO dto);
    RecompensaObtenidaResponseDTO actualizar(Long id, RecompensaObtenidaRequestDTO dto);
    void eliminar(Long id);
}