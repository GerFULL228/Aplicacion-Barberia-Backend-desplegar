package com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.repository;

import com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.dto.response.MovimientoPorSemanaDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.entity.FidelizacionMovimiento;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.entity.enums.OrigenFidelizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface FidelizacionMovimientoRepository extends JpaRepository<FidelizacionMovimiento,Long>, JpaSpecificationExecutor<FidelizacionMovimiento> {
    boolean existsByOrigenAndIdOrigen(OrigenFidelizacion origen, Long idOrigen);
    List<FidelizacionMovimiento> findByClienteClienteIdOrderByCreatedAtDesc(Integer clienteId);
    List<FidelizacionMovimiento> findByTarjetaIdOrderByCreatedAtDesc(Long tarjetaId);
    List<FidelizacionMovimiento> findAllByOrderByCreatedAtDesc(Pageable pageable);
    @Query(value = """
    SELECT DATE_TRUNC('week', m.created_at)::date as semana_inicio,
           COUNT(CASE WHEN m.puntos >= 0 THEN 1 END) as positivos,
           COUNT(CASE WHEN m.puntos < 0 THEN 1 END) as negativos
    FROM fidelizacion_movimiento m
    WHERE m.created_at BETWEEN :fechaInicio AND :fechaFin
    GROUP BY DATE_TRUNC('week', m.created_at)
    ORDER BY DATE_TRUNC('week', m.created_at)
    """, nativeQuery = true)
    List<Object[]> contarMovimientosPorSemanaRaw(@Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin);
}