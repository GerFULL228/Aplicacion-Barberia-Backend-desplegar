package com.sistemabarberia.fadex_backend.modules.reserva.mapper;

import com.sistemabarberia.fadex_backend.modules.reserva.dto.Request.ReservaRequest;
import com.sistemabarberia.fadex_backend.modules.reserva.dto.Response.ReservaDTO;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.Reserva;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservaMapper {

    Reserva toEntity(ReservaRequest request);

    @Mapping(target = "clienteNombre", source = "reserva.cliente.persona.nombre")
    @Mapping(target = "barberoNombre", source = "reserva.barbero.persona.nombre")
    @Mapping(target = "servicio", source = "reserva.servicio.nombre")
    @Mapping(target = "tipoReserva", source = "reserva.tipoReserva")
    ReservaDTO toDto(Reserva reserva);
}
