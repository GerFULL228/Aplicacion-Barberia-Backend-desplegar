package com.sistemabarberia.fadex_backend.modules.ruleta.categoria.specs;

import com.sistemabarberia.fadex_backend.modules.ruleta.categoria.dto.RuletaCategoriaFiltro;
import com.sistemabarberia.fadex_backend.modules.ruleta.categoria.entity.RuletaCategoria;
import org.springframework.data.jpa.domain.Specification;

public class RuletaCategoriaSpecs {
    public static Specification<RuletaCategoria> filter(RuletaCategoriaFiltro filter) {
        return (root, query, cb) -> {
            var predicate = cb.conjunction();

            if (filter.getIdRuleta() != null) {
                predicate.getExpressions().add(cb.equal(root.get("ruleta").get("ruletaId"), filter.getIdRuleta()));
            }

            if (filter.getIdCategoria() != null) {
                predicate.getExpressions().add(cb.equal(root.get("categoria").get("id"), filter.getIdCategoria()));
            }

            return predicate;
        };
    }
}