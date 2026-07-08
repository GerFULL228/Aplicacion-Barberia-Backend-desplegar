package com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.repository;

import com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.entity.FidelizacionMovimiento;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.entity.enums.OrigenFidelizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface FidelizacionMovimientoRepository extends JpaRepository<FidelizacionMovimiento,Long>, JpaSpecificationExecutor<FidelizacionMovimiento> {
    boolean existsByOrigenAndIdOrigen(OrigenFidelizacion origen, Long idOrigen);
    List<FidelizacionMovimiento> findByClienteClienteIdOrderByCreatedAtDesc(Integer clienteId);
    List<FidelizacionMovimiento> findByTarjetaIdOrderByCreatedAtDesc(Long tarjetaId);
}

