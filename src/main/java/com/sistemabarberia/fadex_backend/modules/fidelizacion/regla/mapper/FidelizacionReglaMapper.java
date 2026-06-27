package com.sistemabarberia.fadex_backend.modules.fidelizacion.regla.mapper;

import com.sistemabarberia.fadex_backend.modules.fidelizacion.regla.dto.request.FidelizacionReglaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.regla.dto.response.FidelizacionReglaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.regla.entity.FidelizacionRegla;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FidelizacionReglaMapper {
    FidelizacionRegla toEntity(FidelizacionReglaRequestDTO dto);
    void updateFromRequest(FidelizacionReglaRequestDTO dto, @MappingTarget FidelizacionRegla entity);

    @Mapping(source = "categoria.categoriaId", target = "categoriaId")
    @Mapping(source = "servicio.servicioId", target = "servicioId")
    @Mapping(source = "producto.id", target = "productoId")
    FidelizacionReglaResponseDTO toResponse(FidelizacionRegla entity);
}