package com.sistemabarberia.fadex_backend.modules.cliente.repository;

import com.sistemabarberia.fadex_backend.auth.usuario.Entity.Usuario;
import com.sistemabarberia.fadex_backend.modules.cliente.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente,Integer> {


    boolean existsByPersona_PersonaId(Integer personaId);


    Optional<Cliente> findByPersona_Usuario_IdUsuario(Integer usuarioId);

    Optional<Cliente> findByPersonaUsuario(Usuario usuario);

    // TOTAL CLIENTES
    @Query("SELECT COUNT(c) FROM Cliente c")
    Long contarClientes();

    // NUEVOS CLIENTES ESTE MES
    @Query("""
           SELECT COUNT(c)
           FROM Cliente c
           WHERE MONTH(c.fechaRegistro) = MONTH(CURRENT_DATE)
           AND YEAR(c.fechaRegistro) = YEAR(CURRENT_DATE)
           """)
    Long contarClientesNuevosMes();

    @Query(value = """
    SELECT COUNT(*)
    FROM cliente
    WHERE EXTRACT(MONTH FROM fecha_registro) =
          EXTRACT(MONTH FROM CURRENT_DATE - INTERVAL '1 month')
    AND EXTRACT(YEAR FROM fecha_registro) =
          EXTRACT(YEAR FROM CURRENT_DATE - INTERVAL '1 month')
    """, nativeQuery = true)
    Long contarClientesNuevosMesAnterior();

    @Query(value = """
    SELECT COUNT(*)
    FROM cliente
    WHERE fecha_registro <
          date_trunc('month', CURRENT_DATE)
    """, nativeQuery = true)
    Long contarClientesHastaMesAnterior();

    /*ACTIVIDAD*/

    @Query(value = """

    (
        SELECT
            'CORTE' AS tipo,
            'Último corte' AS titulo,
            CONCAT(c.nombre, ' · Barbero: ', pe.nombre) AS descripcion,
            hc.fecha AS fecha,
            '#facc15' AS color

        FROM historial_cortes hc

        INNER JOIN detalle_reservas dr
            ON hc.id_detalle_reserva = dr.id_detalle_reserva

        INNER JOIN cortes c
            ON dr.id_corte = c.id_corte

        INNER JOIN reservas r
            ON dr.id_reservas = r.id_reservas

        INNER JOIN barbero b
            ON r.id_barbero = b.id_barbero

        INNER JOIN persona pe
            ON b.id_persona = pe.id_persona

        WHERE hc.id_cliente = :idCliente
    )

    UNION ALL

    (
        SELECT
            'COMPRA' AS tipo,
            'Última compra' AS titulo,
            CONCAT(pr.nombre, ' · S/ ', dv.precio_unitario) AS descripcion,
            hv.fecha AS fecha,
            '#10b981' AS color

        FROM historial_venta hv

        INNER JOIN venta v
            ON hv.id_venta = v.id_venta

        INNER JOIN detalle_venta dv
            ON v.id_venta = dv.id_venta

        INNER JOIN producto pr
            ON dv.id_producto = pr.id_producto

        WHERE v.id_cliente = :idCliente
    )

    UNION ALL

    (
        SELECT
            'PAGO' AS tipo,
            'Último pago' AS titulo,
            CONCAT(p.metodo, ' · S/ ', p.monto) AS descripcion,
            hp.fecha AS fecha,
            '#0ea5e9' AS color

        FROM historial_pago hp

        INNER JOIN pago p
            ON hp.id_pago = p.id_pago

        WHERE hp.id_cliente = :idCliente
    )

    ORDER BY fecha DESC
    LIMIT 10

""", nativeQuery = true)
    List<Object[]> obtenerActividadReciente(
            @Param("idCliente") Integer idCliente
    );
}
