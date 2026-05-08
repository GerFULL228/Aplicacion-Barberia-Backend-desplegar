package com.sistemabarberia.fadex_backend.modules.venta.repository;

import com.sistemabarberia.fadex_backend.modules.venta.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer> {

    List<Venta> findByCliente_ClienteId(Integer clienteId);

    List<Venta> findByBarbero_BarberoId(Integer barberoId);

    List<Venta> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);

    List<Venta> findByCliente_ClienteIdAndFechaBetween(
            Integer clienteId,
            LocalDateTime inicio,
            LocalDateTime fin
    );

    List<Venta> findByBarbero_BarberoIdAndFechaBetween(
            Integer barberoId,
            LocalDateTime inicio,
            LocalDateTime fin
    );
}