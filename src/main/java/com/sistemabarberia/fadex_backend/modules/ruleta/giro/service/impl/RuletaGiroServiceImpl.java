package com.sistemabarberia.fadex_backend.modules.ruleta.giro.service.impl;

import com.sistemabarberia.fadex_backend.commons.exception.BusinessException;
import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.cliente.entity.Cliente;
import com.sistemabarberia.fadex_backend.modules.cliente.repository.ClienteRepository;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.entity.FidelizacionTarjeta;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.repository.FidelizacionTarjetaRepository;
import com.sistemabarberia.fadex_backend.modules.ruleta.giro.dto.RuletaGiroFiltro;
import com.sistemabarberia.fadex_backend.modules.ruleta.giro.dto.request.RuletaGiroRequestDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.giro.dto.response.RuletaGiroResponseDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.giro.entity.RuletaGiro;
import com.sistemabarberia.fadex_backend.modules.ruleta.giro.mapper.RuletaGiroMapper;
import com.sistemabarberia.fadex_backend.modules.ruleta.giro.repository.RuletaGiroRepository;
import com.sistemabarberia.fadex_backend.modules.ruleta.giro.service.IRuletaGiroService;
import com.sistemabarberia.fadex_backend.modules.ruleta.giro.specs.RuletaGiroSpecification;
import com.sistemabarberia.fadex_backend.modules.ruleta.item.entity.RuletaItem;
import com.sistemabarberia.fadex_backend.modules.ruleta.item.repository.RuletaItemRepository;
import com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.entity.Ruleta;
import com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.repository.RuletaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RuletaGiroServiceImpl implements IRuletaGiroService {

    private final RuletaGiroRepository giroRepository;
    private final RuletaGiroMapper giroMapper;
    private final FidelizacionTarjetaRepository tarjetaRepository;
    private final ClienteRepository clienteRepository;
    private final RuletaRepository ruletaRepository;
    private final RuletaItemRepository itemRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<RuletaGiroResponseDTO> listarGiros(RuletaGiroFiltro filtro, Pageable pageable) {
        Page<RuletaGiro> page = giroRepository.findAll(RuletaGiroSpecification.conFiltros(filtro), pageable);
        return PageResponse.of(page.map(giroMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public RuletaGiroResponseDTO obtenerGiroPorId(Long id) {
        RuletaGiro giro = giroRepository.findById(id).orElseThrow(() -> new BusinessException("Giro no encontrado", HttpStatus.NOT_FOUND));
        return giroMapper.toResponse(giro);
    }

    @Override
    @Transactional
    public RuletaGiroResponseDTO crearGiro(RuletaGiroRequestDTO dto) {
        FidelizacionTarjeta tarjeta = obtenerTarjeta(dto.getTarjetaId());
        if (!tarjeta.getCliente().getClienteId().equals(dto.getClienteId().intValue())) {
            throw new BusinessException("La tarjeta no pertenece al cliente.", HttpStatus.BAD_REQUEST);
        }
        RuletaGiro giro = giroMapper.toEntity(dto);
        giro.setTarjeta(tarjeta);
        giro.setCliente(obtenerCliente(dto.getClienteId()));
        giro.setRuleta(obtenerRuleta(dto.getRuletaId()));
        giro.setItem(obtenerItem(dto.getItemId()));
        return giroMapper.toResponse(giroRepository.save(giro));
    }

    @Override
    @Transactional
    public RuletaGiroResponseDTO actualizarGiro(Long id, RuletaGiroRequestDTO dto) {
        FidelizacionTarjeta tarjeta = obtenerTarjeta(dto.getTarjetaId());
        if (!tarjeta.getCliente().getClienteId().equals(dto.getClienteId().intValue())) {
            throw new BusinessException("La tarjeta no pertenece al cliente.", HttpStatus.BAD_REQUEST);
        }
        RuletaGiro giro = giroRepository.findById(id).orElseThrow(() -> new BusinessException("Giro no encontrado", HttpStatus.NOT_FOUND));
        giroMapper.updateFromRequest(dto, giro);
        giro.setTarjeta(tarjeta);
        giro.setCliente(obtenerCliente(dto.getClienteId()));
        giro.setRuleta(obtenerRuleta(dto.getRuletaId()));
        giro.setItem(obtenerItem(dto.getItemId()));
        return giroMapper.toResponse(giroRepository.save(giro));
    }

    @Override
    @Transactional
    public void eliminarGiro(Long id) {
        RuletaGiro giro = giroRepository.findById(id).orElseThrow(() -> new BusinessException("Giro no encontrado", HttpStatus.NOT_FOUND));
        giroRepository.delete(giro);
    }

    private FidelizacionTarjeta obtenerTarjeta(Long id) {
        return tarjetaRepository.findById(id).orElseThrow(() -> new BusinessException("Tarjeta no encontrada", HttpStatus.NOT_FOUND));
    }

    private Cliente obtenerCliente(Long id) {
        return clienteRepository.findById(id.intValue()).orElseThrow(() -> new BusinessException("Cliente no encontrado", HttpStatus.NOT_FOUND));
    }

    private Ruleta obtenerRuleta(Long id) {
        return ruletaRepository.findById(id).orElseThrow(() -> new BusinessException("Ruleta no encontrada", HttpStatus.NOT_FOUND));
    }

    private RuletaItem obtenerItem(Long id) {
        return itemRepository.findById(id).orElseThrow(() -> new BusinessException("Premio no encontrado", HttpStatus.NOT_FOUND));
    }
}