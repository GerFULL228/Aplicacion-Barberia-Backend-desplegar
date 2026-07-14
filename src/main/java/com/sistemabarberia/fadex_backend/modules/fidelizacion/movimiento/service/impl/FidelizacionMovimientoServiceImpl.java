package com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.service.impl;

import com.sistemabarberia.fadex_backend.auth.usuario.Entity.Usuario;
import com.sistemabarberia.fadex_backend.auth.usuario.service.UsuarioSecurityService;
import com.sistemabarberia.fadex_backend.commons.exception.BusinessException;
import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.cliente.entity.Cliente;
import com.sistemabarberia.fadex_backend.modules.cliente.repository.ClienteRepository;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.dto.FidelizacionMovimientoFiltro;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.dto.request.FidelizacionMovimientoRequestDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.dto.response.FidelizacionMovimientoResponseDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.entity.FidelizacionMovimiento;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.entity.enums.OrigenFidelizacion;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.mapper.FidelizacionMovimientoMapper;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.repository.FidelizacionMovimientoRepository;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.service.IFidelizacionMovimientoService;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.specs.FidelizacionMovimientoSpecification;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.entity.FidelizacionTarjeta;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.repository.FidelizacionTarjetaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FidelizacionMovimientoServiceImpl implements IFidelizacionMovimientoService {

    private final FidelizacionMovimientoRepository movimientoRepository;
    private final FidelizacionMovimientoMapper movimientoMapper;
    private final FidelizacionTarjetaRepository tarjetaRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioSecurityService usuarioSecurityService;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<FidelizacionMovimientoResponseDTO> listarMovimientos(FidelizacionMovimientoFiltro filtro, Pageable pageable) {
        Page<FidelizacionMovimiento> page = movimientoRepository.findAll(FidelizacionMovimientoSpecification.conFiltros(filtro), pageable);
        return PageResponse.of(page.map(movimientoMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public FidelizacionMovimientoResponseDTO obtenerMovimientoPorId(Long id) {
        FidelizacionMovimiento movimiento = movimientoRepository.findById(id).orElseThrow(() -> new BusinessException("Movimiento no encontrado", HttpStatus.NOT_FOUND));
        return movimientoMapper.toResponse(movimiento);
    }

    @Override
    @Transactional
    public FidelizacionMovimientoResponseDTO crearMovimiento(FidelizacionMovimientoRequestDTO dto) {
        FidelizacionTarjeta tarjeta = obtenerTarjeta(dto.getTarjetaId());
        if (!tarjeta.getCliente().getClienteId().equals(dto.getClienteId().intValue())) {
            throw new BusinessException("La tarjeta no pertenece al cliente indicado.", HttpStatus.BAD_REQUEST);
        }
        FidelizacionMovimiento movimiento = movimientoMapper.toEntity(dto);
        movimiento.setTarjeta(tarjeta);
        movimiento.setCliente(obtenerCliente(dto.getClienteId()));
        return movimientoMapper.toResponse(movimientoRepository.save(movimiento));
    }

    @Override
    @Transactional
    public FidelizacionMovimientoResponseDTO actualizarMovimiento(Long id, FidelizacionMovimientoRequestDTO dto) {
        FidelizacionTarjeta tarjeta = obtenerTarjeta(dto.getTarjetaId());
        if (!tarjeta.getCliente().getClienteId().equals(dto.getClienteId().intValue())) {
            throw new BusinessException("La tarjeta no pertenece al cliente indicado.", HttpStatus.BAD_REQUEST);
        }
        FidelizacionMovimiento movimiento = movimientoRepository.findById(id).orElseThrow(() -> new BusinessException("Movimiento no encontrado", HttpStatus.NOT_FOUND));
        movimientoMapper.updateFromRequest(dto, movimiento);
        movimiento.setTarjeta(tarjeta);
        movimiento.setCliente(obtenerCliente(dto.getClienteId()));
        return movimientoMapper.toResponse(movimientoRepository.save(movimiento));
    }

    @Override
    @Transactional
    public void eliminarMovimiento(Long id) {
        FidelizacionMovimiento movimiento = movimientoRepository.findById(id).orElseThrow(() -> new BusinessException("Movimiento no encontrado", HttpStatus.NOT_FOUND));
        movimientoRepository.delete(movimiento);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FidelizacionMovimientoResponseDTO> listarPorCliente(Integer clienteId) {
        return movimientoMapper.toResponseList(movimientoRepository.findByClienteClienteIdOrderByCreatedAtDesc(clienteId));
    }

    @Override
    @Transactional
    public void registrarMovimiento(FidelizacionTarjeta tarjeta, OrigenFidelizacion origen, Long idOrigen, int puntos, String descripcion) {
        FidelizacionMovimiento movimiento = FidelizacionMovimiento.builder().tarjeta(tarjeta).cliente(tarjeta.getCliente()).origen(origen).idOrigen(idOrigen).puntos(puntos).descripcion(descripcion).build();
        movimientoRepository.save(movimiento);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FidelizacionMovimientoResponseDTO> obtenerMisMovimientos() {
        Usuario usuario = usuarioSecurityService.getUsuarioLogueado();
        Cliente cliente = clienteRepository.findByUsuarioId(usuario.getIdUsuario()).orElseThrow(() -> new BusinessException("Cliente no encontrado.", HttpStatus.NOT_FOUND));
        return movimientoMapper.toResponseList(movimientoRepository.findByClienteClienteIdOrderByCreatedAtDesc(cliente.getClienteId()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FidelizacionMovimientoResponseDTO> obtenerUltimosMovimientos(int limite) {
        return movimientoRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, limite)).stream().map(movimientoMapper::toResponse).toList();
    }

    private FidelizacionTarjeta obtenerTarjeta(Long id) {
        return tarjetaRepository.findById(id).orElseThrow(() -> new BusinessException("Tarjeta no encontrada", HttpStatus.NOT_FOUND));
    }

    private Cliente obtenerCliente(Long id) {
        return clienteRepository.findById(id.intValue()).orElseThrow(() -> new BusinessException("Cliente no encontrado", HttpStatus.NOT_FOUND));
    }
}