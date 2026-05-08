package com.sistemabarberia.fadex_backend.modules.categoria.service.impl;

import com.sistemabarberia.fadex_backend.commons.exception.BusinessException;
import com.sistemabarberia.fadex_backend.modules.categoria.dto.CategoriaFiltro;
import com.sistemabarberia.fadex_backend.modules.categoria.dto.request.CategoriaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.categoria.dto.response.CategoriaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.categoria.entity.Categoria;
import com.sistemabarberia.fadex_backend.modules.categoria.entity.CategoriaEnum;
import com.sistemabarberia.fadex_backend.modules.categoria.mapper.CategoriaMapper;
import com.sistemabarberia.fadex_backend.modules.categoria.repository.CategoriaRepository;
import com.sistemabarberia.fadex_backend.modules.categoria.service.ICategoriaService;
import com.sistemabarberia.fadex_backend.modules.producto.repository.ProductoRepository;
import com.sistemabarberia.fadex_backend.modules.servicio.repository.CorteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements ICategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final CorteRepository corteRepository;
    private final CategoriaMapper categoriaMapper;

    @Override
    public List<CategoriaResponseDTO> listarConFiltro(CategoriaFiltro filtro) {
        boolean incluirInactivas = filtro != null && Boolean.FALSE.equals(filtro.getEstado());
        Map<Long, CategoriaResponseDTO> mapa = construirArbol(incluirInactivas);
        List<CategoriaResponseDTO> raiz = mapa.values().stream().filter(cat -> cat.getPadreId() == null).toList();
        if (filtro == null) {
            return raiz;
        }
        return filtrarArbol(raiz, filtro);
    }

    @Override
    public CategoriaResponseDTO obtenerPorId(Long id) {
        Categoria categoria = categoriaRepository.findById(id).orElseThrow(() -> new BusinessException("Categoría no encontrada", HttpStatus.NOT_FOUND));
        Map<Long, CategoriaResponseDTO> mapa = construirArbol(true);
        if (categoria.getPadre() != null) {
            return mapa.get(categoria.getPadre().getId());
        }
        return mapa.get(id);
    }

    @Override
    public CategoriaResponseDTO crear(CategoriaRequestDTO dto) {
        if (dto.getPadreId() == null) {
            if (categoriaRepository.existsByNombreIgnoreCaseAndPadreIsNullAndEstadoTrue(dto.getNombre().trim())) {
                throw new BusinessException("La categoría ya existe en nivel raíz", HttpStatus.BAD_REQUEST);
            }
        } else {
            if (categoriaRepository.existsByNombreIgnoreCaseAndPadreIdAndEstadoTrueAndTipo(dto.getNombre().trim(), dto.getPadreId(), dto.getTipo())) {
                throw new BusinessException("La categoría ya existe en esta categoría padre", HttpStatus.BAD_REQUEST);
            }
        }
        Categoria categoria = new Categoria();
        categoria.setNombre(dto.getNombre().trim());
        categoria.setDescripcion(dto.getDescripcion() != null ? dto.getDescripcion().trim() : null);
        categoria.setEstado(dto.getEstado() != null ? dto.getEstado() : true);
        categoria.setTipo(dto.getTipo());

        if (dto.getPadreId() != null) {
            Categoria padre = categoriaRepository.findById(dto.getPadreId()).orElseThrow(() -> new BusinessException("La categoría padre no existe", HttpStatus.NOT_FOUND));
            if (!padre.isEstado()) {
                throw new BusinessException("La categoría padre está inactiva", HttpStatus.BAD_REQUEST);
            }
            if (!padre.getTipo().equals(dto.getTipo())) {
                throw new BusinessException("El tipo debe coincidir con la categoría padre", HttpStatus.BAD_REQUEST);
            }
            categoria.setPadre(padre);
        }
        return categoriaMapper.toResponse(categoriaRepository.save(categoria));
    }

    @Override
    public CategoriaResponseDTO actualizar(Long id, CategoriaRequestDTO dto) {
        Categoria categoria = categoriaRepository.findById(id).orElseThrow(() -> new BusinessException("Categoría no encontrada", HttpStatus.NOT_FOUND));
        boolean cambioNombre = !categoria.getNombre().equalsIgnoreCase(dto.getNombre().trim());
        boolean cambioTipo = !categoria.getTipo().equals(dto.getTipo());

        if (cambioNombre) {
            if (dto.getPadreId() == null) {
                if (categoriaRepository.existsByNombreIgnoreCaseAndPadreIsNullAndEstadoTrue(dto.getNombre().trim())) {
                    throw new BusinessException("La categoría ya existe en nivel raíz", HttpStatus.BAD_REQUEST);
                }
            } else {
                if (categoriaRepository.existsByNombreIgnoreCaseAndPadreIdAndEstadoTrueAndTipo(dto.getNombre().trim(), dto.getPadreId(), dto.getTipo())) {
                    throw new BusinessException("La categoría ya existe en esta categoría padre", HttpStatus.BAD_REQUEST);
                }
            }
        }
        if (dto.getPadreId() != null) {
            Categoria padre = categoriaRepository.findById(dto.getPadreId()).orElseThrow(() -> new BusinessException("La categoría padre no existe", HttpStatus.NOT_FOUND));
            if (!padre.isEstado()) {
                throw new BusinessException("La categoría padre está inactiva", HttpStatus.BAD_REQUEST);
            }
            if (dto.getPadreId().equals(id)) {
                throw new BusinessException("Una categoría no puede ser su propia padre", HttpStatus.BAD_REQUEST);
            }
            if (!padre.getTipo().equals(dto.getTipo())) {
                throw new BusinessException("El tipo debe coincidir con la categoría padre", HttpStatus.BAD_REQUEST);
            }
            categoria.setPadre(padre);
        } else {
            categoria.setPadre(null);
        }
        if (cambioTipo) {
            boolean tieneHijos = categoriaRepository.existsByPadreId(categoria.getId());
            boolean tieneElementos = categoria.getTipo() == CategoriaEnum.PRODUCTO ? productoRepository.existsByCategoriaId(id) : corteRepository.existsByCategoriaId(id);
            if (tieneHijos) {
                throw new BusinessException("No se puede cambiar el tipo porque tiene subcategorías", HttpStatus.BAD_REQUEST);
            }
            if (tieneElementos) {
                throw new BusinessException("No se puede cambiar el tipo porque tiene elementos asociados", HttpStatus.BAD_REQUEST);
            }
        }
        categoria.setNombre(dto.getNombre().trim());
        categoria.setDescripcion(dto.getDescripcion() != null ? dto.getDescripcion().trim() : null);
        categoria.setEstado(dto.getEstado() != null ? dto.getEstado() : categoria.isEstado());
        categoria.setTipo(dto.getTipo());
        return categoriaMapper.toResponse(categoriaRepository.save(categoria));
    }

    @Override
    public CategoriaResponseDTO cambiarEstado(Long id, Boolean estado) {
        Categoria categoria = categoriaRepository.findById(id).orElseThrow(() -> new BusinessException("Categoría no encontrada", HttpStatus.NOT_FOUND));
        if (Boolean.FALSE.equals(estado)) {
            boolean tieneSubcategorias = categoriaRepository.existsByPadreId(id);
            boolean tieneElementos = categoria.getTipo() == CategoriaEnum.PRODUCTO ? productoRepository.existsByCategoriaId(id) : corteRepository.existsByCategoriaId(id);
            if (tieneSubcategorias) {
                throw new BusinessException("No se puede desactivar la categoría porque tiene subcategorías asociadas", HttpStatus.BAD_REQUEST);
            }
            if (tieneElementos) {
                throw new BusinessException("No se puede desactivar la categoría porque tiene elementos asociados", HttpStatus.BAD_REQUEST);
            }
        }
        categoria.setEstado(estado);
        return categoriaMapper.toResponse(categoriaRepository.save(categoria));
    }

    @Override
    public void eliminar(Long id) {
        Categoria categoria = categoriaRepository.findById(id).orElseThrow(() -> new BusinessException("Categoría no encontrada", HttpStatus.NOT_FOUND));
        boolean tieneSubcategorias = categoriaRepository.existsByPadreId(id);
        boolean tieneElementos = categoria.getTipo() == CategoriaEnum.PRODUCTO ? productoRepository.existsByCategoriaId(id) : corteRepository.existsByCategoriaId(id);
        if (tieneSubcategorias) {
            throw new BusinessException("No se puede eliminar la categoría porque tiene subcategorías asociadas", HttpStatus.BAD_REQUEST);
        }
        if (tieneElementos) {
            throw new BusinessException("No se puede eliminar la categoría porque tiene elementos asociados", HttpStatus.BAD_REQUEST);
        }
        categoria.setEstado(false);
        categoriaRepository.save(categoria);
    }

    private Map<Long, CategoriaResponseDTO> construirArbol(boolean incluirInactivas) {
        List<Categoria> categorias = incluirInactivas ? categoriaRepository.findAll() : categoriaRepository.findByEstadoTrue();
        List<CategoriaResponseDTO> dtos = categoriaMapper.toResponseList(categorias);
        Map<Long, CategoriaResponseDTO> mapa = new HashMap<>();
        for (CategoriaResponseDTO dto : dtos) {
            dto.setSubcategorias(new ArrayList<>());
            mapa.put(dto.getId(), dto);
        }
        for (CategoriaResponseDTO dto : dtos) {
            if (dto.getPadreId() != null) {
                CategoriaResponseDTO padre = mapa.get(dto.getPadreId());
                if (padre != null) {
                    padre.getSubcategorias().add(dto);
                }
            }
        }
        return mapa;
    }

    private List<CategoriaResponseDTO> filtrarArbol(List<CategoriaResponseDTO> lista, CategoriaFiltro filtro) {
        List<CategoriaResponseDTO> resultado = new ArrayList<>();
        for (CategoriaResponseDTO cat : lista) {
            boolean cumple = true;
            if (filtro.getPadreId() != null && (cat.getPadreId() == null || !cat.getPadreId().equals(filtro.getPadreId()))) {
                cumple = false;
            }
            if (filtro.getNombre() != null && !cat.getNombre().toLowerCase().contains(filtro.getNombre().toLowerCase().trim())) {
                cumple = false;
            }
            if (filtro.getEstado() != null && cat.isEstado() != filtro.getEstado()) {
                cumple = false;
            }
            if (filtro.getTipo() != null && !cat.getTipo().equals(filtro.getTipo())) {
                cumple = false;
            }
            List<CategoriaResponseDTO> hijosFiltrados = filtrarArbol(cat.getSubcategorias(), filtro);
            if (cumple || !hijosFiltrados.isEmpty()) {
                cat.setSubcategorias(hijosFiltrados);
                resultado.add(cat);
            }
        }
        return resultado;
    }
}