package com.sistemabarberia.fadex_backend.modules.ruleta.categoria.mapper;

import com.sistemabarberia.fadex_backend.modules.ruleta.categoria.dto.request.RuletaCategoriaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.categoria.dto.response.RuletaCategoriaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.categoria.entity.RuletaCategoria;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RuletaCategoriaMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ruleta", ignore = true)
    @Mapping(target = "categoria", ignore = true)
    RuletaCategoria toEntity(RuletaCategoriaRequestDTO dto);

    @Mapping(source = "ruleta.ruletaId", target = "idRuleta")
    @Mapping(source = "ruleta.nombre", target = "nombreRuleta")
    @Mapping(source = "categoria.id", target = "idCategoria")
    @Mapping(source = "categoria.nombre", target = "nombreCategoria")
    RuletaCategoriaResponseDTO toResponse(RuletaCategoria entity);
}