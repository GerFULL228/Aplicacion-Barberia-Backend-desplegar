package com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.repository;

import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.entity.RecompensaObtenida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RecompensaObtenidaRepository extends JpaRepository<RecompensaObtenida, Long>, JpaSpecificationExecutor<RecompensaObtenida> {
}
