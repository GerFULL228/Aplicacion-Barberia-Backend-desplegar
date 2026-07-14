package com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.repository;

import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.entity.RecompensaObtenida;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface RecompensaObtenidaRepository extends JpaRepository<RecompensaObtenida, Long>, JpaSpecificationExecutor<RecompensaObtenida> {
    List<RecompensaObtenida> findByClienteClienteIdOrderByCreatedAtDesc(Integer clienteId);
    Optional<RecompensaObtenida> findByCodigoCanje(String codigoCanje);
    List<RecompensaObtenida> findAllByIdIn(List<Long> ids);
    boolean existsByCodigoCanje(String codigoCanje);
    long count();
    List<RecompensaObtenida> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
