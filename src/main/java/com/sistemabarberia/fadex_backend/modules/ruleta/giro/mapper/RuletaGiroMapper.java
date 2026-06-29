package com.sistemabarberia.fadex_backend.modules.ruleta.giro.mapper;

import com.sistemabarberia.fadex_backend.modules.ruleta.giro.dto.request.RuletaGiroRequestDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.giro.dto.response.RuletaGiroResponseDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.giro.entity.RuletaGiro;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RuletaGiroMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "tarjeta", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "ruleta", ignore = true)
    @Mapping(target = "item", ignore = true)
    RuletaGiro toEntity(RuletaGiroRequestDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "tarjeta", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "ruleta", ignore = true)
    @Mapping(target = "item", ignore = true)
    void updateFromRequest(RuletaGiroRequestDTO dto, @MappingTarget RuletaGiro entity);

    @Mapping(target = "tarjetaId", source = "tarjeta.id")
    @Mapping(target = "clienteId", source = "cliente.clienteId")
    @Mapping(target = "clienteNombre", source = "cliente.persona.nombre")
    @Mapping(target = "ruletaId", source = "ruleta.ruletaId")
    @Mapping(target = "ruletaNombre", source = "ruleta.nombre")
    @Mapping(target = "fecha", source = "createdAt")
    @Mapping(target = "itemId", source = "item.itemId")
    @Mapping(target = "premio", source = "item.nombre")
    RuletaGiroResponseDTO toResponse(RuletaGiro entity);

    List<RuletaGiroResponseDTO> toResponseList(List<RuletaGiro> entities);
}