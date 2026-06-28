package com.sistemabarberia.fadex_backend.modules.ruleta.categoria.repository;

import com.sistemabarberia.fadex_backend.modules.ruleta.categoria.entity.RuletaCategoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface RuletaCategoriaRepository extends JpaRepository<RuletaCategoria, Long>, JpaSpecificationExecutor<RuletaCategoria> {

    List<RuletaCategoria> findByRuletaIdRuleta(Long idRuleta);
    List<RuletaCategoria> findByCategoriaIdCategoria(Long idCategoria);
    Optional<RuletaCategoria> findByRuletaIdRuletaAndCategoriaIdCategoria(Long idRuleta, Long idCategoria);
    boolean existsByRuletaIdRuletaAndCategoriaIdCategoria(Long idRuleta, Long idCategoria);
}