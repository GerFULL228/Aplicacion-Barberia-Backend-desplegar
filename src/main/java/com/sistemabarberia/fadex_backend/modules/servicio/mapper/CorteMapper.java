package com.sistemabarberia.fadex_backend.modules.servicio.mapper;

import com.sistemabarberia.fadex_backend.modules.categoria.entity.Categoria;
import com.sistemabarberia.fadex_backend.modules.servicio.dto.request.CorteRequestDTO;
import com.sistemabarberia.fadex_backend.modules.servicio.dto.response.CorteResponseDTO;
import com.sistemabarberia.fadex_backend.modules.servicio.entity.Corte;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CorteMapper {

    @Mapping(target = "corteId", ignore = true)
    @Mapping(target = "categoria", expression = "java(mapCategoria(dto.getCategoriaId()))")
    Corte toEntity(CorteRequestDTO dto);

    @Mapping(source = "categoria.id", target = "categoriaId")
    @Mapping(source = "categoria.nombre", target = "categoriaNombre")
    CorteResponseDTO toResponse(Corte corte);

    List<CorteResponseDTO> toResponseList(List<Corte> cortes);

    default Categoria mapCategoria(Long id) {
        if (id == null) return null;
        Categoria categoria = new Categoria();
        categoria.setId(id);
        return categoria;
    }
}