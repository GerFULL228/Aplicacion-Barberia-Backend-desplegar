package com.sistemabarberia.fadex_backend.modules.ruleta.item.service.impl;

import com.sistemabarberia.fadex_backend.commons.exception.BusinessException;
import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.commons.storage.FileStorageService;
import com.sistemabarberia.fadex_backend.modules.producto.entity.Producto;
import com.sistemabarberia.fadex_backend.modules.ruleta.validator.RuletaItemValidator;
import com.sistemabarberia.fadex_backend.modules.ruleta.item.entity.enums.TipoPremio;
import com.sistemabarberia.fadex_backend.modules.ruleta.item.specs.RuletaItemSpecification;
import com.sistemabarberia.fadex_backend.modules.servicio.entity.Servicio;
import com.sistemabarberia.fadex_backend.modules.producto.repository.ProductoRepository;
import com.sistemabarberia.fadex_backend.modules.ruleta.item.dto.RuletaItemFiltro;
import com.sistemabarberia.fadex_backend.modules.ruleta.item.dto.request.RuletaItemRequestDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.item.dto.response.RuletaItemResponseDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.item.entity.RuletaItem;
import com.sistemabarberia.fadex_backend.modules.ruleta.item.mapper.RuletaItemMapper;
import com.sistemabarberia.fadex_backend.modules.ruleta.item.repository.RuletaItemRepository;
import com.sistemabarberia.fadex_backend.modules.ruleta.item.service.IRuletaItemService;
import com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.entity.Ruleta;
import com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.repository.RuletaRepository;
import com.sistemabarberia.fadex_backend.modules.servicio.repository.ServicioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RuletaItemServiceImpl implements IRuletaItemService {
    private final RuletaItemRepository ruletaItemRepository;
    private final RuletaRepository ruletaRepository;
    private final ProductoRepository productoRepository;
    private final ServicioRepository servicioRepository;
    private final FileStorageService storageService;
    private final RuletaItemMapper ruletaItemMapper;
    private final RuletaItemValidator ruletaItemValidator;

    private static final List<String> TIPOS_IMAGEN = List.of("image/jpeg","image/png","image/webp");

    @Override
    @Transactional(readOnly = true)
    public PageResponse<RuletaItemResponseDTO> listarItemConFiltros(RuletaItemFiltro filtro, Pageable pageable) {
        Page<RuletaItem> page = ruletaItemRepository.findAll(RuletaItemSpecification.conFiltros(filtro), pageable);
        return PageResponse.of(page.map(ruletaItemMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public RuletaItemResponseDTO obtenerItemPorId(Long id) {
        RuletaItem item = ruletaItemRepository.findById(id).orElseThrow(() -> new BusinessException("Item de ruleta no encontrado", HttpStatus.NOT_FOUND));
        return ruletaItemMapper.toResponse(item);
    }

    @Override
    @Transactional
    public RuletaItemResponseDTO crearItem(RuletaItemRequestDTO dto, MultipartFile imagen) {
        Ruleta ruleta = obtenerRuleta(dto.getRuletaId());
        RuletaItem item = ruletaItemMapper.toEntity(dto);
        item.setRuleta(ruleta);
        asignarPremio(item, dto, imagen);
        ruletaItemValidator.validarPremioMayor(dto, null);
        RuletaItem guardado = ruletaItemRepository.save(item);
        ruletaItemValidator.validarProbabilidadesRuleta(item.getRuleta().getRuletaId());
        return ruletaItemMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    public RuletaItemResponseDTO actualizarItem(Long id, RuletaItemRequestDTO dto, MultipartFile imagen) {
        RuletaItem item = ruletaItemRepository.findById(id).orElseThrow(() -> new BusinessException("Item de ruleta no encontrado", HttpStatus.NOT_FOUND));
        boolean imagenPropia = item.getProducto() == null && item.getServicio() == null && item.getImagenUrl() != null;
        if (imagenPropia && (dto.getTipoPremio() == TipoPremio.PRODUCTO || dto.getTipoPremio() == TipoPremio.SERVICIO)) {
            storageService.eliminarArchivo(item.getImagenUrl());
        }
        ruletaItemMapper.updateFromRequest(dto, item);
        item.setRuleta(obtenerRuleta(dto.getRuletaId()));
        asignarPremio(item, dto, imagen);
        ruletaItemValidator.validarPremioMayor(dto, id);
        RuletaItem guardado = ruletaItemRepository.save(item);
        ruletaItemValidator.validarProbabilidadesRuleta(item.getRuleta().getRuletaId());
        return ruletaItemMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    public RuletaItemResponseDTO cambiarEstado(Long id, Boolean activo) {
        RuletaItem item = ruletaItemRepository.findById(id).orElseThrow(() -> new BusinessException("Item de ruleta no encontrado", HttpStatus.NOT_FOUND));
        item.setActivo(activo);
        return ruletaItemMapper.toResponse(ruletaItemRepository.save(item));
    }

    @Override
    @Transactional
    public void eliminarItem(Long id) {
        RuletaItem item = ruletaItemRepository.findById(id).orElseThrow(() -> new BusinessException("Item de ruleta no encontrado", HttpStatus.NOT_FOUND));
        boolean imagenPropia = item.getProducto() == null && item.getServicio() == null && item.getImagenUrl() != null;
        if (imagenPropia) {
            storageService.eliminarArchivo(item.getImagenUrl());
        }
        ruletaItemRepository.delete(item);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuletaItem> obtenerItemsActivos(Long ruletaId) {
        return ruletaItemRepository.findByRuletaRuletaIdAndActivoTrue(ruletaId);
    }

    private void asignarPremio(RuletaItem item, RuletaItemRequestDTO dto, MultipartFile imagen) {
        item.setProducto(null);
        item.setServicio(null);
        if (dto.getCantidadProducto() == null || dto.getCantidadProducto() <= 0) {
            item.setCantidadProducto(1);
        } else {
            item.setCantidadProducto(dto.getCantidadProducto());
        }
        switch (dto.getTipoPremio()) {
            case PRODUCTO -> {
                if (dto.getProductoId() == null) {
                    throw new BusinessException("Debe seleccionar un producto", HttpStatus.BAD_REQUEST);
                }
                Producto producto = productoRepository.findById(dto.getProductoId()).orElseThrow(() -> new BusinessException("Producto no encontrado", HttpStatus.NOT_FOUND));
                item.setProducto(producto);
                item.setImagenUrl(producto.getUrlsMultimedia() != null && !producto.getUrlsMultimedia().isEmpty() ? producto.getUrlsMultimedia().getFirst() : null);
            }
            case SERVICIO -> {
                if (dto.getServicioId() == null) {
                    throw new BusinessException("Debe seleccionar un servicio", HttpStatus.BAD_REQUEST);
                }
                Servicio servicio = servicioRepository.findById(dto.getServicioId()).orElseThrow(() -> new BusinessException("Servicio no encontrado", HttpStatus.NOT_FOUND));
                item.setServicio(servicio);
                item.setImagenUrl(servicio.getUrlsMultimedia() != null && !servicio.getUrlsMultimedia().isEmpty() ? servicio.getUrlsMultimedia().getFirst() : null);
            }
            case DESCUENTO, CUPON -> {
                if (imagen != null && !imagen.isEmpty()) {
                    if (item.getImagenUrl() != null) {
                        storageService.eliminarArchivo(item.getImagenUrl());
                    }
                    String url = storageService.guardarArchivo(imagen, "ruleta-items", TIPOS_IMAGEN);
                    item.setImagenUrl(url);
                }
            }
            case SIN_PREMIO -> {
                item.setProducto(null);
                item.setServicio(null);
                item.setCantidadProducto(null);
                if (imagen != null && !imagen.isEmpty()) {
                    if (item.getImagenUrl() != null) {
                        storageService.eliminarArchivo(item.getImagenUrl());
                    }
                    String url = storageService.guardarArchivo(imagen, "ruleta-items", TIPOS_IMAGEN);
                    item.setImagenUrl(url);
                }
            }
        }
    }

//    private String obtenerImagenProducto(Producto producto) {
//        if (producto.getUrlsMultimedia() == null || producto.getUrlsMultimedia().isEmpty()) {
//            throw new BusinessException("El producto seleccionado no tiene imágenes", HttpStatus.BAD_REQUEST);
//        }
//        return producto.getUrlsMultimedia().getFirst();
//    }
//
//    private String obtenerImagenServicio(Servicio servicio) {
//        if (servicio.getUrlsMultimedia() == null || servicio.getUrlsMultimedia().isEmpty()) {
//            throw new BusinessException("El servicio seleccionado no tiene imágenes", HttpStatus.BAD_REQUEST);
//        }
//        return servicio.getUrlsMultimedia().getFirst();
//    }

    private Ruleta obtenerRuleta(Long id) {
        return ruletaRepository.findById(id).orElseThrow(() -> new BusinessException("Ruleta no encontrada", HttpStatus.NOT_FOUND));
    }
}
