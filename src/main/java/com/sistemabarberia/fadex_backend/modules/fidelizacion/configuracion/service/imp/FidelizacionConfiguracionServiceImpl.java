package com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.service.imp;

import com.sistemabarberia.fadex_backend.commons.exception.BusinessException;
import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.categoria.entity.Categoria;
import com.sistemabarberia.fadex_backend.modules.categoria.repository.CategoriaRepository;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.dto.ConfiguracionFiltro;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.dto.request.ConfiguracionRequestDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.dto.response.ConfiguracionResponseDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.entity.FidelizacionConfiguracion;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.mapper.FidelizacionConfiguracionMapper;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.repository.FidelizacionConfiguracionRepository;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.service.IFidelizacionConfiguracionService;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.specs.FidelizacionConfiguracionSpecification;
import com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.entity.Ruleta;
import com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.repository.RuletaRepository;
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
public class FidelizacionConfiguracionServiceImpl implements IFidelizacionConfiguracionService {

    private final FidelizacionConfiguracionRepository configuracionRepository;
    private final CategoriaRepository categoriaRepository;
    private final RuletaRepository ruletaRepository;
    private final FidelizacionConfiguracionMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ConfiguracionResponseDTO> listarConfiguracionConFiltro(ConfiguracionFiltro filtro, Pageable pageable) {
        Page<FidelizacionConfiguracion> page = configuracionRepository.findAll(FidelizacionConfiguracionSpecification.conFiltros(filtro), pageable);
        List<ConfiguracionResponseDTO> data = page.getContent().stream().map(mapper::toResponse).toList();
        return PageResponse.<ConfiguracionResponseDTO>builder()
                .content(data).pageNumber(page.getNumber()).pageSize(page.getSize()).totalElements(page.getTotalElements()).totalPages(page.getTotalPages()).last(page.isLast()).build();
    }

    @Override
    @Transactional(readOnly = true)
    public ConfiguracionResponseDTO obtenerConfiguracionPorId(Long id) {
        FidelizacionConfiguracion configuracion = configuracionRepository.findById(id).orElseThrow(() -> new BusinessException("Configuración no encontrada", HttpStatus.NOT_FOUND));
        return mapper.toResponse(configuracion);
    }

    @Override
    public ConfiguracionResponseDTO crearConfiguracion(ConfiguracionRequestDTO dto) {
        if (configuracionRepository.existsByCategoria_Id(dto.getCategoriaId())) {
            throw new BusinessException("La categoría ya posee una configuración.", HttpStatus.BAD_REQUEST);
        }
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId()).orElseThrow(() -> new BusinessException("Categoría no encontrada.", HttpStatus.NOT_FOUND));
        Ruleta ruleta = null;
        if (dto.getRuletaId() != null) {
            ruleta = ruletaRepository.findById(dto.getRuletaId()).orElseThrow(() -> new BusinessException("Ruleta no encontrada.", HttpStatus.NOT_FOUND));
        }
        FidelizacionConfiguracion configuracion = FidelizacionConfiguracion.builder().categoria(categoria).activa(dto.getActiva()).meta(dto.getMeta())
                .mostrarSiempre(dto.getMostrarSiempre()).crearTarjetaAutomatica(dto.getCrearTarjetaAutomatica()).ruleta(ruleta).build();
        return mapper.toResponse(configuracionRepository.save(configuracion));
    }

    @Override
    public ConfiguracionResponseDTO actualizarConfiguracion(Long id, ConfiguracionRequestDTO dto) {
        FidelizacionConfiguracion configuracion = configuracionRepository.findById(id).orElseThrow(() -> new BusinessException("Configuración no encontrada.", HttpStatus.NOT_FOUND));
        if (!configuracion.getCategoria().getId().equals(dto.getCategoriaId())) {
            if (configuracionRepository.existsByCategoria_Id(dto.getCategoriaId())) {
                throw new BusinessException("La categoría ya posee una configuración.", HttpStatus.BAD_REQUEST);
            }
            Categoria categoria = categoriaRepository.findById(dto.getCategoriaId()).orElseThrow(() -> new BusinessException("Categoría no encontrada.", HttpStatus.NOT_FOUND));
            configuracion.setCategoria(categoria);
        }
        Ruleta ruleta = null;
        if (dto.getRuletaId() != null) {
            ruleta = ruletaRepository.findById(dto.getRuletaId()).orElseThrow(() -> new BusinessException("Ruleta no encontrada.", HttpStatus.NOT_FOUND));
        }
        configuracion.setActiva(dto.getActiva());
        configuracion.setMeta(dto.getMeta());
        configuracion.setMostrarSiempre(dto.getMostrarSiempre());
        configuracion.setCrearTarjetaAutomatica(dto.getCrearTarjetaAutomatica());
        configuracion.setRuleta(ruleta);
        return mapper.toResponse(configuracionRepository.save(configuracion));
    }

    @Override
    public void eliminarConfiguracion(Long id) {
        FidelizacionConfiguracion configuracion = configuracionRepository.findById(id).orElseThrow(() -> new BusinessException("Configuración no encontrada.", HttpStatus.NOT_FOUND));
        configuracionRepository.delete(configuracion);
    }
}
