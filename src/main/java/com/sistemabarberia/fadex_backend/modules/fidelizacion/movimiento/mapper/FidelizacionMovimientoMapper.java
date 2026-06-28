package com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.mapper;

import com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.dto.request.FidelizacionMovimientoRequestDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.dto.response.FidelizacionMovimientoResponseDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.entity.FidelizacionMovimiento;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FidelizacionMovimientoMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "tarjeta", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    FidelizacionMovimiento toEntity(FidelizacionMovimientoRequestDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "tarjeta", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    void updateFromRequest(FidelizacionMovimientoRequestDTO dto, @MappingTarget FidelizacionMovimiento entity);

    @Mapping(target = "tarjetaId", source = "tarjeta.id")
    @Mapping(target = "clienteId", source = "cliente.clienteId")
    @Mapping(target = "clienteNombre", expression = "java(entity.getCliente().getPersona().getNombre() + \" \" + entity.getCliente().getPersona().getApellido())")
    FidelizacionMovimientoResponseDTO toResponse(FidelizacionMovimiento entity);

    List<FidelizacionMovimientoResponseDTO> toResponseList(List<FidelizacionMovimiento> entities);
}