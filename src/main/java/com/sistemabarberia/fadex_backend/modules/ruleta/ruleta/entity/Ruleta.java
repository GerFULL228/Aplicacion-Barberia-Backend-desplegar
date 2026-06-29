package com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.entity;

import com.sistemabarberia.fadex_backend.commons.shared.AuditableEntity;
import com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.entity.enums.TipoRuleta;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "ruleta")
public class Ruleta extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ruleta")
    private Long ruletaId;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoRuleta tipo;

    @Builder.Default
    @Column(nullable = false)
    private Boolean activa = true;

    @Builder.Default
    @Column(name = "incremento_por_giro", nullable = false, precision = 6, scale = 4)
    private BigDecimal incrementoPorGiro = BigDecimal.ZERO;
}