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

    @Mapping(source = "categoria.id", target = "categoriaId")
    @Mapping(source = "servicio.servicioId", target = "servicioId")
    @Mapping(source = "producto.id", target = "productoId")
    FidelizacionReglaResponseDTO toResponse(FidelizacionRegla entity);
}