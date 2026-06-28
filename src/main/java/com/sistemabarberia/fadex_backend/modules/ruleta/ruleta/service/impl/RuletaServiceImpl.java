package com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.service.impl;

import com.sistemabarberia.fadex_backend.commons.exception.BusinessException;
import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.dto.RuletaFiltro;
import com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.dto.request.RuletaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.dto.response.RuletaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.entity.Ruleta;
import com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.mapper.RuletaMapper;
import com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.repository.RuletaRepository;
import com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.service.IRuletaService;
import com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.specs.RuletaSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RuletaServiceImpl implements IRuletaService {

    private final RuletaRepository ruletaRepository;
    private final RuletaMapper ruletaMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<RuletaResponseDTO> listarRuletasConFiltro(RuletaFiltro filtro, Pageable pageable) {
        Page<Ruleta> page = ruletaRepository.findAll(RuletaSpecification.conFiltros(filtro), pageable);
        List<RuletaResponseDTO> data = page.getContent().stream().map(ruletaMapper::toResponse).toList();
        return PageResponse.<RuletaResponseDTO>builder().content(data).pageNumber(page.getNumber()).pageSize(page.getSize()).totalElements(page.getTotalElements()).totalPages(page.getTotalPages()).last(page.isLast()).build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuletaResponseDTO> listarActivas() {
        return ruletaRepository.findByActivaTrue().stream().map(ruletaMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RuletaResponseDTO obtenerRuletaPorId(Long id) {
        Ruleta ruleta = ruletaRepository.findById(id).orElseThrow(() -> new BusinessException("Ruleta no encontrada.", HttpStatus.NOT_FOUND));
        return ruletaMapper.toResponse(ruleta);
    }

    @Override
    public RuletaResponseDTO crearRuleta(RuletaRequestDTO dto) {
        if (ruletaRepository.existsByNombreIgnoreCase(dto.getNombre())) {
            throw new BusinessException("Ya existe una ruleta con ese nombre.", HttpStatus.BAD_REQUEST);
        }
        Ruleta ruleta = Ruleta.builder().nombre(dto.getNombre()).descripcion(dto.getDescripcion()).tipo(dto.getTipo()).activa(dto.getActiva()).incrementoPorGiro(dto.getIncrementoPorGiro()).build();
        return ruletaMapper.toResponse(ruletaRepository.save(ruleta));
    }

    @Override
    public RuletaResponseDTO actualizarRuleta(Long id, RuletaRequestDTO dto) {
        Ruleta ruleta = ruletaRepository.findById(id).orElseThrow(() -> new BusinessException("Ruleta no encontrada.", HttpStatus.NOT_FOUND));
        if (!ruleta.getNombre().equalsIgnoreCase(dto.getNombre()) && ruletaRepository.existsByNombreIgnoreCase(dto.getNombre())) {
            throw new BusinessException("Ya existe una ruleta con ese nombre.", HttpStatus.BAD_REQUEST);
        }
        ruleta.setNombre(dto.getNombre());
        ruleta.setDescripcion(dto.getDescripcion());
        ruleta.setTipo(dto.getTipo());
        ruleta.setActiva(dto.getActiva());
        ruleta.setIncrementoPorGiro(dto.getIncrementoPorGiro());
        return ruletaMapper.toResponse(ruletaRepository.save(ruleta));
    }

    @Override
    public void eliminarRuleta(Long id) {
        Ruleta ruleta = ruletaRepository.findById(id).orElseThrow(() -> new BusinessException("Ruleta no encontrada.", HttpStatus.NOT_FOUND));
        ruletaRepository.delete(ruleta);
    }
}