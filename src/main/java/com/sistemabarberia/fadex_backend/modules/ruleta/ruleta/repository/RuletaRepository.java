package com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.repository;

import com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.entity.Ruleta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RuletaRepository extends JpaRepository<Ruleta, Long>, JpaSpecificationExecutor<Ruleta>  {
    Optional<Ruleta> findByRuletaIdAndActivaTrue(Long ruletaId);
    boolean existsByNombreIgnoreCase(String nombre);
    List<Ruleta> findByActivaTrue();
}