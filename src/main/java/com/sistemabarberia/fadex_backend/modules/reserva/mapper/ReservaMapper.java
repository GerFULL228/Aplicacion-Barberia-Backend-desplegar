package com.sistemabarberia.fadex_backend.modules.reserva.mapper;


import com.sistemabarberia.fadex_backend.modules.reserva.dto.reponse.ReservaDTO;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.Reserva;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ReservaMapper {

    @Mapping(target = "nombreCliente",   qualifiedByName = "getNombreCliente",   source = ".")
    @Mapping(target = "telefonoCliente", qualifiedByName = "getTelefonoCliente", source = ".")
    @Mapping(target = "nombreBarbero",   qualifiedByName = "getNombreBarbero",   source = ".")
    @Mapping(target = "servicios",       qualifiedByName = "getServicios",       source = ".")
    @Mapping(target = "servicioResumen", qualifiedByName = "getServicioResumen", source = ".")
    @Mapping(target = "duracionMinutos", qualifiedByName = "getDuracion",        source = ".")
    @Mapping(target = "totalPrecio",     qualifiedByName = "getTotalPrecio",     source = ".")
    @Mapping(target = "fecha",       source = "fecha")
    @Mapping(target = "horaInicio",  source = "horaInicio")
    @Mapping(target = "horaFin",     source = "horaFin")
    @Mapping(target = "estado",      source = "estado")
    @Mapping(target = "tipoReserva", source = "tipoReserva")
    ReservaDTO toDTO(Reserva reserva);

    @Named("getNombreCliente")
    default String getNombreCliente(Reserva reserva) {
        if (reserva.getCliente() == null || reserva.getCliente().getPersona() == null)
            return "";
        return reserva.getCliente().getPersona().getNombre()
                + " " + reserva.getCliente().getPersona().getApellido();
    }

    @Named("getTelefonoCliente")
    default String getTelefonoCliente(Reserva reserva) {
        if (reserva.getCliente() == null || reserva.getCliente().getPersona() == null)
            return "";
        return reserva.getCliente().getPersona().getTelefono();
    }

    @Named("getNombreBarbero")
    default String getNombreBarbero(Reserva reserva) {
        if (reserva.getBarbero() == null || reserva.getBarbero().getPersona() == null)
            return "";
        return reserva.getBarbero().getPersona().getNombre()
                + " " + reserva.getBarbero().getPersona().getApellido();
    }

    @Named("getServicios")
    default List<String> getServicios(Reserva reserva) {
        if (reserva.getDetalles() == null) return List.of();
        return reserva.getDetalles().stream()
                .map(d -> d.getCorte().getNombre())
                .collect(Collectors.toList());
    }

    @Named("getServicioResumen")
    default String getServicioResumen(Reserva reserva) {
        if (reserva.getDetalles() == null || reserva.getDetalles().isEmpty())
            return "Sin servicio";
        return reserva.getDetalles().stream()
                .map(d -> d.getCorte().getNombre())
                .collect(Collectors.joining(" + "));
    }

    @Named("getDuracion")
    default Integer getDuracion(Reserva reserva) {
        if (reserva.getDetalles() == null) return 0;
        return reserva.getDetalles().stream()
                .mapToInt(d -> d.getCorte().getDuracionMinutos() != null
                        ? d.getCorte().getDuracionMinutos() : 0)
                .sum();
    }

    @Named("getTotalPrecio")
    default BigDecimal getTotalPrecio(Reserva reserva) {
        if (reserva.getDetalles() == null) return BigDecimal.ZERO;
        return reserva.getDetalles().stream()
                .map(d -> d.getCorte().getPrecio() != null
                        ? d.getCorte().getPrecio() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
