package com.sistemabarberia.fadex_backend.modules.ruleta.categoria.repository;

import com.sistemabarberia.fadex_backend.modules.ruleta.categoria.entity.RuletaCategoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface RuletaCategoriaRepository extends JpaRepository<RuletaCategoria, Long>, JpaSpecificationExecutor<RuletaCategoria> {
    List<RuletaCategoria> findByRuletaRuletaId(Long ruletaId);
    List<RuletaCategoria> findByCategoriaId(Long categoriaId);
    Optional<RuletaCategoria> findByRuletaRuletaIdAndCategoriaId(Long ruletaId, Long categoriaId);
    boolean existsByRuletaRuletaIdAndCategoriaId(Long ruletaId, Long categoriaId);

    List<RuletaCategoria> findByRuleta_RuletaId(Long ruletaId);
    List<RuletaCategoria> findByCategoria_Id(Long categoriaId);
    Optional<RuletaCategoria> findByRuleta_RuletaIdAndCategoria_Id(Long ruletaId, Long categoriaId);
    boolean existsByRuleta_RuletaIdAndCategoria_Id(Long ruletaId, Long categoriaId);
}