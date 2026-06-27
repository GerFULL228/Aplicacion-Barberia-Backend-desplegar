package com.sistemabarberia.fadex_backend.modules.fidelizacion.regla.repository;

import com.sistemabarberia.fadex_backend.modules.fidelizacion.regla.entity.FidelizacionRegla;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FidelizacionReglaRepository extends JpaRepository<FidelizacionRegla, Long>, JpaSpecificationExecutor<FidelizacionRegla> {
    boolean existsByCategoriaCategoriaIdAndServicioServicioId(Long categoriaId, Long servicioId);
    boolean existsByCategoriaCategoriaIdAndProductoProductoId(Long categoriaId, Long productoId);
}