package com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.repository;

import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.entity.FidelizacionTarjeta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FidelizacionTarjetaRepository extends JpaRepository<FidelizacionTarjeta, Long> {
    Page<FidelizacionTarjeta> findAll(Specification<FidelizacionTarjeta> spec, Pageable pageable);
    Optional<FidelizacionTarjeta> findByClienteIdAndCategoriaId(Long clienteId, Long categoriaId);
    boolean existsByClienteIdAndCategoriaId(Long clienteId, Long categoriaId);
}