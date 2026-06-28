package com.sistemabarberia.fadex_backend.modules.ruleta.item.mapper;

import com.sistemabarberia.fadex_backend.modules.ruleta.item.dto.request.RuletaItemRequestDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.item.dto.response.RuletaItemResponseDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.item.entity.RuletaItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RuletaItemMapper {

        @Mapping(target = "itemId", ignore = true)
        @Mapping(target = "ruleta", ignore = true)
        @Mapping(target = "producto", ignore = true)
        @Mapping(target = "servicio", ignore = true)
        @Mapping(target = "createdAt", ignore = true)
        @Mapping(target = "updatedAt", ignore = true)
        RuletaItem toEntity(RuletaItemRequestDTO dto);

        @Mapping(target = "itemId", ignore = true)
        @Mapping(target = "ruleta", ignore = true)
        @Mapping(target = "producto", ignore = true)
        @Mapping(target = "servicio", ignore = true)
        @Mapping(target = "createdAt", ignore = true)
        @Mapping(target = "updatedAt", ignore = true)
        void updateFromRequest(RuletaItemRequestDTO dto, @MappingTarget RuletaItem entity);

    @Mapping(source = "ruleta.ruletaId", target = "ruletaId")
    @Mapping(source = "producto.id", target = "productoId")
    @Mapping(source = "servicio.servicioId", target = "servicioId")
    RuletaItemResponseDTO toResponse(RuletaItem entity);
}