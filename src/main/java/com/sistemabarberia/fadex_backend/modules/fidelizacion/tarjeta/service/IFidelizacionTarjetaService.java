package com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.service;

import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.categoria.entity.Categoria;
import com.sistemabarberia.fadex_backend.modules.cliente.entity.Cliente;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.dto.FidelizacionTarjetaFiltro;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.dto.request.FidelizacionTarjetaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.dto.response.FidelizacionTarjetaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.entity.FidelizacionTarjeta;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.Reserva;
import com.sistemabarberia.fadex_backend.modules.venta.entity.Venta;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IFidelizacionTarjetaService {
    PageResponse<FidelizacionTarjetaResponseDTO> listarTarjetas(FidelizacionTarjetaFiltro filtro, Pageable pageable);
    FidelizacionTarjetaResponseDTO obtenerTarjetaPorId(Long id);
    FidelizacionTarjetaResponseDTO crearTarjeta(FidelizacionTarjetaRequestDTO dto);
    FidelizacionTarjetaResponseDTO actualizarTarjeta(Long id, FidelizacionTarjetaRequestDTO dto);
    void eliminarTarjeta(Long id);
    void crearTarjetasIniciales(Cliente cliente);
    void acumularPorServicio(Reserva reserva);
    void acumularPorVenta(Venta venta);
    void consumirGiro(FidelizacionTarjeta tarjeta);
    List<FidelizacionTarjeta> obtenerTarjetasPorCliente(Integer clienteId);
    void evaluarMeta(FidelizacionTarjeta tarjeta);
    void crearTarjetasParaCategoria(Categoria categoria);
    List<FidelizacionTarjetaResponseDTO> obtenerMisTarjetas();
    FidelizacionTarjetaResponseDTO obtenerMiTarjeta(Long tarjetaId);
    FidelizacionTarjeta obtenerTarjetaConGiroDisponible();
    FidelizacionTarjeta obtenerMiTarjetaConGiro(Long tarjetaId);
}
