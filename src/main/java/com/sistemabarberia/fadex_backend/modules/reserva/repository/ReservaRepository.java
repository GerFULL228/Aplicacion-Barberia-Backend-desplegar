package com.sistemabarberia.fadex_backend.modules.reserva.repository;

import com.sistemabarberia.fadex_backend.modules.reserva.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

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


    List<Reserva> findByCliente_ClienteId (Integer clienteId);
    List<Reserva> findByBarbero_BarberoId (Integer barberoId);


    @Query("""
        SELECT r FROM Reserva r
        LEFT JOIN FETCH r.cliente c
        LEFT JOIN FETCH c.persona
        WHERE r.barbero.barberoId = :barberoId
          AND r.fecha BETWEEN :desde AND :hasta
        ORDER BY r.fecha ASC
    """)
    List<Reserva> findReservasDiarias(
            @Param("barberoId") Integer barberoId,
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta
    );

    /*********************/
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


    //Resumen-Cliente
    @Query(value = """
       SELECT COUNT(DISTINCT id_cliente)
       FROM reservas
       WHERE EXTRACT(MONTH FROM fecha) = EXTRACT(MONTH FROM CURRENT_DATE)
       AND EXTRACT(YEAR FROM fecha) = EXTRACT(YEAR FROM CURRENT_DATE)
       """, nativeQuery = true)
    Long clientesActivosMes();

    @Query(value = """
       SELECT 
       (
           COUNT(*) * 100.0 /
           (SELECT COUNT(*) FROM cliente)
       )
       FROM (
           SELECT id_cliente
           FROM reservas
           GROUP BY id_cliente
           HAVING COUNT(*) > 1
       ) clientes_fieles
       """, nativeQuery = true)
    Double calcularRetencion();

    @Query(value = """
   SELECT COUNT(DISTINCT id_cliente)
   FROM reservas
   WHERE EXTRACT(MONTH FROM fecha) =
         EXTRACT(MONTH FROM CURRENT_DATE - INTERVAL '1 month')
   AND EXTRACT(YEAR FROM fecha) =
         EXTRACT(YEAR FROM CURRENT_DATE - INTERVAL '1 month')
   """, nativeQuery = true)
    Long clientesActivosMesAnterior();

    @Query(value = """
   SELECT 
   (
       COUNT(*) * 100.0 /
       (SELECT COUNT(*) FROM cliente)
   )
   FROM (
       SELECT id_cliente
       FROM reservas
       WHERE EXTRACT(MONTH FROM fecha) =
             EXTRACT(MONTH FROM CURRENT_DATE - INTERVAL '1 month')
       AND EXTRACT(YEAR FROM fecha) =
             EXTRACT(YEAR FROM CURRENT_DATE - INTERVAL '1 month')
       GROUP BY id_cliente
       HAVING COUNT(*) > 1
   ) clientes_fieles
   """, nativeQuery = true)
    Double calcularRetencionMesAnterior();


    /*Resumen Cliente*/

    @Query(value = """
    SELECT COUNT(*)
    FROM reservas
    WHERE id_cliente = :clienteId
    """, nativeQuery = true)
    Long contarReservasCliente(
            @Param("clienteId") Integer clienteId
    );

    @Query(value = """
    SELECT COUNT(dr.id_detalle_reserva)
    FROM detalle_reservas dr
    INNER JOIN reservas r
        ON dr.id_reservas = r.id_reservas
    WHERE r.id_cliente = :clienteId
    """, nativeQuery = true)
    Long contarCortesCliente(
            @Param("clienteId") Integer clienteId
    );

    @Query(value = """
    SELECT MAX(fecha)
    FROM reservas
    WHERE id_cliente = :clienteId
    """, nativeQuery = true)
    java.sql.Date ultimaVisitaCliente(
            @Param("clienteId") Integer clienteId
    );
}
