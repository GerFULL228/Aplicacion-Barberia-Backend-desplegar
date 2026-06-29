package com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.service.impl;

import com.sistemabarberia.fadex_backend.auth.usuario.Entity.Usuario;
import com.sistemabarberia.fadex_backend.auth.usuario.Repository.UsuarioRepository;
import com.sistemabarberia.fadex_backend.commons.exception.BusinessException;
import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.cliente.entity.Cliente;
import com.sistemabarberia.fadex_backend.modules.cliente.repository.ClienteRepository;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.dto.RecompensaObtenidaFiltro;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.dto.request.RecompensaObtenidaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.dto.response.RecompensaObtenidaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.entity.RecompensaObtenida;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.mapper.RecompensaObtenidaMapper;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.repository.RecompensaObtenidaRepository;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.service.IRecompensaObtenidaService;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.specs.RecompensaObtenidaSpecification;
import com.sistemabarberia.fadex_backend.modules.ruleta.giro.entity.RuletaGiro;
import com.sistemabarberia.fadex_backend.modules.ruleta.giro.repository.RuletaGiroRepository;
import com.sistemabarberia.fadex_backend.modules.ruleta.item.entity.RuletaItem;
import com.sistemabarberia.fadex_backend.modules.ruleta.item.repository.RuletaItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecompensaObtenidaServiceImpl implements IRecompensaObtenidaService {

    private final RecompensaObtenidaRepository recompensaObtenidaRepository;
    private final RecompensaObtenidaMapper recompensaObtenidaMapper;
    private final RuletaGiroRepository giroRepository;
    private final ClienteRepository clienteRepository;
    private final RuletaItemRepository itemRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<RecompensaObtenidaResponseDTO> listar(RecompensaObtenidaFiltro filtro, Pageable pageable) {
        Page<RecompensaObtenida> page = recompensaObtenidaRepository.findAll(RecompensaObtenidaSpecification.conFiltros(filtro), pageable);
        return PageResponse.of(page.map(recompensaObtenidaMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public RecompensaObtenidaResponseDTO obtenerPorId(Long id) {
        RecompensaObtenida entity = recompensaObtenidaRepository.findById(id).orElseThrow(() -> new BusinessException("Recompensa no encontrada", HttpStatus.NOT_FOUND));
        return recompensaObtenidaMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public RecompensaObtenidaResponseDTO crear(RecompensaObtenidaRequestDTO dto) {
        RecompensaObtenida entity = recompensaObtenidaMapper.toEntity(dto);
        entity.setGiro(obtenerGiro(dto.getGiroId()));
        entity.setCliente(obtenerCliente(dto.getClienteId()));
        entity.setItem(obtenerItem(dto.getItemId()));
        if (dto.getUsuarioCanjeId() != null) {
            entity.setUsuarioCanje(obtenerUsuario(dto.getUsuarioCanjeId()));
        }
        return recompensaObtenidaMapper.toResponse(recompensaObtenidaRepository.save(entity));
    }

    @Override
    @Transactional
    public RecompensaObtenidaResponseDTO actualizar(Long id, RecompensaObtenidaRequestDTO dto) {
        RecompensaObtenida entity = recompensaObtenidaRepository.findById(id).orElseThrow(() -> new BusinessException("Recompensa no encontrada", HttpStatus.NOT_FOUND));
        recompensaObtenidaMapper.updateFromRequest(dto, entity);
        entity.setGiro(obtenerGiro(dto.getGiroId()));
        entity.setCliente(obtenerCliente(dto.getClienteId()));
        entity.setItem(obtenerItem(dto.getItemId()));
        if (dto.getUsuarioCanjeId() != null) {
            entity.setUsuarioCanje(obtenerUsuario(dto.getUsuarioCanjeId()));
        } else {
            entity.setUsuarioCanje(null);
        }
        return recompensaObtenidaMapper.toResponse(recompensaObtenidaRepository.save(entity));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        RecompensaObtenida entity = recompensaObtenidaRepository.findById(id).orElseThrow(() -> new BusinessException("Recompensa no encontrada", HttpStatus.NOT_FOUND));
        recompensaObtenidaRepository.delete(entity);
    }

    private RuletaGiro obtenerGiro(Long id) {
        return giroRepository.findById(id).orElseThrow(() -> new BusinessException("Giro no encontrado", HttpStatus.NOT_FOUND));
    }

    private Cliente obtenerCliente(Long id) {
        return clienteRepository.findById(id.intValue()).orElseThrow(() -> new BusinessException("Cliente no encontrado", HttpStatus.NOT_FOUND));
    }

    private RuletaItem obtenerItem(Long id) {
        return itemRepository.findById(id).orElseThrow(() -> new BusinessException("Item no encontrado", HttpStatus.NOT_FOUND));
    }

    private Usuario obtenerUsuario(Long id) {
        return usuarioRepository.findById(id.intValue()).orElseThrow(() -> new BusinessException("Usuario no encontrado", HttpStatus.NOT_FOUND));
    }
}