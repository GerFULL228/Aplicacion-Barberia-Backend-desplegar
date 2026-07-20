package com.sistemabarberia.fadex_backend.modules.reserva.specs;

import com.sistemabarberia.fadex_backend.modules.reserva.dto.ReservaFiltro;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.Reserva;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class ReservaSpecification {

    public static Specification<Reserva> conFiltros(ReservaFiltro filtro) {
        return (root, query, cb) -> {
            var predicate = cb.conjunction();
            if (filtro == null) return predicate;

            if (filtro.getClienteId() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("cliente").get("clienteId"), filtro.getClienteId()));
            }
            if (filtro.getBarberoId() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("barbero").get("barberoId"), filtro.getBarberoId()));
            }
            if (filtro.getServicioId() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("servicio").get("servicioId"), filtro.getServicioId()));
            }

            if (filtro.getClienteNombre() != null && !filtro.getClienteNombre().isBlank()) {
                Join<Object, Object> cliente = root.join("cliente", JoinType.LEFT);
                Join<Object, Object> personaCliente = cliente.join("persona", JoinType.LEFT);
                predicate = cb.and(predicate,
                        cb.like(cb.lower(personaCliente.get("nombre")), "%" + filtro.getClienteNombre().toLowerCase() + "%"));
            }

            if (filtro.getBarberoNombre() != null && !filtro.getBarberoNombre().isBlank()) {
                Join<Object, Object> barbero = root.join("barbero", JoinType.LEFT);
                Join<Object, Object> personaBarbero = barbero.join("persona", JoinType.LEFT);
                predicate = cb.and(predicate,
                        cb.like(cb.lower(personaBarbero.get("nombre")), "%" + filtro.getBarberoNombre().toLowerCase() + "%"));
            }

            if (filtro.getEstadoReserva() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("estadoReserva"), filtro.getEstadoReserva()));
            }
            if (filtro.getTipoReserva() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("tipoReserva"), filtro.getTipoReserva()));
            }

            if (filtro.getFecha() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("fecha"), filtro.getFecha()));
            }
            if (filtro.getFechaDesde() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("fecha"), filtro.getFechaDesde()));
            }
            if (filtro.getFechaHasta() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("fecha"), filtro.getFechaHasta()));
            }

            return predicate;
        };
    }
}