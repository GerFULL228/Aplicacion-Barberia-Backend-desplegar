package com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.service.impl;

import com.sistemabarberia.fadex_backend.auth.usuario.Entity.Usuario;
import com.sistemabarberia.fadex_backend.auth.usuario.service.UsuarioSecurityService;
import com.sistemabarberia.fadex_backend.commons.exception.BusinessException;
import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.categoria.entity.Categoria;
import com.sistemabarberia.fadex_backend.modules.categoria.entity.CategoriaEnum;
import com.sistemabarberia.fadex_backend.modules.categoria.repository.CategoriaRepository;
import com.sistemabarberia.fadex_backend.modules.cliente.entity.Cliente;
import com.sistemabarberia.fadex_backend.modules.cliente.repository.ClienteRepository;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.entity.FidelizacionConfiguracion;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.repository.FidelizacionConfiguracionRepository;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.service.IFidelizacionConfiguracionService;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.helper.CategoriaFidelizacionResolver;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.entity.enums.OrigenFidelizacion;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.repository.FidelizacionMovimientoRepository;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.service.IFidelizacionMovimientoService;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.regla.entity.FidelizacionRegla;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.regla.entity.enums.TipoAlcanceFidelizacion;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.regla.repository.FidelizacionReglaRepository;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.dto.FidelizacionTarjetaFiltro;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.dto.request.FidelizacionTarjetaPatchRequestDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.dto.request.FidelizacionTarjetaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.dto.response.FidelizacionTarjetaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.dto.response.TarjetasPorCategoriaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.entity.FidelizacionTarjeta;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.mapper.FidelizacionTarjetaMapper;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.repository.FidelizacionTarjetaRepository;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.service.IFidelizacionTarjetaService;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.specs.FidelizacionTarjetaSpecification;
import com.sistemabarberia.fadex_backend.modules.producto.entity.Producto;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.Reserva;
import com.sistemabarberia.fadex_backend.modules.servicio.entity.Servicio;
import com.sistemabarberia.fadex_backend.modules.venta.entity.DetalleVenta;
import com.sistemabarberia.fadex_backend.modules.venta.entity.Venta;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FidelizacionTarjetaServiceImpl implements IFidelizacionTarjetaService {

    private final FidelizacionReglaRepository reglaRepository;
    private final FidelizacionMovimientoRepository movimientoRepository;
    private final FidelizacionTarjetaRepository tarjetaRepository;
    private final ClienteRepository clienteRepository;
    private final CategoriaRepository categoriaRepository;
    private final FidelizacionTarjetaMapper tarjetaMapper;
    private final IFidelizacionMovimientoService movimientoService;
    private final FidelizacionConfiguracionRepository configuracionRepository;
    private final UsuarioSecurityService usuarioSecurityService;
    private final CategoriaFidelizacionResolver categoriaResolver;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<FidelizacionTarjetaResponseDTO> listarTarjetas(FidelizacionTarjetaFiltro filtro, Pageable pageable) {
        Page<FidelizacionTarjeta> page = tarjetaRepository.findAll(FidelizacionTarjetaSpecification.conFiltros(filtro), pageable);
        Page<FidelizacionTarjetaResponseDTO> dtoPage = page.map(tarjetaMapper::toResponse);
        enriquecerConMeta(dtoPage.getContent());
        return PageResponse.of(dtoPage);
    }

    @Override
    @Transactional(readOnly = true)
    public FidelizacionTarjetaResponseDTO obtenerTarjetaPorId(Long id) {
        FidelizacionTarjeta tarjeta = tarjetaRepository.findById(id).orElseThrow(() -> new BusinessException("Tarjeta no encontrada", HttpStatus.NOT_FOUND));
        FidelizacionTarjetaResponseDTO dto = tarjetaMapper.toResponse(tarjeta);
        enriquecerConMeta(dto);
        return dto;
    }

    @Override
    @Transactional
    public FidelizacionTarjetaResponseDTO crearTarjeta(FidelizacionTarjetaRequestDTO dto) {
        if (tarjetaRepository.existsByClienteClienteIdAndCategoriaId(dto.getClienteId().intValue(), dto.getCategoriaId())) {
            throw new BusinessException("El cliente ya posee una tarjeta para esta categoría.", HttpStatus.BAD_REQUEST);
        }
        FidelizacionTarjeta tarjeta = tarjetaMapper.toEntity(dto);
        tarjeta.setCliente(obtenerCliente(dto.getClienteId().intValue()));
        tarjeta.setCategoria(obtenerCategoria(dto.getCategoriaId()));
        FidelizacionTarjetaResponseDTO response = tarjetaMapper.toResponse(tarjetaRepository.save(tarjeta));
        enriquecerConMeta(response);
        return response;
    }

    @Override
    @Transactional
    public FidelizacionTarjetaResponseDTO actualizarTarjeta(Long id, FidelizacionTarjetaRequestDTO dto) {
        FidelizacionTarjeta tarjeta = tarjetaRepository.findById(id).orElseThrow(() -> new BusinessException("Tarjeta no encontrada", HttpStatus.NOT_FOUND));
        if (!tarjeta.getCliente().getClienteId().equals(dto.getClienteId()) || !tarjeta.getCategoria().getId().equals(dto.getCategoriaId())) {
            if (tarjetaRepository.existsByClienteClienteIdAndCategoriaId(dto.getClienteId().intValue(), dto.getCategoriaId())){
                throw new BusinessException("Ya existe una tarjeta para ese cliente y categoría.", HttpStatus.BAD_REQUEST);
            }
        }
        tarjetaMapper.updateFromRequest(dto, tarjeta);
        tarjeta.setCliente(obtenerCliente(dto.getClienteId().intValue()));
        tarjeta.setCategoria(obtenerCategoria(dto.getCategoriaId()));
        FidelizacionTarjetaResponseDTO response = tarjetaMapper.toResponse(tarjetaRepository.save(tarjeta));
        enriquecerConMeta(response);
        return response;
    }

    @Override
    @Transactional
    public void eliminarTarjeta(Long id) {
        FidelizacionTarjeta tarjeta = tarjetaRepository.findById(id).orElseThrow(() -> new BusinessException("Tarjeta no encontrada", HttpStatus.NOT_FOUND));
        tarjetaRepository.delete(tarjeta);
    }

    @Override
    @Transactional
    public void crearTarjetasIniciales(Cliente cliente) {
        List<FidelizacionConfiguracion> configuraciones = configuracionRepository.findByActivaTrueAndCrearTarjetaAutomaticaTrue();
        for (FidelizacionConfiguracion configuracion : configuraciones) {
            Categoria categoria = configuracion.getCategoria();
            if (categoria.getPadre() != null) {continue;}
            if (tarjetaRepository.existsByClienteClienteIdAndCategoriaId(cliente.getClienteId(), categoria.getId())) {continue;}
            FidelizacionTarjeta tarjeta = FidelizacionTarjeta.builder().cliente(cliente).categoria(categoria).progreso(0).girosDisponibles(0).totalGiros(0).activo(true).cicloActivo(true).build();
            tarjetaRepository.save(tarjeta);
        }
    }

    @Override
    @Transactional
    public void acumularPorServicio(Reserva reserva) {
        System.out.println("========== FIDELIZACION ==========");
        System.out.println("Cliente: " + (reserva.getCliente() != null ? reserva.getCliente().getClienteId() : "NULL"));
        System.out.println("Reserva: " + reserva.getId());
        if (reserva.getCliente() == null) {
            System.out.println("No hay cliente");
            return;}
        if (movimientoRepository.existsByOrigenAndIdOrigen(OrigenFidelizacion.RESERVA, reserva.getId())) {
            System.out.println("Ya existe movimiento");
            return;}
        Servicio servicio = reserva.getServicio();
        Categoria categoriaFidelizacion = categoriaResolver.resolver(servicio.getCategoria());
        if (categoriaFidelizacion == null) {return;}
        Long categoriaId = categoriaFidelizacion.getId();
        FidelizacionTarjeta tarjeta = tarjetaRepository.findByClienteClienteIdAndCategoriaId(reserva.getCliente().getClienteId(), categoriaId).orElse(null);
        if (tarjeta == null) {return;}
        if (!Boolean.TRUE.equals(tarjeta.getActivo())) {return;}
        System.out.println("Tarjeta encontrada: " + (tarjeta != null));
        FidelizacionRegla regla = reglaRepository.findByCategoriaIdAndServicioServicioIdAndActivoTrue(categoriaId, servicio.getServicioId()).orElseGet(() -> {
            FidelizacionRegla nueva = FidelizacionRegla.builder().categoria(categoriaFidelizacion).tipoAlcance(TipoAlcanceFidelizacion.SERVICIO)
                    .servicio(servicio).puntos(1).activo(true).build();
            return reglaRepository.save(nueva);
        });
        tarjeta.setProgreso(tarjeta.getProgreso() + regla.getPuntos());
        tarjetaRepository.save(tarjeta);
        evaluarMeta(tarjeta);
        System.out.println("Nuevo progreso: " + tarjeta.getProgreso());
        movimientoService.registrarMovimiento(tarjeta, OrigenFidelizacion.RESERVA, reserva.getId(), regla.getPuntos(), "Servicio: " + servicio.getNombre());
        System.out.println("Movimiento registrado");
    }

    @Override
    @Transactional
    public void acumularPorVenta(Venta venta) {
        if (venta.getCliente() == null) {return;}
        if (venta.getDetalles() == null || venta.getDetalles().isEmpty()) {return;}
        if (movimientoRepository.existsByOrigenAndIdOrigen(OrigenFidelizacion.VENTA, venta.getVentaId().longValue())) {return;}
        for (DetalleVenta detalle : venta.getDetalles()) {
            if (detalle.getProducto() != null) {
                Producto producto = detalle.getProducto();
                Categoria categoriaFidelizacion = categoriaResolver.resolver(producto.getCategoria());
                if (categoriaFidelizacion == null) {continue;}
                Long categoriaId = categoriaFidelizacion.getId();
                FidelizacionTarjeta tarjeta = tarjetaRepository.findByClienteClienteIdAndCategoriaId(venta.getCliente().getClienteId(), categoriaId).orElse(null);
                if (tarjeta == null || !Boolean.TRUE.equals(tarjeta.getActivo())) {continue;}
                FidelizacionRegla regla = reglaRepository.findByCategoriaIdAndProductoIdAndActivoTrue(categoriaId, producto.getId()).orElseGet(() -> {
                    FidelizacionRegla nueva = FidelizacionRegla.builder().categoria(categoriaFidelizacion).tipoAlcance(TipoAlcanceFidelizacion.PRODUCTO).producto(producto).puntos(1).activo(true).build();
                    return reglaRepository.save(nueva);
                });
                int puntos = regla.getPuntos() * detalle.getCantidad();
                tarjeta.setProgreso(tarjeta.getProgreso() + puntos);
                tarjetaRepository.save(tarjeta);
                evaluarMeta(tarjeta);
                movimientoService.registrarMovimiento(tarjeta, OrigenFidelizacion.VENTA, venta.getVentaId().longValue(), puntos, "Producto: " + producto.getNombre());

            }
            if (detalle.getServicio() != null) {
                Servicio servicio = detalle.getServicio();
                Categoria categoriaFidelizacion = categoriaResolver.resolver(servicio.getCategoria());
                if (categoriaFidelizacion == null) {continue;}
                Long categoriaId = categoriaFidelizacion.getId();

                FidelizacionTarjeta tarjeta = tarjetaRepository.findByClienteClienteIdAndCategoriaId(venta.getCliente().getClienteId(), categoriaId).orElse(null);
                if (tarjeta == null || !Boolean.TRUE.equals(tarjeta.getActivo())) {continue;}
                FidelizacionRegla regla = reglaRepository.findByCategoriaIdAndServicioServicioIdAndActivoTrue(categoriaId, servicio.getServicioId()).orElseGet(() -> {
                    FidelizacionRegla nueva = FidelizacionRegla.builder().categoria(categoriaFidelizacion).tipoAlcance(TipoAlcanceFidelizacion.SERVICIO).servicio(servicio).puntos(1).activo(true).build();return reglaRepository.save(nueva);
                });
                int puntos = regla.getPuntos() * detalle.getCantidad();
                tarjeta.setProgreso(tarjeta.getProgreso() + puntos);
                tarjetaRepository.save(tarjeta);
                evaluarMeta(tarjeta);
                movimientoService.registrarMovimiento(tarjeta, OrigenFidelizacion.VENTA, venta.getVentaId().longValue(), puntos, "Servicio: " + servicio.getNombre());
            }
        }
    }

    @Override
    public void evaluarMeta(FidelizacionTarjeta tarjeta) {
        FidelizacionConfiguracion config = configuracionRepository.findByCategoriaIdAndActivaTrue(tarjeta.getCategoria().getId().intValue()).orElse(null);
        if (config == null) {return;}
        while (tarjeta.getProgreso() >= config.getMeta()) {
            tarjeta.setProgreso(tarjeta.getProgreso() - config.getMeta());
            tarjeta.setGirosDisponibles(tarjeta.getGirosDisponibles() + config.getGirosPorMeta());
            tarjeta.setTotalGiros(tarjeta.getTotalGiros() + config.getGirosPorMeta());
        }
        tarjetaRepository.save(tarjeta);
    }

    @Override
    @Transactional
    public void consumirGiro(FidelizacionTarjeta tarjeta) {
        if (!Boolean.TRUE.equals(tarjeta.getActivo())) {
            throw new BusinessException("La tarjeta está inactiva.", HttpStatus.BAD_REQUEST);
        }
        if (tarjeta.getGirosDisponibles() <= 0) {
            throw new BusinessException("La tarjeta no posee giros disponibles.", HttpStatus.BAD_REQUEST);
        }
        tarjeta.setGirosDisponibles(tarjeta.getGirosDisponibles() - 1);
        tarjetaRepository.save(tarjeta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FidelizacionTarjeta> obtenerTarjetasPorCliente(Integer clienteId) {
        return tarjetaRepository.findByClienteClienteId(clienteId);
    }

    @Override
    @Transactional
    public void crearTarjetasParaCategoria(Categoria categoria) {
        List<Cliente> clientes = clienteRepository.findAll();
        for (Cliente cliente : clientes) {
            if (tarjetaRepository.existsByClienteClienteIdAndCategoriaId(cliente.getClienteId(), categoria.getId())) {continue;}
            FidelizacionTarjeta tarjeta = FidelizacionTarjeta.builder().cliente(cliente).categoria(categoria).progreso(0).girosDisponibles(0).totalGiros(0).activo(true).cicloActivo(true).build();
            tarjetaRepository.save(tarjeta);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<FidelizacionTarjetaResponseDTO> obtenerMisTarjetas() {
        Usuario usuario = usuarioSecurityService.getUsuarioLogueado();
        Cliente cliente = clienteRepository.findByUsuarioId(usuario.getIdUsuario()).orElseThrow(() -> new BusinessException("Cliente no encontrado.", HttpStatus.NOT_FOUND));
        List<FidelizacionTarjetaResponseDTO> dtos = tarjetaRepository.findByClienteClienteId(cliente.getClienteId()).stream().map(tarjetaMapper::toResponse).toList();
        enriquecerConMeta(dtos);
        return dtos;
    }

    @Override
    @Transactional(readOnly = true)
    public FidelizacionTarjetaResponseDTO obtenerMiTarjeta(Long id) {
        Usuario usuario = usuarioSecurityService.getUsuarioLogueado();
        Cliente cliente = clienteRepository.findByUsuarioId(usuario.getIdUsuario()).orElseThrow(() -> new BusinessException("Cliente no encontrado para el usuario autenticado.", HttpStatus.NOT_FOUND));
        FidelizacionTarjeta tarjeta = tarjetaRepository.findById(id).orElseThrow(() -> new BusinessException("Tarjeta no encontrada.", HttpStatus.NOT_FOUND));
        if (!tarjeta.getCliente().getClienteId().equals(cliente.getClienteId())) {
            throw new BusinessException("No tiene permiso para acceder a esta tarjeta.", HttpStatus.FORBIDDEN);
        }
        if (!Boolean.TRUE.equals(tarjeta.getActivo())) {
            throw new BusinessException("La tarjeta está inactiva.", HttpStatus.BAD_REQUEST);
        }
        FidelizacionTarjetaResponseDTO dto = tarjetaMapper.toResponse(tarjeta);
        enriquecerConMeta(dto);
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public FidelizacionTarjeta obtenerTarjetaConGiroDisponible() {
        Usuario usuario = usuarioSecurityService.getUsuarioLogueado();
        Cliente cliente = clienteRepository.findByUsuarioId(usuario.getIdUsuario()).orElseThrow(() -> new BusinessException("Cliente no encontrado para el usuario autenticado.", HttpStatus.NOT_FOUND));
        List<FidelizacionTarjeta> tarjetas = tarjetaRepository.findByClienteClienteId(cliente.getClienteId());
        return tarjetas.stream().filter(t -> Boolean.TRUE.equals(t.getActivo())).filter(t -> Boolean.TRUE.equals(t.getCicloActivo())).filter(t -> t.getGirosDisponibles() > 0)
                .findFirst().orElseThrow(() -> new BusinessException("No posee ninguna tarjeta con giros disponibles.", HttpStatus.BAD_REQUEST));
    }

    @Override
    @Transactional(readOnly = true)
    public FidelizacionTarjeta obtenerMiTarjetaConGiro(Long tarjetaId) {
        Usuario usuario = usuarioSecurityService.getUsuarioLogueado();
        Cliente cliente = clienteRepository.findByUsuarioId(usuario.getIdUsuario()).orElseThrow(() -> new BusinessException("Cliente no encontrado para el usuario autenticado.", HttpStatus.NOT_FOUND));
        FidelizacionTarjeta tarjeta = tarjetaRepository.findById(tarjetaId).orElseThrow(() -> new BusinessException("Tarjeta no encontrada.", HttpStatus.NOT_FOUND));
        if (!tarjeta.getCliente().getClienteId().equals(cliente.getClienteId())) {
            throw new BusinessException("No tiene permiso para acceder a esta tarjeta.", HttpStatus.FORBIDDEN);
        }
        if (!Boolean.TRUE.equals(tarjeta.getActivo())) {
            throw new BusinessException("La tarjeta está inactiva.", HttpStatus.BAD_REQUEST);
        }
        if (!Boolean.TRUE.equals(tarjeta.getCicloActivo())) {
            throw new BusinessException("La tarjeta no tiene un ciclo activo.", HttpStatus.BAD_REQUEST);
        }
        if (tarjeta.getGirosDisponibles() <= 0) {
            throw new BusinessException("La tarjeta no posee giros disponibles.", HttpStatus.BAD_REQUEST);
        }
        return tarjeta;
    }

    private Cliente obtenerCliente(Integer id) {
        return clienteRepository.findById(id).orElseThrow(() -> new BusinessException("Cliente no encontrado", HttpStatus.NOT_FOUND));
    }

    private Categoria obtenerCategoria(Long id) {
        return categoriaRepository.findById(id).orElseThrow(() -> new BusinessException("Categoría no encontrada", HttpStatus.NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public Integer contarTarjetas() {
        return Math.toIntExact(tarjetaRepository.count());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TarjetasPorCategoriaResponseDTO> obtenerTarjetasPorCategoria() {
        return tarjetaRepository.obtenerTarjetasPorCategoria().stream().map(r -> TarjetasPorCategoriaResponseDTO.builder()
                        .categoriaId((Long) r[0]).categoriaNombre((String) r[1]).totalTarjetas(((Long) r[2]).intValue()).tarjetasConGiroDisponible(((Long) r[3]).intValue())
                        .girosDisponibles(((Long) r[4]).intValue()).build()).toList();
    }

    @Override
    @Transactional
    public FidelizacionTarjetaResponseDTO actualizarParcial(Long id, FidelizacionTarjetaPatchRequestDTO dto) {
        FidelizacionTarjeta tarjeta = tarjetaRepository.findById(id).orElseThrow(() -> new BusinessException("Tarjeta no encontrada", HttpStatus.NOT_FOUND));
        System.out.println("ANTES: " + tarjeta.getActivo());
        switch (dto.getCampo()) {
            case "activo" -> tarjeta.setActivo((Boolean) dto.getValor());
            case "cicloActivo" -> tarjeta.setCicloActivo((Boolean) dto.getValor());
            default -> throw new BusinessException("Campo no permitido para actualización.", HttpStatus.BAD_REQUEST);
        }
        tarjeta = tarjetaRepository.save(tarjeta);
        FidelizacionTarjetaResponseDTO response = tarjetaMapper.toResponse(tarjeta);
        enriquecerConMeta(response);
        return response;
    }


    private void enriquecerConMeta(List<FidelizacionTarjetaResponseDTO> dtos) {
        if (dtos.isEmpty()) return;
        List<Long> categoriaIds = dtos.stream().map(FidelizacionTarjetaResponseDTO::getCategoriaId).distinct().toList();
        java.util.Map<Long, FidelizacionConfiguracion> configPorCategoria = configuracionRepository.findByCategoriaIdIn(categoriaIds).stream().collect(java.util.stream.Collectors.toMap(c -> c.getCategoria().getId(), c -> c));
        for (FidelizacionTarjetaResponseDTO dto : dtos) {
            FidelizacionConfiguracion config = configPorCategoria.get(dto.getCategoriaId());
            if (config != null) {
                dto.setMeta(config.getMeta());
                dto.setGirosPorMeta(config.getGirosPorMeta());
            }
        }
    }

    private void enriquecerConMeta(FidelizacionTarjetaResponseDTO dto) {
        enriquecerConMeta(List.of(dto));
    }
}