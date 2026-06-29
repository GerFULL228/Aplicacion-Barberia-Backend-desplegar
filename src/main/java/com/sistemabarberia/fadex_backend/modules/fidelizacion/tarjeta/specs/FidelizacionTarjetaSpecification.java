package com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.specs;

import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.dto.FidelizacionTarjetaFiltro;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.entity.FidelizacionTarjeta;
import org.springframework.data.jpa.domain.Specification;

public class FidelizacionTarjetaSpecification {
    public static Specification<FidelizacionTarjeta> conFiltros(FidelizacionTarjetaFiltro filtro) {
        return (root, query, cb) -> {
            var predicate = cb.conjunction();
            if (filtro.getClienteId() != null) {
                predicate.getExpressions().add(cb.equal(root.get("cliente").get("id"), filtro.getClienteId()));
            }

            if (filtro.getCategoriaId() != null) {
                predicate.getExpressions().add(cb.equal(root.get("categoria").get("id"), filtro.getCategoriaId()));
            }

            if (filtro.getCicloActivo() != null) {
                predicate.getExpressions().add(cb.equal(root.get("cicloActivo"), filtro.getCicloActivo()));
            }

            return predicate;
        };
    }
}