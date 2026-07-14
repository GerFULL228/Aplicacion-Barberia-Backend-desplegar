package com.sistemabarberia.fadex_backend.modules.fidelizacion.regla.mapper;

import com.sistemabarberia.fadex_backend.modules.fidelizacion.regla.dto.request.FidelizacionReglaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.regla.dto.response.FidelizacionReglaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.regla.entity.FidelizacionRegla;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface FidelizacionReglaMapper {

    @BeanMapping(ignoreByDefault = false, unmappedTargetPolicy = ReportingPolicy.IGNORE)
    FidelizacionRegla toEntity(FidelizacionReglaRequestDTO dto);

    @BeanMapping(ignoreByDefault = false, unmappedTargetPolicy = ReportingPolicy.IGNORE)
    void updateFromRequest(FidelizacionReglaRequestDTO dto, @MappingTarget FidelizacionRegla entity);

    @Mapping(target = "categoriaId", source = "categoria.id")
    @Mapping(target = "categoriaNombre", source = "categoria.nombre")
    @Mapping(target = "servicioId", source = "servicio.servicioId")
    @Mapping(target = "servicioNombre", source = "servicio.nombre")
    @Mapping(target = "productoId", source = "producto.id")
    @Mapping(target = "productoNombre", source = "producto.nombre")
    FidelizacionReglaResponseDTO toResponse(FidelizacionRegla entity);
}