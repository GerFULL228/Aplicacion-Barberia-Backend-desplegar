package com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.specs;

import com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.dto.RuletaFiltro;
import com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.entity.Ruleta;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class RuletaSpecification {
    private RuletaSpecification(){}
    public static Specification<Ruleta> conFiltros(RuletaFiltro filtro){
        return (root, query, cb)->{
            List<Predicate> predicates = new ArrayList<>();

            if(filtro.getNombre()!=null && !filtro.getNombre().isBlank()){
                predicates.add(cb.like(cb.lower(root.get("nombre")), "%" + filtro.getNombre().trim().toLowerCase() + "%"));
            }

            if (filtro.getTipo() != null) {
                predicates.add(cb.equal(root.get("tipo"), filtro.getTipo()));
            }

            if(filtro.getActiva()!=null){predicates.add(cb.equal(root.get("activa"), filtro.getActiva()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}