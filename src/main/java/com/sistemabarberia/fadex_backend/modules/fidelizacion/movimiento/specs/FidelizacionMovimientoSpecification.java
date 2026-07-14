package com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.specs;

import com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.dto.FidelizacionMovimientoFiltro;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.entity.FidelizacionMovimiento;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class FidelizacionMovimientoSpecification {
    public static Specification<FidelizacionMovimiento> conFiltros(FidelizacionMovimientoFiltro filtro) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filtro.getClienteId() != null) {
                predicates.add(cb.equal(root.get("cliente").get("clienteId"), filtro.getClienteId().intValue()));
            }

            if (filtro.getTarjetaId() != null) {
                predicates.add(cb.equal(root.get("tarjeta").get("id"), filtro.getTarjetaId()));
            }

            if (filtro.getOrigen() != null) {
                predicates.add(cb.equal(root.get("origen"), filtro.getOrigen()));
            }

            if (filtro.getFechaInicio() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), filtro.getFechaInicio().atStartOfDay()));
            }

            if (filtro.getFechaFin() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), filtro.getFechaFin().atTime(LocalTime.MAX)));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}