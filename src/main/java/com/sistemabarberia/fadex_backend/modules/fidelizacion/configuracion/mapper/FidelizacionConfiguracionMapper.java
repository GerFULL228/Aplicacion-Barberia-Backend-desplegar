package com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.mapper;

import com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.dto.response.ConfiguracionResponseDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.entity.FidelizacionConfiguracion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FidelizacionConfiguracionMapper {
    @Mapping(target = "configuracionId", source = "configuracionId")
    @Mapping(target = "categoriaId", source = "categoria.id")
    @Mapping(target = "categoriaNombre", source = "categoria.nombre")
    @Mapping(target = "ruletaId", source = "ruleta.ruletaId")
    @Mapping(target = "ruletaNombre", source = "ruleta.nombre")
    ConfiguracionResponseDTO toResponse(FidelizacionConfiguracion entity);
}