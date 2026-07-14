package com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.service;

import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.cliente.entity.Cliente;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.Reserva;
import com.sistemabarberia.fadex_backend.modules.ruleta.giro.entity.RuletaGiro;
import com.sistemabarberia.fadex_backend.modules.ruleta.item.entity.RuletaItem;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.dto.RecompensaObtenidaFiltro;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.dto.request.RecompensaObtenidaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.dto.response.RecompensaObtenidaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.entity.RecompensaObtenida;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.entity.enums.EstadoRecompensa;
import com.sistemabarberia.fadex_backend.modules.venta.entity.Venta;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IRecompensaObtenidaService {
    PageResponse<RecompensaObtenidaResponseDTO> listarRecompensaConFiltro(RecompensaObtenidaFiltro filtro, Pageable pageable);
    RecompensaObtenidaResponseDTO obtenerRecompensaPorId(Long id);
    RecompensaObtenidaResponseDTO crearRecompensa(RecompensaObtenidaRequestDTO dto);
    RecompensaObtenidaResponseDTO actualizarRecompensa(Long id, RecompensaObtenidaRequestDTO dto);
    void eliminarRecompensa(Long id);
    RecompensaObtenida crearDesdeGiro(RuletaGiro giro, Cliente cliente, RuletaItem item);
    List<RecompensaObtenidaResponseDTO> obtenerMisRecompensas();
    RecompensaObtenidaResponseDTO obtenerMiRecompensa(Long id);
    RecompensaObtenidaResponseDTO canjearRecompensa(String codigoCanje);
    void aplicarRecompensas(List<Long> recompensaIds, Venta venta);
    Reserva aplicarRecompensas(Reserva reserva, Integer clienteId, List<Long> longs);
    Integer contarRecompensas();
    List<RecompensaObtenidaResponseDTO> obtenerUltimasRecompensas(int limite);
    RecompensaObtenidaResponseDTO cambiarEstado(Long id, EstadoRecompensa nuevoEstado, String observacion);
}