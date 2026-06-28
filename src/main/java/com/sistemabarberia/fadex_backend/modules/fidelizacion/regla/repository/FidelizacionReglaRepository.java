package com.sistemabarberia.fadex_backend.modules.fidelizacion.regla.repository;

import com.sistemabarberia.fadex_backend.modules.fidelizacion.regla.entity.FidelizacionRegla;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FidelizacionReglaRepository extends JpaRepository<FidelizacionRegla, Long>, JpaSpecificationExecutor<FidelizacionRegla> {
    boolean existsByCategoriaIdAndServicioServicioId(Long categoriaId, Long servicioId);
    boolean existsByCategoriaIdAndProductoId(Long categoriaId, Long productoId);
}