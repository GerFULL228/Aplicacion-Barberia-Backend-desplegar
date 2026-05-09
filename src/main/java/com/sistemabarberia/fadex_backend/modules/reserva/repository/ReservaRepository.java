package com.sistemabarberia.fadex_backend.modules.reserva.repository;

import com.sistemabarberia.fadex_backend.modules.reserva.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    @Query("""
    SELECT COUNT(r) > 0 FROM Reserva r
    WHERE r.barbero.barberoId = :barberoId
    AND r.fecha = :fecha
    AND (r.horaInicio < :horaFin AND r.horaFin > :horaInicio)
""")
    boolean existeConflicto(
            @Param("barberoId") Integer barberoId,
            @Param("fecha") LocalDate fecha,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFin") LocalTime horaFin
    );
}
