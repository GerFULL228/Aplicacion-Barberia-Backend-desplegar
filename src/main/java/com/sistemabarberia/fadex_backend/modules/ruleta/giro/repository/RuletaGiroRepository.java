package com.sistemabarberia.fadex_backend.modules.ruleta.giro.repository;

import com.sistemabarberia.fadex_backend.modules.ruleta.giro.entity.RuletaGiro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RuletaGiroRepository extends JpaRepository<RuletaGiro, Long>, JpaSpecificationExecutor<RuletaGiro> {
    List<RuletaGiro> findByClienteClienteIdOrderByCreatedAtDesc(Integer clienteId);
    List<RuletaGiro> findAllByOrderByCreatedAtDesc(Pageable pageable);
    @Query(value = """
    SELECT DATE_TRUNC('week', g.created_at)::date as semana_inicio, COUNT(g.id_giro) as total
    FROM ruleta_giro g
    WHERE g.created_at BETWEEN :fechaInicio AND :fechaFin
    GROUP BY DATE_TRUNC('week', g.created_at)
    ORDER BY DATE_TRUNC('week', g.created_at)
    """, nativeQuery = true)
    List<Object[]> contarGirosPorSemanaRaw(@Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin);
}