package com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.repository;

import com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.entity.FidelizacionConfiguracion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FidelizacionConfiguracionRepository extends JpaRepository<FidelizacionConfiguracion, Long>, JpaSpecificationExecutor<FidelizacionConfiguracion> {
    Optional<FidelizacionConfiguracion> findByCategoria_IdAndActivaTrue(Long categoriaId);
    Optional<FidelizacionConfiguracion> findByCategoria_Id(Long categoriaId);
    boolean existsByCategoria_Id(Long categoriaId);
}