package com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.repository;

import com.sistemabarberia.fadex_backend.modules.fidelizacion.movimiento.entity.FidelizacionMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FidelizacionMovimientoRepository extends JpaRepository<FidelizacionMovimiento,Long>, JpaSpecificationExecutor<FidelizacionMovimiento> {

}

