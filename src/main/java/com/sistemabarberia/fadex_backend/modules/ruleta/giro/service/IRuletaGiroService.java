package com.sistemabarberia.fadex_backend.modules.ruleta.giro.service;

import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.cliente.entity.Cliente;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.entity.FidelizacionTarjeta;
import com.sistemabarberia.fadex_backend.modules.ruleta.giro.dto.RuletaGiroFiltro;
import com.sistemabarberia.fadex_backend.modules.ruleta.giro.dto.request.RuletaGiroRequestDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.giro.dto.response.RuletaGiroResponseDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.giro.entity.RuletaGiro;
import com.sistemabarberia.fadex_backend.modules.ruleta.item.entity.RuletaItem;
import com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.entity.Ruleta;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IRuletaGiroService {
    PageResponse<RuletaGiroResponseDTO> listarGiros(RuletaGiroFiltro filtro, Pageable pageable);
    RuletaGiroResponseDTO obtenerGiroPorId(Long id);
    RuletaGiroResponseDTO crearGiro(RuletaGiroRequestDTO dto);
    RuletaGiroResponseDTO actualizarGiro(Long id, RuletaGiroRequestDTO dto);
    void eliminarGiro(Long id);
    RuletaGiro guardarGiro(FidelizacionTarjeta tarjeta, Cliente cliente, Ruleta ruleta, RuletaItem premio);
    List<RuletaGiroResponseDTO> obtenerMisGiros();
    RuletaGiroResponseDTO obtenerMiGiro(Long id);
}