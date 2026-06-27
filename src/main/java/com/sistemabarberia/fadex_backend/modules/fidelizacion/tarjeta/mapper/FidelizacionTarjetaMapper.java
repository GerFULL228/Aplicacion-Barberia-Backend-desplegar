package com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.mapper;

import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.dto.request.FidelizacionTarjetaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.dto.response.FidelizacionTarjetaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.entity.FidelizacionTarjeta;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FidelizacionTarjetaMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "categoria", ignore = true)
    FidelizacionTarjeta toEntity(FidelizacionTarjetaRequestDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "categoria", ignore = true)
    void updateFromRequest(FidelizacionTarjetaRequestDTO dto, @MappingTarget FidelizacionTarjeta entity);

    @Mapping(target = "clienteId", source = "cliente.clienteId")
    @Mapping(target = "clienteNombre", source = "cliente.persona.nombre")
    @Mapping(target = "categoriaId", source = "categoria.id")
    @Mapping(target = "categoriaNombre", source = "categoria.nombre")
    FidelizacionTarjetaResponseDTO toResponse(FidelizacionTarjeta entity);

    List<FidelizacionTarjetaResponseDTO> toResponseList(List<FidelizacionTarjeta> entities);
}