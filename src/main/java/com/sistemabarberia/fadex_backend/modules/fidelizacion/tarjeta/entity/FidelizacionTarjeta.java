package com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.entity;

import com.sistemabarberia.fadex_backend.commons.shared.AuditableEntity;
import com.sistemabarberia.fadex_backend.modules.categoria.entity.Categoria;
import com.sistemabarberia.fadex_backend.modules.cliente.entity.Cliente;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "fidelizacion_tarjeta", uniqueConstraints = {@UniqueConstraint(columnNames = {"id_cliente","id_categoria"})})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class FidelizacionTarjeta extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tarjeta")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    @Column(nullable = false)
    @Builder.Default
    private Integer progreso = 0;

    @Column(name = "giros_disponibles", nullable = false)
    @Builder.Default
    private Integer girosDisponibles = 0;

    @Column(name = "total_giros", nullable = false)
    @Builder.Default
    private Integer totalGiros = 0;

    @Column(name = "ciclo_activo", nullable = false)
    @Builder.Default
    private Boolean cicloActivo = true;
}