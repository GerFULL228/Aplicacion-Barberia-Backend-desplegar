package com.sistemabarberia.fadex_backend.modules.barbero.mapper;

import com.sistemabarberia.fadex_backend.modules.barbero.dto.request.BarberoRequestDTO;
import com.sistemabarberia.fadex_backend.modules.barbero.dto.response.BarberoResponseDTO;
import com.sistemabarberia.fadex_backend.modules.barbero.entity.Barbero;
import com.sistemabarberia.fadex_backend.modules.persona.entity.Persona;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface BarberoMapper {

    //Listar
    @Mapping(source = "barberoId", target = "barberoId")
    @Mapping(source = "persona.nombre", target = "nombre")
    @Mapping(source = "persona.apellido", target = "apellido")
    @Mapping(source = "persona.telefono", target = "telefono")
    @Mapping(source = "persona.email", target = "email")
    BarberoResponseDTO toResponseDTO(Barbero barbero);

    //Registrar
    @Mapping(source = "persona", target = "persona")
    @Mapping(target = "barberoId", ignore = true)
    Barbero toEntity(BarberoRequestDTO dto, Persona persona);

}
