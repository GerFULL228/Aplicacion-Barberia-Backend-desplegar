package com.sistemabarberia.fadex_backend.modules.ruleta.item.repository;

import com.sistemabarberia.fadex_backend.modules.ruleta.item.entity.RuletaItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RuletaItemRepository extends JpaRepository<RuletaItem, Long>, JpaSpecificationExecutor<RuletaItem> {
    List<RuletaItem> findByRuletaRuletaIdAndActivoTrue(Long ruletaId);
    boolean existsByRuletaRuletaIdAndNombreIgnoreCase(Long ruletaId, String nombre);
    boolean existsByRuletaRuletaIdAndEsPremioMayorTrue(Long ruletaId);
}