package com.sistemabarberia.fadex_backend.modules.servicio.mapper;

import com.sistemabarberia.fadex_backend.modules.categoria.entity.Categoria;

import com.sistemabarberia.fadex_backend.modules.servicio.dto.request.ServicioRequestDTO;

import com.sistemabarberia.fadex_backend.modules.servicio.dto.response.ServicioResponseDTO;

import com.sistemabarberia.fadex_backend.modules.servicio.entity.Servicio;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ServicioMapper {

    @Mapping(target = "servicioId", ignore = true)
    @Mapping(target = "categoria", expression = "java(mapCategoria(dto.getCategoriaId()))")
    Servicio toEntity(ServicioRequestDTO dto);

    @Mapping(source = "categoria.id", target = "categoriaId")
    @Mapping(source = "categoria.nombre", target = "categoriaNombre")
    ServicioResponseDTO toResponse(Servicio servicio);

    List<ServicioResponseDTO> toResponseList(List<Servicio> cortes);

    default Categoria mapCategoria(Long id) {
        if (id == null) return null;
        Categoria categoria = new Categoria();
        categoria.setId(id);
        return categoria;
    }
}