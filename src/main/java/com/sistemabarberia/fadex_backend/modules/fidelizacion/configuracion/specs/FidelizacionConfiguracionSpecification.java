package com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.specs;

import com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.dto.ConfiguracionFiltro;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.entity.FidelizacionConfiguracion;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class FidelizacionConfiguracionSpecification {
    private FidelizacionConfiguracionSpecification(){}
    public static Specification<FidelizacionConfiguracion> conFiltros(ConfiguracionFiltro filtro) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filtro.getActiva() != null) {
                predicates.add(cb.equal(root.get("activa"), filtro.getActiva()));
            }

            if (filtro.getCategoriaId() != null) {
                predicates.add(cb.equal(root.get("categoria").get("id"), filtro.getCategoriaId()));
            }

            if (filtro.getRuletaId() != null) {
                predicates.add(cb.equal(root.get("ruleta").get("ruletaId"), filtro.getRuletaId()));
            }

            if (filtro.getCategoriaNombre() != null && !filtro.getCategoriaNombre().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("categoria").get("nombre")), "%" + filtro.getCategoriaNombre().trim().toLowerCase() + "%"));
            }

            if (filtro.getRuletaNombre() != null && !filtro.getRuletaNombre().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("ruleta").get("nombre")), "%" + filtro.getRuletaNombre().trim().toLowerCase() + "%"));
            }

            if (filtro.getMetaDesde() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("meta"), filtro.getMetaDesde()));
            }

            if (filtro.getMetaHasta() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("meta"), filtro.getMetaHasta()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}