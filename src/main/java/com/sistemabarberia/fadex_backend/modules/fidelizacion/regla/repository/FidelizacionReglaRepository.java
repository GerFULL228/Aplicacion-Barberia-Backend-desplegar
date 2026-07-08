package com.sistemabarberia.fadex_backend.modules.fidelizacion.regla.repository;

import com.sistemabarberia.fadex_backend.modules.fidelizacion.regla.entity.FidelizacionRegla;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.regla.entity.enums.TipoAlcanceFidelizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface FidelizacionReglaRepository extends JpaRepository<FidelizacionRegla, Long>, JpaSpecificationExecutor<FidelizacionRegla> {
    boolean existsByCategoriaIdAndServicioServicioId(Long categoriaId, Long servicioId);
    boolean existsByCategoriaIdAndProductoId(Long categoriaId, Long productoId);
    Optional<FidelizacionRegla> findByCategoriaIdAndServicioServicioIdAndActivoTrue(Long categoriaId, Long servicioId);
    Optional<FidelizacionRegla> findByCategoriaIdAndProductoIdAndActivoTrue(Long categoriaId, Long productoId);
    Optional<FidelizacionRegla> findByCategoriaIdAndTipoAlcanceAndActivoTrue(Long categoriaId, TipoAlcanceFidelizacion tipoAlcance);
    boolean existsByCategoriaIdAndTipoAlcanceAndActivoTrue(Long categoriaId, TipoAlcanceFidelizacion tipoAlcance);
}