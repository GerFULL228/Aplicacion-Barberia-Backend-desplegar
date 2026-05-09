package com.sistemabarberia.fadex_backend.modules.servicio.service.impl;

import com.sistemabarberia.fadex_backend.modules.categoria.entity.Categoria;
import com.sistemabarberia.fadex_backend.modules.categoria.repository.CategoriaRepository;

import com.sistemabarberia.fadex_backend.modules.servicio.dto.request.ServicioRequestDTO;
import com.sistemabarberia.fadex_backend.modules.servicio.dto.response.ServicioResponseDTO;
import com.sistemabarberia.fadex_backend.modules.servicio.entity.Servicio;
import com.sistemabarberia.fadex_backend.modules.servicio.mapper.ServicioMapper;

import com.sistemabarberia.fadex_backend.modules.servicio.repository.ServicioRepository;
import com.sistemabarberia.fadex_backend.modules.servicio.service.IServicioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicioServiceImpl implements IServicioService {

    private final ServicioRepository servicioRepository;
    private final CategoriaRepository categoriaRepository;
    private final ServicioMapper corteMapper;

    @Override
    public ServicioResponseDTO crear(ServicioRequestDTO dto) {

        if (servicioRepository.existsByNombre(dto.getNombre())) {
            throw new RuntimeException("Servicio ya existe");
        }

        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        Servicio servicio = corteMapper.toEntity(dto);
        servicio.setCategoria(categoria);

        return corteMapper.toResponse(servicioRepository.save(servicio));
    }

    @Override
    public List<ServicioResponseDTO> listar() {
        return corteMapper.toResponseList(servicioRepository.findAll());
    }

    @Override
    public List<ServicioResponseDTO> listarPorCategoria(Long categoriaId) {

        if (!categoriaRepository.existsById(categoriaId)) {
            throw new RuntimeException("Categoría no encontrada");
        }

        return corteMapper.toResponseList(
                servicioRepository.findByCategoriaId(categoriaId)
        );
    }

    @Override
    public ServicioResponseDTO obtenerPorId(Long id) {
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

        return corteMapper.toResponse(servicio);
    }

    @Override
    public ServicioResponseDTO actualizar(Long id, ServicioRequestDTO dto) {

        Servicio corte = servicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

        if (!corte.getNombre().equals(dto.getNombre()) &&
                servicioRepository.existsByNombre(dto.getNombre())) {
            throw new RuntimeException("Corte ya existe");
        }

        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        corte.setNombre(dto.getNombre());
        corte.setPrecio(dto.getPrecio());
        corte.setCategoria(categoria);

        return corteMapper.toResponse(servicioRepository.save(corte));
    }

    @Override
    public void eliminar(Long id) {

        if (!servicioRepository.existsById(id)) {
            throw new RuntimeException("Corte no encontrado");
        }

        servicioRepository.deleteById(id);
    }
}