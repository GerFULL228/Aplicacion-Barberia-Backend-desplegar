package com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.service.impl;

import com.sistemabarberia.fadex_backend.commons.exception.BusinessException;
import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.categoria.entity.Categoria;
import com.sistemabarberia.fadex_backend.modules.categoria.repository.CategoriaRepository;
import com.sistemabarberia.fadex_backend.modules.cliente.entity.Cliente;
import com.sistemabarberia.fadex_backend.modules.cliente.repository.ClienteRepository;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.dto.FidelizacionTarjetaFiltro;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.dto.request.FidelizacionTarjetaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.dto.response.FidelizacionTarjetaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.entity.FidelizacionTarjeta;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.mapper.FidelizacionTarjetaMapper;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.repository.FidelizacionTarjetaRepository;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.service.IFidelizacionTarjetaService;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.specs.FidelizacionTarjetaSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FidelizacionTarjetaServiceImpl implements IFidelizacionTarjetaService {

    private final FidelizacionTarjetaRepository tarjetaRepository;
    private final ClienteRepository clienteRepository;
    private final CategoriaRepository categoriaRepository;
    private final FidelizacionTarjetaMapper tarjetaMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<FidelizacionTarjetaResponseDTO> listarTarjetas(FidelizacionTarjetaFiltro filtro, Pageable pageable) {
        Page<FidelizacionTarjeta> page = tarjetaRepository.findAll(FidelizacionTarjetaSpecification.conFiltros(filtro), pageable);
        return PageResponse.of(page.map(tarjetaMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public FidelizacionTarjetaResponseDTO obtenerTarjetaPorId(Long id) {
        FidelizacionTarjeta tarjeta = tarjetaRepository.findById(id).orElseThrow(() -> new BusinessException("Tarjeta no encontrada", HttpStatus.NOT_FOUND));
        return tarjetaMapper.toResponse(tarjeta);
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
        return tarjetaMapper.toResponse(tarjetaRepository.save(tarjeta));
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
        return tarjetaMapper.toResponse(tarjetaRepository.save(tarjeta));
    }

    @Override
    @Transactional
    public void eliminarTarjeta(Long id) {
        FidelizacionTarjeta tarjeta = tarjetaRepository.findById(id).orElseThrow(() -> new BusinessException("Tarjeta no encontrada", HttpStatus.NOT_FOUND));
        tarjetaRepository.delete(tarjeta);
    }

    private Cliente obtenerCliente(Integer id) {
        return clienteRepository.findById(id).orElseThrow(() -> new BusinessException("Cliente no encontrado", HttpStatus.NOT_FOUND));
    }

    private Categoria obtenerCategoria(Long id) {
        return categoriaRepository.findById(id).orElseThrow(() -> new BusinessException("Categoría no encontrada", HttpStatus.NOT_FOUND));
    }
}