package com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.mapper;

import com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.dto.response.RuletaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.entity.Ruleta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RuletaMapper {

    @Mapping(target = "ruletaId", source = "ruletaId")
    RuletaResponseDTO toResponse(Ruleta entity);

}