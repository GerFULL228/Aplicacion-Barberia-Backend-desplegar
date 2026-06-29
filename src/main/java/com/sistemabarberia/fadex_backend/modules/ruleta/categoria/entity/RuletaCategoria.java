package com.sistemabarberia.fadex_backend.modules.ruleta.categoria.entity;

import com.sistemabarberia.fadex_backend.modules.categoria.entity.Categoria;
import com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.entity.Ruleta;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ruleta_categoria", uniqueConstraints = {@UniqueConstraint(name = "uk_ruleta_categoria", columnNames = {"id_ruleta", "id_categoria"})})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RuletaCategoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ruleta_categoria")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ruleta", nullable = false)
    private Ruleta ruleta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;
}