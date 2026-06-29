package com.sistemabarberia.fadex_backend.modules.ruleta.giro.repository;

import com.sistemabarberia.fadex_backend.modules.ruleta.giro.entity.RuletaGiro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RuletaGiroRepository extends JpaRepository<RuletaGiro, Long>, JpaSpecificationExecutor<RuletaGiro> {
}