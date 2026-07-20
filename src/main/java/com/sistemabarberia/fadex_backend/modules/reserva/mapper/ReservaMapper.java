package com.sistemabarberia.fadex_backend.modules.reserva.mapper;

import com.sistemabarberia.fadex_backend.modules.persona.entity.Persona;
import com.sistemabarberia.fadex_backend.modules.reserva.dto.Request.ReservaRequest;
import com.sistemabarberia.fadex_backend.modules.reserva.dto.Response.ReservaDTO;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.Reserva;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReservaMapper {

    Reserva toEntity(ReservaRequest request);

    @Mapping(target = "reservaId", source = "id")
    @Mapping(target = "clienteNombre", expression = "java(nombreCompleto(reserva.getCliente() != null ? reserva.getCliente().getPersona() : null))")
    @Mapping(target = "barberoNombre", expression = "java(nombreCompleto(reserva.getBarbero() != null ? reserva.getBarbero().getPersona() : null))")
    @Mapping(target = "servicio", source = "reserva.servicio.nombre")
    @Mapping(target = "tipoReserva", source = "reserva.tipoReserva")
    ReservaDTO toDto(Reserva reserva);

    List<ReservaDTO> toDtoLista(List<Reserva> reservas);

    default String nombreCompleto(Persona persona) {
        if (persona == null) return "N/A";
        String nombre = persona.getNombre() != null ? persona.getNombre() : "";
        String apellido = persona.getApellido() != null ? persona.getApellido() : "";
        return (nombre + " " + apellido).trim();
    }
}