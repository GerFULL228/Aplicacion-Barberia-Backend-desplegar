package com.sistemabarberia.fadex_backend.modules.venta.mapper;

import com.sistemabarberia.fadex_backend.modules.venta.dto.request.DetalleVentaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.venta.dto.response.DetalleVentaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.venta.entity.DetalleVenta;
import com.sistemabarberia.fadex_backend.modules.venta.entity.Venta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DetalleVentaMapper {

    @Mapping(target = "detalleVentaId", ignore = true)
    @Mapping(target = "venta", ignore = true)
    DetalleVenta toEntity(DetalleVentaRequestDTO dto);

    @Mapping(source = "venta.ventaId", target = "ventaId")
    @Mapping(target = "productoNombre", ignore = true)
    DetalleVentaResponseDTO toResponse(DetalleVenta detalle);

    List<DetalleVentaResponseDTO> toResponseList(List<DetalleVenta> detalles);

    default Venta mapVenta(Integer id) {
        if (id == null) return null;
        Venta venta = new Venta();
        venta.setVentaId(id);
        return venta;
    }
}