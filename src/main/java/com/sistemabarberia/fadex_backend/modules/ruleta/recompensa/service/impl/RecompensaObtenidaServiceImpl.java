package com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.service.impl;

import com.sistemabarberia.fadex_backend.auth.usuario.Entity.Usuario;
import com.sistemabarberia.fadex_backend.auth.usuario.Repository.UsuarioRepository;
import com.sistemabarberia.fadex_backend.auth.usuario.service.UsuarioSecurityService;
import com.sistemabarberia.fadex_backend.commons.exception.BusinessException;
import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.cliente.entity.Cliente;
import com.sistemabarberia.fadex_backend.modules.cliente.repository.ClienteRepository;
import com.sistemabarberia.fadex_backend.modules.cliente.service.IClienteService;
import com.sistemabarberia.fadex_backend.modules.producto.entity.Producto;
import com.sistemabarberia.fadex_backend.modules.producto.service.IProductoService;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.Reserva;
import com.sistemabarberia.fadex_backend.modules.reserva.repository.ReservaRepository;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.dto.RecompensaObtenidaFiltro;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.dto.request.RecompensaObtenidaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.dto.response.RecompensaObtenidaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.entity.RecompensaObtenida;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.entity.enums.EstadoRecompensa;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.mapper.RecompensaObtenidaMapper;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.repository.RecompensaObtenidaRepository;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.service.IRecompensaObtenidaService;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.specs.RecompensaObtenidaSpecification;
import com.sistemabarberia.fadex_backend.modules.ruleta.giro.entity.RuletaGiro;
import com.sistemabarberia.fadex_backend.modules.ruleta.giro.repository.RuletaGiroRepository;
import com.sistemabarberia.fadex_backend.modules.ruleta.item.entity.RuletaItem;
import com.sistemabarberia.fadex_backend.modules.ruleta.item.repository.RuletaItemRepository;
import com.sistemabarberia.fadex_backend.modules.servicio.entity.Servicio;
import com.sistemabarberia.fadex_backend.modules.venta.entity.Venta;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecompensaObtenidaServiceImpl implements IRecompensaObtenidaService {

    private final RecompensaObtenidaRepository recompensaObtenidaRepository;
    private final RecompensaObtenidaMapper recompensaObtenidaMapper;
    private final RuletaGiroRepository giroRepository;
    private final ClienteRepository clienteRepository;
    private final RuletaItemRepository itemRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioSecurityService usuarioSecurityService;
    private final IClienteService clienteService;
    private final ReservaRepository reservaRepository;
    private final IProductoService productoService;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<RecompensaObtenidaResponseDTO> listarRecompensaConFiltro(RecompensaObtenidaFiltro filtro, Pageable pageable) {
        Page<RecompensaObtenida> page = recompensaObtenidaRepository.findAll(RecompensaObtenidaSpecification.conFiltros(filtro), pageable);
        return PageResponse.of(page.map(recompensaObtenidaMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public RecompensaObtenidaResponseDTO obtenerRecompensaPorId(Long id) {
        RecompensaObtenida entity = recompensaObtenidaRepository.findById(id).orElseThrow(() -> new BusinessException("Recompensa no encontrada", HttpStatus.NOT_FOUND));
        return recompensaObtenidaMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public RecompensaObtenidaResponseDTO crearRecompensa(RecompensaObtenidaRequestDTO dto) {
        RecompensaObtenida entity = recompensaObtenidaMapper.toEntity(dto);
        entity.setGiro(obtenerGiro(dto.getGiroId()));
        entity.setCliente(obtenerCliente(dto.getClienteId()));
        entity.setItem(obtenerItem(dto.getItemId()));
        if (dto.getUsuarioCanjeId() != null) {
            entity.setUsuarioCanje(obtenerUsuario(dto.getUsuarioCanjeId()));
        }
        return recompensaObtenidaMapper.toResponse(recompensaObtenidaRepository.save(entity));
    }

    @Override
    @Transactional
    public RecompensaObtenidaResponseDTO actualizarRecompensa(Long id, RecompensaObtenidaRequestDTO dto) {
        RecompensaObtenida entity = recompensaObtenidaRepository.findById(id).orElseThrow(() -> new BusinessException("Recompensa no encontrada", HttpStatus.NOT_FOUND));
        recompensaObtenidaMapper.updateFromRequest(dto, entity);
        entity.setGiro(obtenerGiro(dto.getGiroId()));
        entity.setCliente(obtenerCliente(dto.getClienteId()));
        entity.setItem(obtenerItem(dto.getItemId()));
        if (dto.getUsuarioCanjeId() != null) {
            entity.setUsuarioCanje(obtenerUsuario(dto.getUsuarioCanjeId()));
        } else {
            entity.setUsuarioCanje(null);
        }
        return recompensaObtenidaMapper.toResponse(recompensaObtenidaRepository.save(entity));
    }

    @Override
    @Transactional
    public void eliminarRecompensa(Long id) {
        RecompensaObtenida entity = recompensaObtenidaRepository.findById(id).orElseThrow(() -> new BusinessException("Recompensa no encontrada", HttpStatus.NOT_FOUND));
        recompensaObtenidaRepository.delete(entity);
    }

    @Override
    @Transactional
    public RecompensaObtenida crearDesdeGiro(RuletaGiro giro, Cliente cliente, RuletaItem item) {
        RecompensaObtenida recompensa = RecompensaObtenida.builder()
                .giro(giro)
                .cliente(cliente)
                .item(item)
                .estado(EstadoRecompensa.PENDIENTE)
                .codigoCanje(generarCodigoCanje())
                .fechaVencimiento(LocalDateTime.now().plusDays(30))
                .build();
        return recompensaObtenidaRepository.save(recompensa);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecompensaObtenidaResponseDTO> obtenerMisRecompensas() {
        Usuario usuario = usuarioSecurityService.getUsuarioLogueado();
        Integer clienteId = clienteService.obtenerIdClientePorUsuario(usuario.getIdUsuario());
        return recompensaObtenidaRepository.findByClienteClienteIdOrderByCreatedAtDesc(clienteId).stream().map(recompensaObtenidaMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RecompensaObtenidaResponseDTO obtenerMiRecompensa(Long id) {
        Usuario usuario = usuarioSecurityService.getUsuarioLogueado();
        Integer clienteId = clienteService.obtenerIdClientePorUsuario(usuario.getIdUsuario());
        RecompensaObtenida recompensa = recompensaObtenidaRepository.findById(id).orElseThrow(() -> new BusinessException("Recompensa no encontrada", HttpStatus.NOT_FOUND));
        if (!recompensa.getCliente().getClienteId().equals(clienteId)) {
            throw new BusinessException("No tiene permiso para acceder a esta recompensa.", HttpStatus.FORBIDDEN);
        }
        return recompensaObtenidaMapper.toResponse(recompensa);
    }

    @Override
    @Transactional
    public RecompensaObtenidaResponseDTO canjearRecompensa(String codigoCanje) {
        Usuario usuario = usuarioSecurityService.getUsuarioLogueado();
        RecompensaObtenida recompensa = recompensaObtenidaRepository.findByCodigoCanje(codigoCanje).orElseThrow(() -> new BusinessException("Código de canje inválido.", HttpStatus.NOT_FOUND));
        if (recompensa.getEstado() != EstadoRecompensa.PENDIENTE) {
            throw new BusinessException("La recompensa ya no puede ser canjeada.", HttpStatus.BAD_REQUEST);
        }
        if (recompensa.getFechaVencimiento() != null && recompensa.getFechaVencimiento().isBefore(LocalDateTime.now())) {
            recompensa.setEstado(EstadoRecompensa.VENCIDO);
            recompensaObtenidaRepository.save(recompensa);
            throw new BusinessException("La recompensa está vencida.", HttpStatus.BAD_REQUEST);
        }
        recompensa.setEstado(EstadoRecompensa.CANJEADO);
        recompensa.setFechaCanje(LocalDateTime.now());
        recompensa.setUsuarioCanje(usuario);
        RecompensaObtenida guardada = recompensaObtenidaRepository.save(recompensa);
        return recompensaObtenidaMapper.toResponse(guardada);
    }

    @Override
    @Transactional
    public void aplicarRecompensas(List<Long> recompensaIds, Venta venta) {
        if (recompensaIds == null || recompensaIds.isEmpty()) {return;}
        Usuario usuario = usuarioSecurityService.getUsuarioLogueado();
        List<RecompensaObtenida> recompensas = recompensaObtenidaRepository.findAllByIdIn(recompensaIds);
        if (recompensas.size() != recompensaIds.size()) {
            throw new BusinessException("Una o más recompensas no existen.", HttpStatus.BAD_REQUEST);
        }
        for (RecompensaObtenida recompensa : recompensas) {
            if (!recompensa.getCliente().getClienteId().equals(venta.getCliente().getClienteId())) {
                throw new BusinessException("La recompensa no pertenece al cliente.", HttpStatus.BAD_REQUEST);
            }
            if (recompensa.getEstado() != EstadoRecompensa.PENDIENTE) {
                throw new BusinessException("La recompensa ya fue utilizada.", HttpStatus.BAD_REQUEST);
            }
            if (recompensa.getFechaVencimiento() != null && recompensa.getFechaVencimiento().isBefore(LocalDateTime.now())) {
                recompensa.setEstado(EstadoRecompensa.VENCIDO);
                recompensaObtenidaRepository.save(recompensa);
                throw new BusinessException("La recompensa está vencida.", HttpStatus.BAD_REQUEST);
            }
            recompensa.setEstado(EstadoRecompensa.CANJEADO);
            recompensa.setFechaCanje(LocalDateTime.now());
            recompensa.setUsuarioCanje(usuario);
            recompensaObtenidaRepository.save(recompensa);
        }
    }

    @Override
    @Transactional
    public Reserva aplicarRecompensas(Reserva reserva, Integer clienteId, List<Long> recompensaIds) {
        if (recompensaIds == null || recompensaIds.isEmpty()) {return reserva;}
        List<RecompensaObtenida> recompensas = recompensaObtenidaRepository.findAllByIdIn(recompensaIds);
        if (recompensas.size() != recompensaIds.size()) {
            throw new BusinessException("Una o más recompensas no existen.", HttpStatus.BAD_REQUEST);
        }
        BigDecimal descuentoTotal = BigDecimal.ZERO;
        Usuario usuario = usuarioSecurityService.getUsuarioLogueado();
        for (RecompensaObtenida recompensa : recompensas) {
            if (!recompensa.getCliente().getClienteId().equals(clienteId)) {
                throw new BusinessException("La recompensa no pertenece al cliente.", HttpStatus.BAD_REQUEST);
            }
            if (recompensa.getEstado() != EstadoRecompensa.PENDIENTE) {
                throw new BusinessException("La recompensa ya fue utilizada.", HttpStatus.BAD_REQUEST);
            }
            if (recompensa.getFechaVencimiento() != null && recompensa.getFechaVencimiento().isBefore(LocalDateTime.now())) {
                recompensa.setEstado(EstadoRecompensa.VENCIDO);
                recompensaObtenidaRepository.save(recompensa);
                throw new BusinessException("La recompensa está vencida.", HttpStatus.BAD_REQUEST);
            }
            RuletaItem item = recompensa.getItem();
            switch (item.getTipoPremio()) {
                case DESCUENTO -> {
                    if (item.getValor() == null) {
                        throw new BusinessException("La recompensa no tiene un valor de descuento.", HttpStatus.BAD_REQUEST);
                    }
                    descuentoTotal = descuentoTotal.add(item.getValor());
                }
                case PRODUCTO -> {
                    if (item.getProducto() == null) {
                        throw new BusinessException("La recompensa no tiene un producto asociado.", HttpStatus.BAD_REQUEST);
                    }
                    productoService.descontarStockPremio(item.getProducto().getId(), 1);
                }
                case SERVICIO -> {
                    if (item.getServicio() == null) {
                        throw new BusinessException("La recompensa no tiene un servicio asociado.", HttpStatus.BAD_REQUEST);
                    }
                    if (!reserva.getServicio().getServicioId().equals(item.getServicio().getServicioId())) {
                        throw new BusinessException("La recompensa solo puede usarse para el servicio " + item.getServicio().getNombre(), HttpStatus.BAD_REQUEST);
                    }
                    reserva.setTotal(BigDecimal.ZERO);
                }
                case CUPON ->{
                    if (item.getProducto() != null) {
                        productoService.descontarStockPremio(item.getProducto().getId(), 1);
                    }

                    if (item.getServicio() != null) {
                        if (!reserva.getServicio().getServicioId().equals(item.getServicio().getServicioId())) {
                            throw new BusinessException("" +
                                    "Este cupón solo aplica al servicio " + item.getServicio().getNombre(), HttpStatus.BAD_REQUEST);
                        }
                        reserva.setTotal(BigDecimal.ZERO);
                    }
                }
            }
            recompensa.setEstado(EstadoRecompensa.CANJEADO);
            recompensa.setFechaCanje(LocalDateTime.now());
            recompensa.setUsuarioCanje(usuario);
            recompensaObtenidaRepository.save(recompensa);
        }
        if (descuentoTotal.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal nuevoTotal = reserva.getTotal().subtract(descuentoTotal);
            if (nuevoTotal.compareTo(BigDecimal.ZERO) < 0) {
                nuevoTotal = BigDecimal.ZERO;
            }
            reserva.setTotal(nuevoTotal);
        }
        return reservaRepository.save(reserva);
    }

    private String generarCodigoCanje() {
        String codigo;
        do {
            int numero = (int) (Math.random() * 900000) + 100000;
            codigo = "REC-" + numero;
        } while (recompensaObtenidaRepository.existsByCodigoCanje(codigo));
        return codigo;
    }

    private RuletaGiro obtenerGiro(Long id) {
        return giroRepository.findById(id).orElseThrow(() -> new BusinessException("Giro no encontrado", HttpStatus.NOT_FOUND));
    }

    private Cliente obtenerCliente(Long id) {
        return clienteRepository.findById(id.intValue()).orElseThrow(() -> new BusinessException("Cliente no encontrado", HttpStatus.NOT_FOUND));
    }

    private RuletaItem obtenerItem(Long id) {
        return itemRepository.findById(id).orElseThrow(() -> new BusinessException("Item no encontrado", HttpStatus.NOT_FOUND));
    }

    private Usuario obtenerUsuario(Long id) {
        return usuarioRepository.findById(id.intValue()).orElseThrow(() -> new BusinessException("Usuario no encontrado", HttpStatus.NOT_FOUND));
    }
}