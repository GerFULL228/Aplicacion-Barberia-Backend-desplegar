package com.sistemabarberia.fadex_backend.modules.servicio.service.impl;

import com.sistemabarberia.fadex_backend.modules.categoria.entity.Categoria;
import com.sistemabarberia.fadex_backend.modules.categoria.repository.CategoriaRepository;
import com.sistemabarberia.fadex_backend.modules.servicio.dto.request.CorteRequestDTO;
import com.sistemabarberia.fadex_backend.modules.servicio.dto.response.CorteResponseDTO;
import com.sistemabarberia.fadex_backend.modules.servicio.entity.Corte;
import com.sistemabarberia.fadex_backend.modules.servicio.mapper.CorteMapper;
import com.sistemabarberia.fadex_backend.modules.servicio.repository.CorteRepository;
import com.sistemabarberia.fadex_backend.modules.servicio.service.ICorteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CorteServiceImpl implements ICorteService {

    private final CorteRepository corteRepository;
    private final CategoriaRepository categoriaRepository;
    private final CorteMapper corteMapper;

    @Override
    public CorteResponseDTO crear(CorteRequestDTO dto) {

        if (corteRepository.existsByNombre(dto.getNombre())) {
            throw new RuntimeException("Corte ya existe");
        }

        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        Corte corte = corteMapper.toEntity(dto);
        corte.setCategoria(categoria);

        return corteMapper.toResponse(corteRepository.save(corte));
    }

    @Override
    public List<CorteResponseDTO> listar() {
        return corteMapper.toResponseList(corteRepository.findAll());
    }

    @Override
    public List<CorteResponseDTO> listarPorCategoria(Long categoriaId) {

        if (!categoriaRepository.existsById(categoriaId)) {
            throw new RuntimeException("Categoría no encontrada");
        }

        return corteMapper.toResponseList(
                corteRepository.findByCategoriaId(categoriaId)
        );
    }

    @Override
    public CorteResponseDTO obtenerPorId(Long id) {
        Corte corte = corteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Corte no encontrado"));

        return corteMapper.toResponse(corte);
    }

    @Override
    public CorteResponseDTO actualizar(Long id, CorteRequestDTO dto) {

        Corte corte = corteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Corte no encontrado"));

        if (!corte.getNombre().equals(dto.getNombre()) &&
                corteRepository.existsByNombre(dto.getNombre())) {
            throw new RuntimeException("Corte ya existe");
        }

        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        corte.setNombre(dto.getNombre());
        corte.setPrecio(dto.getPrecio());
        corte.setCategoria(categoria);

        return corteMapper.toResponse(corteRepository.save(corte));
    }

    @Override
    public void eliminar(Long id) {

        if (!corteRepository.existsById(id)) {
            throw new RuntimeException("Corte no encontrado");
        }

        corteRepository.deleteById(id);
    }
}