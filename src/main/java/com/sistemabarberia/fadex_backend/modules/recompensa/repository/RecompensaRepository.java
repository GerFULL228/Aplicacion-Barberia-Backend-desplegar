package com.sistemabarberia.fadex_backend.modules.recompensa.repository;

import com.sistemabarberia.fadex_backend.modules.recompensa.entity.Recompensa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecompensaRepository extends JpaRepository<Recompensa, Integer> {

    Optional<Recompensa> findByCliente_ClienteId(Integer clienteId);

    Optional<Recompensa> findByCliente_Persona_Usuario_IdUsuario(Integer usuarioId);

    // Suma +1 al acumulado y si llega a 10 otorga un corte gratis y resetea
    @Modifying
    @Query(value = """
            UPDATE recompensa
            SET cortes_acumulados  = CASE
                                        WHEN cortes_acumulados + 1 >= 10 THEN 0
                                        ELSE cortes_acumulados + 1
                                     END,
                cortes_gratis      = CASE
                                        WHEN cortes_acumulados + 1 >= 10 THEN cortes_gratis + 1
                                        ELSE cortes_gratis
                                     END,
                fecha_actualizacion = NOW()
            WHERE id_cliente = :clienteId
            """, nativeQuery = true)
    void acumularCorte(@Param("clienteId") Integer clienteId);

    // Descuenta un corte gratis al canjearlo
    @Modifying
    @Query(value = """
            UPDATE recompensa
            SET cortes_gratis       = cortes_gratis - 1,
                fecha_actualizacion = NOW()
            WHERE id_cliente = :clienteId
              AND cortes_gratis > 0
            """, nativeQuery = true)
    int canjearCorteGratis(@Param("clienteId") Integer clienteId);
}