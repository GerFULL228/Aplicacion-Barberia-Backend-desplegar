package com.sistemabarberia.fadex_backend.modules.producto.mapper;

import com.sistemabarberia.fadex_backend.modules.producto.dto.request.ProductoRequest;
import com.sistemabarberia.fadex_backend.modules.producto.dto.response.ProductoResponse;
import com.sistemabarberia.fadex_backend.modules.producto.entity.Producto;
import com.sistemabarberia.fadex_backend.modules.producto.entity.ProductoImagen;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class ProductoMapper {

    @Value("${app.base-url}")
    protected String baseUrl;

    @Value("${app.upload.dir}")
    protected String uploadDir;

    @Mapping(target = "idCategoria", source = "categoria.id")
    @Mapping(target = "nombreCategoria", source = "categoria.nombre")
    @Mapping(target = "imagenes", source = "imagenes", qualifiedByName = "mapImagenes")
    public abstract ProductoResponse toResponse(Producto producto);

    @Named("mapImagenes")
    protected List<String> mapImagenes(List<ProductoImagen> imagenes) {
        if (imagenes == null) return List.of();

        return imagenes.stream()
                .map(ProductoImagen::getUrl)
                .toList();
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categoria", ignore = true)
    @Mapping(target = "imagenes", ignore = true)
    public abstract Producto toEntity(ProductoRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categoria", ignore = true)
    @Mapping(target = "imagenes", ignore = true)
    public abstract void updateFromRequest(ProductoRequest request, @MappingTarget Producto producto);
}