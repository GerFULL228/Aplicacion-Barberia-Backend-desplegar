package com.sistemabarberia.fadex_backend.modules.fidelizacion.regla.specs;

import com.sistemabarberia.fadex_backend.modules.fidelizacion.regla.dto.FidelizacionReglaFiltro;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.regla.entity.FidelizacionRegla;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class FidelizacionReglaSpecification {
    private FidelizacionReglaSpecification(){}
    public static Specification<FidelizacionRegla> conFiltros(FidelizacionReglaFiltro filtro){
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(filtro.getCategoriaId()!=null){predicates.add(
                    cb.equal(root.get("categoria").get("id"), filtro.getCategoriaId()));
            }

            if(filtro.getTipoAlcance()!=null){
                predicates.add(cb.equal(root.get("tipoAlcance"), filtro.getTipoAlcance()));
            }

            if(filtro.getActivo()!=null){
                predicates.add(cb.equal(root.get("activo"), filtro.getActivo()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}