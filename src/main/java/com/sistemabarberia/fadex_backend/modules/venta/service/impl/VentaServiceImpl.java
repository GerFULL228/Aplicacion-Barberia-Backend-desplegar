package com.sistemabarberia.fadex_backend.modules.venta.service.impl;

import com.sistemabarberia.fadex_backend.commons.exception.ResourceNotFoundException;
import com.sistemabarberia.fadex_backend.modules.barbero.entity.Barbero;
import com.sistemabarberia.fadex_backend.modules.barbero.repository.BarberoRepository;
import com.sistemabarberia.fadex_backend.modules.cliente.entity.Cliente;
import com.sistemabarberia.fadex_backend.modules.cliente.repository.ClienteRepository;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.engine.service.IFidelizacionEngine;
import com.sistemabarberia.fadex_backend.modules.producto.entity.Producto;
import com.sistemabarberia.fadex_backend.modules.producto.repository.ProductoRepository;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.service.IRecompensaObtenidaService;
import com.sistemabarberia.fadex_backend.modules.pagos.entity.Pago;
import com.sistemabarberia.fadex_backend.modules.pagos.entity.TipoPago;
import com.sistemabarberia.fadex_backend.modules.pagos.repository.PagoRepository;
import com.sistemabarberia.fadex_backend.modules.venta.dto.request.VentaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.venta.dto.request.DetalleVentaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.venta.dto.response.*;
import com.sistemabarberia.fadex_backend.modules.venta.entity.*;
import com.sistemabarberia.fadex_backend.modules.venta.mapper.VentaMapper;
import com.sistemabarberia.fadex_backend.modules.venta.mapper.DetalleVentaMapper;
import com.sistemabarberia.fadex_backend.modules.venta.mapper.HistorialVentaMapper;
import com.sistemabarberia.fadex_backend.modules.venta.repository.*;
import com.sistemabarberia.fadex_backend.modules.venta.service.IVentaService;

