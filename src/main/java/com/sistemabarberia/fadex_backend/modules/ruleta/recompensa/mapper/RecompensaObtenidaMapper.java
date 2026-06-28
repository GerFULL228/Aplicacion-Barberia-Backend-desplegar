package com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.mapper;

import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.dto.request.RecompensaObtenidaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.dto.response.RecompensaObtenidaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.entity.RecompensaObtenida;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RecompensaObtenidaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "fechaCanje", ignore = true)
    @Mapping(target = "giro", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "item", ignore = true)
    @Mapping(target = "usuarioCanje", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "fechaObtencion", ignore = true)
    RecompensaObtenida toEntity(RecompensaObtenidaRequestDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "fechaCanje", ignore = true)
    @Mapping(target = "giro", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "item", ignore = true)
    @Mapping(target = "usuarioCanje", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "fechaObtencion", ignore = true)
    void updateFromRequest(RecompensaObtenidaRequestDTO dto, @MappingTarget RecompensaObtenida entity);

    @Mapping(target = "giroId", source = "giro.id")
    @Mapping(target = "clienteId", source = "cliente.clienteId")
    @Mapping(target = "clienteNombre", source = "cliente.persona.nombre")
    @Mapping(target = "itemId", source = "item.itemId")
    @Mapping(target = "itemNombre", source = "item.nombre")
    @Mapping(target = "usuarioCanjeId", source = "usuarioCanje.idUsuario")
    RecompensaObtenidaResponseDTO toResponse(RecompensaObtenida entity);

    List<RecompensaObtenidaResponseDTO> toResponseList(List<RecompensaObtenida> entities);
}