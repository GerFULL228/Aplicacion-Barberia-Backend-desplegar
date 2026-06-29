package com.sistemabarberia.fadex_backend.modules.ruleta.item.entity;

import com.sistemabarberia.fadex_backend.commons.shared.AuditableEntity;
import com.sistemabarberia.fadex_backend.modules.producto.entity.Producto;
import com.sistemabarberia.fadex_backend.modules.ruleta.item.entity.enums.TipoPremio;
import com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.entity.Ruleta;
import com.sistemabarberia.fadex_backend.modules.servicio.entity.Servicio;
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
@Table(name = "ruleta_item")
public class RuletaItem extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item")
    private Long itemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ruleta", nullable = false)
    private Ruleta ruleta;

    @Column(nullable = false,length = 100)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_premio",nullable = false)
    private TipoPremio tipoPremio;

    @Column(precision = 10,scale = 2)
    private BigDecimal valor;

    @Column(nullable = false,precision = 6,scale = 3)
    private BigDecimal probabilidad;

    @Builder.Default
    @Column(name = "es_premio_mayor",nullable = false)
    private Boolean esPremioMayor=false;

    private Integer stock;

    @Builder.Default
    @Column(name="orden_display")
    private Integer ordenDisplay=0;

    @Column(name="imagen_url")
    private String imagenUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="id_producto")
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="id_servicio")
    private Servicio servicio;

    @Builder.Default
    @Column(nullable = false)
    private Boolean activo=true;
}