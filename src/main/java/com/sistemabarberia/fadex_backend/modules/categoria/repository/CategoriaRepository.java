package com.sistemabarberia.fadex_backend.modules.categoria.repository;

import com.sistemabarberia.fadex_backend.modules.categoria.entity.Categoria;
import com.sistemabarberia.fadex_backend.modules.categoria.entity.CategoriaEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    List<Categoria> findByEstadoTrue();
    boolean existsByPadreId(Long padreId);
    boolean existsByNombreIgnoreCaseAndPadreIsNullAndEstadoTrue(String nombre);
    boolean existsByNombreIgnoreCaseAndPadreIdAndEstadoTrueAndTipo(String nombre, Long padreId, CategoriaEnum tipo);
}
