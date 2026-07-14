package com.sistemabarberia.fadex_backend.modules.ruleta.giro.specs;

import com.sistemabarberia.fadex_backend.modules.ruleta.giro.dto.RuletaGiroFiltro;
import com.sistemabarberia.fadex_backend.modules.ruleta.giro.entity.RuletaGiro;
import org.springframework.data.jpa.domain.Specification;

public class RuletaGiroSpecification {
    public static Specification<RuletaGiro> conFiltros(RuletaGiroFiltro filtro) {
        return (root, query, cb) -> {
            var predicate = cb.conjunction();

            if (filtro.getClienteId() != null) {
                predicate.getExpressions().add(cb.equal(root.get("cliente").get("clienteId"), filtro.getClienteId()));
            }

            if (filtro.getTarjetaId() != null) {
                predicate.getExpressions().add(cb.equal(root.get("tarjeta").get("id"), filtro.getTarjetaId()));
            }

            if (filtro.getRuletaId() != null) {
                predicate.getExpressions().add(cb.equal(root.get("ruleta").get("ruletaId"), filtro.getRuletaId()));
            }

            if (filtro.getFechaInicio() != null) {
                predicate.getExpressions().add(cb.greaterThanOrEqualTo(root.get("createdAt"), filtro.getFechaInicio().atStartOfDay()));
            }

            if (filtro.getFechaFin() != null) {
                predicate.getExpressions().add(cb.lessThanOrEqualTo(root.get("createdAt"), filtro.getFechaFin().atTime(23,59,59)));
            }

            return predicate;
        };

    }

}