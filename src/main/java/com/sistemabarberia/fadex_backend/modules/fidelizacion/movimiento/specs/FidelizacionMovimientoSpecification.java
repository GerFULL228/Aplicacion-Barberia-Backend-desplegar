package com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.specs;

import com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.dto.FidelizacionMovimientoFiltro;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.entity.FidelizacionMovimiento;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalTime;

public class FidelizacionMovimientoSpecification {
    public static Specification<FidelizacionMovimiento> conFiltros(FidelizacionMovimientoFiltro filtro) {
        return (root, query, cb) -> {
            var predicate = cb.conjunction();

            if (filtro.getClienteId() != null) {
                predicate.getExpressions().add(cb.equal(root.get("cliente").get("clienteId"), filtro.getClienteId()));
            }

            if (filtro.getTarjetaId() != null) {
                predicate.getExpressions().add(cb.equal(root.get("tarjeta").get("id"), filtro.getTarjetaId()));
            }

            if (filtro.getOrigen() != null) {
                predicate.getExpressions().add(cb.equal(root.get("origen"), filtro.getOrigen()));
            }

            if (filtro.getFechaInicio() != null) {
                predicate.getExpressions().add(cb.greaterThanOrEqualTo(root.get("createdAt"), filtro.getFechaInicio().atStartOfDay()));
            }

            if (filtro.getFechaFin() != null) {
                predicate.getExpressions().add(cb.lessThanOrEqualTo(root.get("createdAt"), filtro.getFechaFin().atTime(LocalTime.MAX)));
            }

            return predicate;
        };
    }

}