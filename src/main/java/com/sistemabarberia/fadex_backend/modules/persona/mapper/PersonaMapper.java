package com.sistemabarberia.fadex_backend.modules.persona.mapper;


import com.sistemabarberia.fadex_backend.modules.persona.dto.request.PersonaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.persona.dto.response.PersonaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.persona.entity.Persona;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface PersonaMapper {

    //Listar
    @Mapping(source = "personaId", target = "personaId")
    @Mapping(source = "usuarioId", target = "usuarioId")
    PersonaResponseDTO toResponseDTO(Persona persona);

    //Registrar
    @Mapping(target = "personaId", ignore = true)
    @Mapping(source = "usuarioId", target = "usuarioId")
    Persona toEntity(PersonaRequestDTO dto);

}
