package com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.service.impl;

import com.sistemabarberia.fadex_backend.commons.exception.BusinessException;
import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.categoria.entity.Categoria;
import com.sistemabarberia.fadex_backend.modules.categoria.repository.CategoriaRepository;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.dto.ConfiguracionFiltro;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.dto.request.ConfiguracionPatchRequestDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.dto.request.ConfiguracionRequestDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.dto.response.ConfiguracionResponseDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.entity.FidelizacionConfiguracion;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.mapper.FidelizacionConfiguracionMapper;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.repository.FidelizacionConfiguracionRepository;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.service.IFidelizacionConfiguracionService;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.specs.FidelizacionConfiguracionSpecification;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.regla.service.IFidelizacionReglaService;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.service.IFidelizacionTarjetaService;
import com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.entity.Ruleta;
import com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.repository.RuletaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FidelizacionConfiguracionServiceImpl implements IFidelizacionConfiguracionService {

    @Autowired
    private FidelizacionConfiguracionRepository configuracionRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private RuletaRepository ruletaRepository;

    @Autowired
    private FidelizacionConfiguracionMapper configuracionMapper;

    @Autowired
    @Lazy
    private IFidelizacionTarjetaService tarjetaService;

    @Autowired
    private IFidelizacionReglaService reglaService;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ConfiguracionResponseDTO> listarConfiguracionConFiltro(ConfiguracionFiltro filtro, Pageable pageable) {
        Page<FidelizacionConfiguracion> page = configuracionRepository.findAll(FidelizacionConfiguracionSpecification.conFiltros(filtro), pageable);
        List<ConfiguracionResponseDTO> data = page.getContent().stream().map(configuracionMapper::toResponse).toList();
        return PageResponse.<ConfiguracionResponseDTO>builder().content(data).pageNumber(page.getNumber()).pageSize(page.getSize()).totalElements(page.getTotalElements()).totalPages(page.getTotalPages()).last(page.isLast()).build();
    }

    @Override
    @Transactional(readOnly = true)
    public ConfiguracionResponseDTO obtenerConfiguracionPorId(Long id) {
        FidelizacionConfiguracion configuracion = configuracionRepository.findById(id).orElseThrow(() -> new BusinessException("Configuración no encontrada", HttpStatus.NOT_FOUND));
        return configuracionMapper.toResponse(configuracion);
    }

    @Override
    @Transactional
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
                .girosPorMeta(dto.getGirosPorMeta()).mostrarSiempre(dto.getMostrarSiempre()).crearTarjetaAutomatica(dto.getCrearTarjetaAutomatica()).ruleta(ruleta).build();

        configuracion = configuracionRepository.save(configuracion);
        reglaService.crearReglaPorDefecto(configuracion.getCategoria());
        if (Boolean.TRUE.equals(configuracion.getCrearTarjetaAutomatica())) {
            tarjetaService.crearTarjetasParaCategoria(configuracion.getCategoria());
        }
        return configuracionMapper.toResponse(configuracion);
    }

    @Override
    @Transactional
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
        boolean antes = Boolean.TRUE.equals(configuracion.getCrearTarjetaAutomatica());
        configuracion.setActiva(dto.getActiva());
        configuracion.setMeta(dto.getMeta());
        configuracion.setGirosPorMeta(dto.getGirosPorMeta());
        configuracion.setMostrarSiempre(dto.getMostrarSiempre());
        configuracion.setRuleta(ruleta);
        configuracion.setCrearTarjetaAutomatica(dto.getCrearTarjetaAutomatica());
        System.out.println(configuracion.getGirosPorMeta());
        configuracion = configuracionRepository.save(configuracion);
        System.out.println(configuracion.getGirosPorMeta());
        if (!antes && Boolean.TRUE.equals(configuracion.getCrearTarjetaAutomatica())) {
            tarjetaService.crearTarjetasParaCategoria(configuracion.getCategoria());
        }
        System.out.println(dto.getMeta());
        System.out.println(dto.getGirosPorMeta());
        System.out.println(dto);
        return configuracionMapper.toResponse(configuracion);
    }

    @Override
    @Transactional
    public void eliminarConfiguracion(Long id) {
        FidelizacionConfiguracion configuracion = configuracionRepository.findById(id).orElseThrow(() -> new BusinessException("Configuración no encontrada.", HttpStatus.NOT_FOUND));
        configuracionRepository.delete(configuracion);
    }

    @Override
    @Transactional(readOnly = true)
    public FidelizacionConfiguracion obtenerConfiguracionActiva(Long categoriaId) {
        return configuracionRepository.findByCategoria_IdAndActivaTrue(categoriaId).orElseThrow(() -> new BusinessException("No existe una configuración activa para la categoría.", HttpStatus.NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public Integer contarConfiguraciones() {
        return Math.toIntExact(configuracionRepository.count());
    }

    @Override
    @Transactional
    public ConfiguracionResponseDTO actualizarParcial(Long id, ConfiguracionPatchRequestDTO dto) {
        FidelizacionConfiguracion configuracion = configuracionRepository.findById(id).orElseThrow(() -> new BusinessException("Configuración no encontrada.", HttpStatus.NOT_FOUND));
        switch (dto.getCampo()) {
            case "activa": configuracion.setActiva(dto.getValor());
                break;
            case "mostrarSiempre": configuracion.setMostrarSiempre(dto.getValor());
                break;
            case "crearTarjetaAutomatica": boolean antes = Boolean.TRUE.equals(configuracion.getCrearTarjetaAutomatica());
                configuracion.setCrearTarjetaAutomatica(dto.getValor());
                if (!antes && Boolean.TRUE.equals(dto.getValor())) {
                    tarjetaService.crearTarjetasParaCategoria(configuracion.getCategoria());
                }
                break;
            default:
                throw new BusinessException("Campo no permitido.", HttpStatus.BAD_REQUEST);
        }
        configuracion = configuracionRepository.save(configuracion);
        return configuracionMapper.toResponse(configuracion);
    }
}
