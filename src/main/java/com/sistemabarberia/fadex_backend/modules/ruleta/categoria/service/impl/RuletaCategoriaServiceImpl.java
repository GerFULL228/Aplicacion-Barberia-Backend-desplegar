package com.sistemabarberia.fadex_backend.modules.ruleta.categoria.service.impl;

import com.sistemabarberia.fadex_backend.commons.exception.BusinessException;
import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.categoria.entity.Categoria;
import com.sistemabarberia.fadex_backend.modules.categoria.repository.CategoriaRepository;
import com.sistemabarberia.fadex_backend.modules.ruleta.categoria.dto.RuletaCategoriaFiltro;
import com.sistemabarberia.fadex_backend.modules.ruleta.categoria.dto.request.RuletaCategoriaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.categoria.dto.response.RuletaCategoriaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.categoria.entity.RuletaCategoria;
import com.sistemabarberia.fadex_backend.modules.ruleta.categoria.mapper.RuletaCategoriaMapper;
import com.sistemabarberia.fadex_backend.modules.ruleta.categoria.repository.RuletaCategoriaRepository;
import com.sistemabarberia.fadex_backend.modules.ruleta.categoria.service.IRuletaCategoriaService;
import com.sistemabarberia.fadex_backend.modules.ruleta.categoria.specs.RuletaCategoriaSpecs;
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
@Transactional
public class RuletaCategoriaServiceImpl implements IRuletaCategoriaService {

    private final RuletaCategoriaRepository ruletaCategoriaRepository;
    private final RuletaRepository ruletaRepository;
    private final CategoriaRepository categoriaRepository;
    private final RuletaCategoriaMapper ruletaCategoriaMapper;

    @Override
    public RuletaCategoriaResponseDTO crearCategoria(RuletaCategoriaRequestDTO request) {
        Ruleta ruleta = ruletaRepository.findById(request.getIdRuleta()).orElseThrow(() -> new BusinessException("Ruleta no encontrada.", HttpStatus.NOT_FOUND));
        Categoria categoria = categoriaRepository.findById(request.getIdCategoria()).orElseThrow(() -> new BusinessException("Categoría no encontrada", HttpStatus.NOT_FOUND));
        RuletaCategoria entity = new RuletaCategoria();
        entity.setRuleta(ruleta);
        entity.setCategoria(categoria);
        return ruletaCategoriaMapper.toResponse(ruletaCategoriaRepository.save(entity));
    }

    @Override
    public RuletaCategoriaResponseDTO actualizarCategoria(Long id, RuletaCategoriaRequestDTO request) {
        RuletaCategoria entity = ruletaCategoriaRepository.findById(id).orElseThrow(() -> new BusinessException("Relación no encontrada", HttpStatus.NOT_FOUND));
        Ruleta ruleta = ruletaRepository.findById(request.getIdRuleta()).orElseThrow(() -> new BusinessException("Ruleta no encontrada.", HttpStatus.NOT_FOUND));
        Categoria categoria = categoriaRepository.findById(request.getIdCategoria()).orElseThrow(() ->  new BusinessException("Categoría no encontrada", HttpStatus.NOT_FOUND));
        entity.setRuleta(ruleta);
        entity.setCategoria(categoria);
        return ruletaCategoriaMapper.toResponse(ruletaCategoriaRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public RuletaCategoriaResponseDTO obtenerCategoriaPorId(Long id) {
        RuletaCategoria entity = ruletaCategoriaRepository.findById(id).orElseThrow(() -> new BusinessException("Relación no encontrada.", HttpStatus.NOT_FOUND));
        return ruletaCategoriaMapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<RuletaCategoriaResponseDTO> listarCategoriaConFiltros(RuletaCategoriaFiltro filtro, Pageable pageable) {
        Page<RuletaCategoria> page = ruletaCategoriaRepository.findAll(RuletaCategoriaSpecs.filter(filtro), pageable);
        return PageResponse.of(page.map(ruletaCategoriaMapper::toResponse));
    }

    @Override
    public void eliminarCategoria(Long id) {
        RuletaCategoria entity = ruletaCategoriaRepository.findById(id).orElseThrow(() -> new BusinessException("Relación no encontrada.", HttpStatus.NOT_FOUND));
        ruletaCategoriaRepository.delete(entity);
    }
}