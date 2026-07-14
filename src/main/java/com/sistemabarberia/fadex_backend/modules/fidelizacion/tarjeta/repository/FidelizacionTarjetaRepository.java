package com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.repository;

import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.dto.response.TarjetasPorCategoriaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.entity.FidelizacionTarjeta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FidelizacionTarjetaRepository extends JpaRepository<FidelizacionTarjeta, Long> {
    Page<FidelizacionTarjeta> findAll(Specification<FidelizacionTarjeta> spec, Pageable pageable);
    Optional<FidelizacionTarjeta> findByClienteClienteIdAndCategoriaId(Integer clienteId, Long categoriaId);
    boolean existsByClienteClienteIdAndCategoriaId(Integer clienteId, Long categoriaId);
    List<FidelizacionTarjeta> findByClienteClienteId(Integer clienteId);
    long countByClienteClienteId(Integer clienteId);
    @Query("""
        SELECT COALESCE(SUM(t.girosDisponibles),0)
        FROM FidelizacionTarjeta t
        WHERE t.cliente.clienteId = :clienteId
    """)
    Integer obtenerTotalGirosDisponibles(Integer clienteId);

    @Query("""
        SELECT COUNT(t)
        FROM FidelizacionTarjeta t
        WHERE t.cliente.clienteId = :clienteId
          AND t.girosDisponibles > 0
    """)
    Integer contarTarjetasConGiro(Integer clienteId);

    @Query("""
    SELECT t.categoria.id, t.categoria.nombre, COUNT(t),
    SUM(CASE WHEN t.girosDisponibles > 0 THEN 1 ELSE 0 END),
    COALESCE(SUM(t.girosDisponibles),0)
    FROM FidelizacionTarjeta t
    GROUP BY t.categoria.id, t.categoria.nombre
    ORDER BY COUNT(t) DESC
    """)
    List<Object[]> obtenerTarjetasPorCategoria();
}