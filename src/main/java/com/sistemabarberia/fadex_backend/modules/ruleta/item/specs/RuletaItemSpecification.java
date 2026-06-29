package com.sistemabarberia.fadex_backend.modules.ruleta.item.specs;

import com.sistemabarberia.fadex_backend.modules.ruleta.item.dto.RuletaItemFiltro;
import com.sistemabarberia.fadex_backend.modules.ruleta.item.entity.RuletaItem;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class RuletaItemSpecification {
    private RuletaItemSpecification(){}
    public static Specification<RuletaItem> conFiltros(RuletaItemFiltro filtro){
        return (root,query,cb)->{
            List<Predicate> predicates=new ArrayList<>();

            if(filtro.getRuletaId()!=null){
                predicates.add(cb.equal(root.get("ruleta").get("ruletaId"),filtro.getRuletaId()));
            }

            if(filtro.getNombre()!=null && !filtro.getNombre().isBlank()){
                predicates.add(cb.like(cb.lower(root.get("nombre")), "%" + filtro.getNombre().trim().toLowerCase() + "%"));
            }

            if(filtro.getTipoPremio()!=null){
                predicates.add(cb.equal(root.get("tipoPremio"),filtro.getTipoPremio()));
            }

            if(filtro.getActivo()!=null){
                predicates.add(cb.equal(root.get("activo"),filtro.getActivo()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}