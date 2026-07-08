package com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.repository;

import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.entity.FidelizacionTarjeta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FidelizacionTarjetaRepository extends JpaRepository<FidelizacionTarjeta, Long> {
    Page<FidelizacionTarjeta> findAll(Specification<FidelizacionTarjeta> spec, Pageable pageable);
    Optional<FidelizacionTarjeta> findByClienteClienteIdAndCategoriaId(Integer clienteId, Long categoriaId);
    boolean existsByClienteClienteIdAndCategoriaId(Integer clienteId, Long categoriaId);
    List<FidelizacionTarjeta> findByClienteClienteId(Integer clienteId);

}