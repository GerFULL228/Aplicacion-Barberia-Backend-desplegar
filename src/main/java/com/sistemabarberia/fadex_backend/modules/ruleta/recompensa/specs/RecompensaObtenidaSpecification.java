package com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.specs;

import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.dto.RecompensaObtenidaFiltro;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.entity.RecompensaObtenida;
import org.springframework.data.jpa.domain.Specification;

public class RecompensaObtenidaSpecification {
    public static Specification<RecompensaObtenida> conFiltros(RecompensaObtenidaFiltro filtro) {
        return (root, query, cb) -> {
            var predicate = cb.conjunction();

            if (filtro.getClienteId() != null) {
                predicate.getExpressions().add(cb.equal(root.get("cliente").get("clienteId"), filtro.getClienteId().intValue()));
            }

            if (filtro.getEstado() != null) {
                predicate.getExpressions().add(cb.equal(root.get("estado"), filtro.getEstado()));
            }

            if (filtro.getItemId() != null) {
                predicate.getExpressions().add(cb.equal(root.get("item").get("id"), filtro.getItemId()));
            }

            return predicate;
        };
    }
}