import com.sistemabarberia.fadex_backend.modules.reserva.repository.ReservaRepository;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.Reserva;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.EstadoReserva;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VentaServiceImpl implements IVentaService {

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final HistorialVentaRepository historialVentaRepository;
    private final BarberoRepository barberoRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final PagoRepository pagoRepository;
    private final ReservaRepository reservaRepository;

    private final VentaMapper ventaMapper;
    private final DetalleVentaMapper detalleVentaMapper;
    private final HistorialVentaMapper historialVentaMapper;
    private final IFidelizacionEngine fidelizacionEngine;
    private final IRecompensaObtenidaService recompensaObtenidaService;

    @Override
    @Transactional
    public VentaResponseDTO crear(VentaRequestDTO dto) {

        Cliente cliente;
        Barbero barbero = null;
        Reserva reservaVinculada = null;
        List<DetalleVenta> detalles = new ArrayList<>();

        if (dto.getBarberoId() != null) {
            barbero = barberoRepository.findById(dto.getBarberoId()).orElseThrow(() -> new ResourceNotFoundException("Barbero no encontrado"));
        }

        Venta venta = new Venta();
        venta.setBarbero(barbero);
        venta.setFecha(dto.getFecha() != null ? dto.getFecha() : LocalDateTime.now());
        venta.setTipoComprobante(dto.getTipoComprobante());
        venta.setNumeroCorrelativo(generarCorrelativo(venta.getFecha()));

        if (dto.getReservaId() != null) {
            reservaVinculada = reservaRepository.findById(dto.getReservaId()).orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));
            cliente = reservaVinculada.getCliente();

            DetalleVenta detalleServicio = new DetalleVenta();
            detalleServicio.setVenta(venta);
            detalleServicio.setServicio(reservaVinculada.getServicio());
            detalleServicio.setCantidad(1);
            detalleServicio.setPrecioUnitario(reservaVinculada.getTotal());
            detalles.add(detalleServicio);
            reservaVinculada.setEstadoReserva(EstadoReserva.FINALIZADA);
            reservaRepository.save(reservaVinculada);

        } else {
            if (dto.getClienteId() == null) {
                throw new IllegalArgumentException("El cliente es obligatorio para ventas libres.");
            }
            cliente = clienteRepository.findById(dto.getClienteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
        }

        venta.setCliente(cliente);
        if (dto.getDetalles() != null && !dto.getDetalles().isEmpty()) {
            for (DetalleVentaRequestDTO detDto : dto.getDetalles()) {
                if (detDto.getProductoId() != null) {
                    DetalleVenta detalleProd = detalleVentaMapper.toEntity(detDto);
                    detalleProd.setVenta(venta);

                    Producto producto = productoRepository.findById(Long.valueOf(detDto.getProductoId()))
                            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

                    if (producto.getStock() < detDto.getCantidad()) {
                        throw new IllegalArgumentException("Stock insuficiente para: " + producto.getNombre());
                    }

                    producto.setStock(producto.getStock() - detDto.getCantidad());
                    productoRepository.save(producto);
                    detalleProd.setProducto(producto);

                    detalles.add(detalleProd);
                }
            }
        }

        venta.setDetalles(detalles);
        Venta ventaGuardada = ventaRepository.save(venta);
        ventaGuardada = ventaRepository.findByIdWithDetalles(ventaGuardada.getVentaId());

        // Recompensas (módulo migrado a tablas nuevas): se aplican antes de calcular el
        // monto total por si el servicio ajusta precios/descuentos sobre los detalles.
        if (dto.getRecompensasAplicadas() != null && !dto.getRecompensasAplicadas().isEmpty()) {
            recompensaObtenidaService.aplicarRecompensas(dto.getRecompensasAplicadas(), ventaGuardada);
        }

        BigDecimal montoTotal = ventaGuardada.getDetalles().stream()
                .map(detalle -> detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(detalle.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Pago pago = new Pago();
        pago.setCliente(cliente);
        pago.setBarbero(barbero);
        pago.setVenta(ventaGuardada);
        pago.setMonto(montoTotal);
        pago.setMetodo(dto.getMetodoPago());
        pago.setFecha(LocalDateTime.now());
        pago.setTipo(TipoPago.VENTA);

        if (reservaVinculada != null) {
            pago.setReserva(reservaVinculada);
        }
        pagoRepository.save(pago);
        HistorialVenta historial = new HistorialVenta();
        historial.setVenta(ventaGuardada);
        historial.setFecha(LocalDateTime.now());
        historialVentaRepository.save(historial);
        Long reservaIdParaFidelizacion = reservaVinculada != null ? reservaVinculada.getId() : null;
        fidelizacionEngine.procesarVenta(ventaGuardada, reservaIdParaFidelizacion);
        VentaResponseDTO response = ventaMapper.toResponse(ventaGuardada);
        response.setMetodoPago(dto.getMetodoPago());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaResponseDTO> buscarConFiltros(String cliente, String numeroCorrelativo, String tipoComprobanteStr, String fechaInicioStr, String fechaFinStr) {
        LocalDateTime fechaInicio = null;
        LocalDateTime fechaFin = null;
        TipoComprobante comprobanteEnum = null;
        if (fechaInicioStr != null && !fechaInicioStr.isEmpty()) {
            fechaInicio = LocalDate.parse(fechaInicioStr.substring(0, 10)).atStartOfDay();
        }
        if (fechaFinStr != null && !fechaFinStr.isEmpty()) {
            fechaFin = LocalDate.parse(fechaFinStr.substring(0, 10)).atTime(23, 59, 59);
        }
        if (tipoComprobanteStr != null && !tipoComprobanteStr.isEmpty()) {
            try {
                comprobanteEnum = TipoComprobante.valueOf(tipoComprobanteStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                comprobanteEnum = null;
            }
        }
        String clienteFiltro = (cliente != null && !cliente.trim().isEmpty()) ? cliente.trim() : null;
        String correlativoFiltro = (numeroCorrelativo != null && !numeroCorrelativo.trim().isEmpty()) ? numeroCorrelativo.trim() : null;
        boolean hasCliente = clienteFiltro != null;
        String valCliente = hasCliente ? clienteFiltro : "";
        boolean hasCorrelativo = correlativoFiltro != null;
        String valCorrelativo = hasCorrelativo ? correlativoFiltro : "";
        boolean hasComprobante = comprobanteEnum != null;
        TipoComprobante valComprobante = hasComprobante ? comprobanteEnum : TipoComprobante.BOLETA;
        boolean hasFechaInicio = fechaInicio != null;
        LocalDateTime valFechaInicio = hasFechaInicio ? fechaInicio : LocalDateTime.now();
        boolean hasFechaFin = fechaFin != null;
        LocalDateTime valFechaFin = hasFechaFin ? fechaFin : LocalDateTime.now();
        List<Venta> ventas = ventaRepository.buscarConFiltrosAvanzados(
                hasCliente, valCliente,
                hasCorrelativo, valCorrelativo,
                hasComprobante, valComprobante,
                hasFechaInicio, valFechaInicio,
                hasFechaFin, valFechaFin
        );

        List<VentaResponseDTO> dtoList = ventaMapper.toResponseList(ventas);
        dtoList.forEach(dto -> {
            pagoRepository.findFirstByVenta_VentaId(dto.getVentaId())
                    .ifPresent(pago -> dto.setMetodoPago(pago.getMetodo()));
        });

        return dtoList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaResponseDTO> listar() {
        return buscarConFiltros(null, null, null, null, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaResponseDTO> listar(String cliente) {
        return buscarConFiltros(cliente, null, null, null, null);
    }

    @Override
    @Transactional(readOnly = true)
    public VentaResponseDTO obtenerPorId(Integer id) {
        Venta venta = Optional.ofNullable(ventaRepository.findByIdWithDetalles(id)).orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada"));
        VentaResponseDTO dto = ventaMapper.toResponse(venta);
        pagoRepository.findFirstByVenta_VentaId(dto.getVentaId()).ifPresent(pago -> dto.setMetodoPago(pago.getMetodo()));
        return dto;
    }

    @Override
    @Transactional
    public VentaResponseDTO actualizar(Integer id, VentaRequestDTO dto) {
        Venta venta = ventaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada"));
        Cliente cliente = clienteRepository.findById(dto.getClienteId()).orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

        venta.setCliente(cliente);
        venta.setFecha(dto.getFecha());
        venta.setTipoComprobante(dto.getTipoComprobante());
        detalleVentaRepository.deleteAll(venta.getDetalles());
        venta.getDetalles().clear();
        for (DetalleVentaRequestDTO detDto : dto.getDetalles()) {
            DetalleVenta detalle = detalleVentaMapper.toEntity(detDto);
            detalle.setVenta(venta);
            venta.getDetalles().add(detalle);
        }
        Venta ventaActualizada = ventaRepository.save(venta);
        ventaActualizada = ventaRepository.findByIdWithDetalles(ventaActualizada.getVentaId());
        HistorialVenta historial = new HistorialVenta();
        historial.setVenta(ventaActualizada);
        historial.setFecha(LocalDateTime.now());
        historialVentaRepository.save(historial);

        VentaResponseDTO response = ventaMapper.toResponse(ventaActualizada);

        pagoRepository.findFirstByVenta_VentaId(ventaActualizada.getVentaId()).ifPresent(pago -> {
            pago.setMetodo(dto.getMetodoPago());
            pagoRepository.save(pago);
            response.setMetodoPago(pago.getMetodo());
        });

        return response;
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        if (!ventaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Venta no encontrada");
        }
        ventaRepository.deleteById(id);
    }

    @Override
    public List<DetalleVentaResponseDTO> listarDetalles(Integer ventaId) {
        return detalleVentaMapper.toResponseList(detalleVentaRepository.findByVenta_VentaId(ventaId));
    }

    @Override
    public List<HistorialVentaResponseDTO> listarHistorial(Integer ventaId) {
        return historialVentaMapper.toResponseList(historialVentaRepository.findByVenta_VentaId(ventaId));
    }

    private String generarCorrelativo(LocalDateTime fecha) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMyyyy");
        String mesAnio = fecha.format(formatter);
        String prefijo = "VEN-" + mesAnio + "-";
        String ultimoCorrelativo = ventaRepository.findUltimoCorrelativoPorMesAnio(prefijo);
        int siguienteNumero = 1;
        if (ultimoCorrelativo != null && !ultimoCorrelativo.isEmpty()) {
            try {
                String strNumero = ultimoCorrelativo.substring(prefijo.length());
                siguienteNumero = Integer.parseInt(strNumero) + 1;
            } catch (NumberFormatException e) {
                siguienteNumero = 1;
            }
        }
        return prefijo + String.format("%04d", siguienteNumero);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaResponseDTO> listarPorBarbero(Integer barberoId) {
        List<Venta> ventas = ventaRepository.findByBarbero_BarberoIdOrderByFechaDesc(barberoId);
        List<VentaResponseDTO> dtoList = ventaMapper.toResponseList(ventas);
        dtoList.forEach(dto -> {
            pagoRepository.findFirstByVenta_VentaId(dto.getVentaId())
                    .ifPresent(pago -> dto.setMetodoPago(pago.getMetodo()));
        });
        return dtoList;
    }
}