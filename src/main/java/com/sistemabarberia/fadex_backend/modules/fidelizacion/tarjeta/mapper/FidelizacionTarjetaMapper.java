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

    @Mapping(target = "progreso", ignore = true)
    @Mapping(target = "girosDisponibles", ignore = true)
    @Mapping(target = "totalGiros", ignore = true)
    @Mapping(target = "cicloActivo", ignore = true)

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    FidelizacionTarjeta toEntity(FidelizacionTarjetaRequestDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "categoria", ignore = true)

    @Mapping(target = "progreso", ignore = true)
    @Mapping(target = "girosDisponibles", ignore = true)
    @Mapping(target = "totalGiros", ignore = true)
    @Mapping(target = "cicloActivo", ignore = true)

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateFromRequest(FidelizacionTarjetaRequestDTO dto, @MappingTarget FidelizacionTarjeta entity);

    @Mapping(target = "clienteId", source = "cliente.clienteId")
    @Mapping(target = "clienteNombreCompleto", expression = "java((entity.getCliente().getPersona().getNombre() == null ? \"\" : entity.getCliente().getPersona().getNombre()) + \" \" + (entity.getCliente().getPersona().getApellido() == null ? \"\" : entity.getCliente().getPersona().getApellido()))")
    @Mapping(target = "categoriaId", source = "categoria.id")
    @Mapping(target = "categoriaNombre", source = "categoria.nombre")
    FidelizacionTarjetaResponseDTO toResponse(FidelizacionTarjeta entity);

    List<FidelizacionTarjetaResponseDTO> toResponseList(List<FidelizacionTarjeta> entities);
}