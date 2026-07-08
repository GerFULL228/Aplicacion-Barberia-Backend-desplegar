package com.sistemabarberia.fadex_backend.modules.fidelizacion.regla.service.impl;

import com.sistemabarberia.fadex_backend.commons.exception.BusinessException;
import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.categoria.entity.Categoria;
import com.sistemabarberia.fadex_backend.modules.categoria.repository.CategoriaRepository;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.regla.dto.FidelizacionReglaFiltro;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.regla.dto.request.FidelizacionReglaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.regla.dto.response.FidelizacionReglaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.regla.entity.FidelizacionRegla;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.regla.entity.enums.TipoAlcanceFidelizacion;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.regla.mapper.FidelizacionReglaMapper;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.regla.repository.FidelizacionReglaRepository;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.regla.service.IFidelizacionReglaService;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.regla.specs.FidelizacionReglaSpecification;
import com.sistemabarberia.fadex_backend.modules.producto.entity.Producto;
import com.sistemabarberia.fadex_backend.modules.producto.repository.ProductoRepository;
import com.sistemabarberia.fadex_backend.modules.servicio.entity.Servicio;
import com.sistemabarberia.fadex_backend.modules.servicio.repository.ServicioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FidelizacionReglaServiceImpl implements IFidelizacionReglaService {

    private final FidelizacionReglaRepository reglaRepository;
    private final CategoriaRepository categoriaRepository;
    private final ServicioRepository servicioRepository;
    private final ProductoRepository productoRepository;
    private final FidelizacionReglaMapper reglaMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<FidelizacionReglaResponseDTO> listarReglaConFiltros(FidelizacionReglaFiltro filtro, Pageable pageable) {
        Page<FidelizacionRegla> page = reglaRepository.findAll(FidelizacionReglaSpecification.conFiltros(filtro),pageable);
        return PageResponse.of(page.map(reglaMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public FidelizacionReglaResponseDTO obtenerReglaPorId(Long id) {
        FidelizacionRegla regla = reglaRepository.findById(id).orElseThrow(() -> new BusinessException("Regla no encontrada", HttpStatus.NOT_FOUND));
        return reglaMapper.toResponse(regla);
    }

    @Override
    @Transactional
    public FidelizacionReglaResponseDTO crearRegla(FidelizacionReglaRequestDTO dto) {
        FidelizacionRegla regla = reglaMapper.toEntity(dto);
        regla.setCategoria(obtenerCategoria(dto.getCategoriaId()));
        asignarAlcance(regla,dto);
        return reglaMapper.toResponse(reglaRepository.save(regla));
    }

    @Override
    @Transactional
    public FidelizacionReglaResponseDTO actualizarRegla(Long id, FidelizacionReglaRequestDTO dto) {
        FidelizacionRegla regla = reglaRepository.findById(id).orElseThrow(() -> new BusinessException("Regla no encontrada", HttpStatus.NOT_FOUND));
        reglaMapper.updateFromRequest(dto,regla);
        regla.setCategoria(obtenerCategoria(dto.getCategoriaId()));
        asignarAlcance(regla,dto);
        return reglaMapper.toResponse(reglaRepository.save(regla));
    }

    @Override
    @Transactional
    public void eliminarRegla(Long id) {
        FidelizacionRegla regla = reglaRepository.findById(id).orElseThrow(() -> new BusinessException("Regla no encontrada", HttpStatus.NOT_FOUND));
        reglaRepository.delete(regla);

    }

    @Override
    @Transactional
    public void crearReglaPorDefecto(Categoria categoria) {
        if (reglaRepository.existsByCategoriaIdAndTipoAlcanceAndActivoTrue(categoria.getId(), TipoAlcanceFidelizacion.CATEGORIA)) {return;}
        FidelizacionRegla regla = FidelizacionRegla.builder().categoria(categoria).tipoAlcance(TipoAlcanceFidelizacion.CATEGORIA).puntos(1).activo(true).build();
        reglaRepository.save(regla);
    }

    private Categoria obtenerCategoria(Long id){
        return categoriaRepository.findById(id).orElseThrow(() -> new BusinessException("Categoría no encontrada", HttpStatus.NOT_FOUND));
    }

    private void asignarAlcance(FidelizacionRegla regla,FidelizacionReglaRequestDTO dto){
        regla.setServicio(null);
        regla.setProducto(null);
        switch (dto.getTipoAlcance()){
            case SERVICIO -> {
                if (dto.getServicioId() == null) {
                    throw new BusinessException("Debe seleccionar un servicio", HttpStatus.BAD_REQUEST);
                }
                Servicio servicio = servicioRepository.findById(dto.getServicioId()).orElseThrow(() -> new BusinessException("Servicio no encontrado", HttpStatus.NOT_FOUND));
                regla.setServicio(servicio);
            }
            case PRODUCTO -> {
                if (dto.getProductoId() == null) {
                    throw new BusinessException("Debe seleccionar un producto", HttpStatus.BAD_REQUEST);
                }
                Producto producto = productoRepository.findById(dto.getProductoId()).orElseThrow(() -> new BusinessException("Producto no encontrado", HttpStatus.NOT_FOUND));
                regla.setProducto(producto);
            }
            case COMBO -> {
                if (dto.getServicioId() == null) {
                    throw new BusinessException("Debe seleccionar un servicio", HttpStatus.BAD_REQUEST);
                }
                if (dto.getProductoId() == null) {
                    throw new BusinessException("Debe seleccionar un producto", HttpStatus.BAD_REQUEST);
                }
                Servicio servicio = servicioRepository.findById(dto.getServicioId()).orElseThrow(() -> new BusinessException("Servicio no encontrado", HttpStatus.NOT_FOUND));
                Producto producto = productoRepository.findById(dto.getProductoId()).orElseThrow(() -> new BusinessException("Producto no encontrado", HttpStatus.NOT_FOUND));
                regla.setServicio(servicio);
                regla.setProducto(producto);
            }
            case CATEGORIA -> {
                if (dto.getServicioId() != null) {
                    throw new BusinessException("Una regla por categoría no puede tener servicio", HttpStatus.BAD_REQUEST);
                }

                if (dto.getProductoId() != null) {
                    throw new BusinessException("Una regla por categoría no puede tener producto", HttpStatus.BAD_REQUEST);
                }
            }
        }
    }
}