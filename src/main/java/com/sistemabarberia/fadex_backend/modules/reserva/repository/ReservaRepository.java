package com.sistemabarberia.fadex_backend.modules.reserva.repository;

import com.sistemabarberia.fadex_backend.modules.reserva.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

    @Query("""
        SELECT r FROM Reserva r
        LEFT JOIN FETCH r.cliente c
        LEFT JOIN FETCH c.persona
        LEFT JOIN FETCH r.detalles d
        LEFT JOIN FETCH d.corte
        WHERE r.barbero.barberoId = :barberoId
          AND r.fecha BETWEEN :desde AND :hasta
        ORDER BY r.fecha ASC
    """)
    List<Reserva> findReservasDiarias(
            @Param("barberoId") Integer barberoId,
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta
    );

    @Query("""
        SELECT r FROM Reserva r
        WHERE r.barbero.barberoId = :barberoId
          AND r.fecha BETWEEN :desde AND :hasta
    """)
    List<Reserva> findByBarberoIdAndFechaBetween(
            @Param("barberoId") Integer barberoId,
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta
    );
}
