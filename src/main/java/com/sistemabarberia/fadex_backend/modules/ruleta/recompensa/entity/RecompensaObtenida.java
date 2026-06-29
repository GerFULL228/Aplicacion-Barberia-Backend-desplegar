package com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.entity;

import com.sistemabarberia.fadex_backend.auth.usuario.Entity.Usuario;
import com.sistemabarberia.fadex_backend.commons.shared.AuditableEntity;
import com.sistemabarberia.fadex_backend.modules.cliente.entity.Cliente;
import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.entity.enums.EstadoRecompensa;
import com.sistemabarberia.fadex_backend.modules.ruleta.giro.entity.RuletaGiro;
import com.sistemabarberia.fadex_backend.modules.ruleta.item.entity.RuletaItem;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "recompensa_obtenida")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RecompensaObtenida extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_recompensa")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_giro", nullable = false)
    private RuletaGiro giro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_item", nullable = false)
    private RuletaItem item;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EstadoRecompensa estado = EstadoRecompensa.PENDIENTE;

    @Column(length = 255)
    private String observacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_canje")
    private Usuario usuarioCanje;

    @Column(name = "fecha_obtencion", nullable = false)
    @Builder.Default
    private LocalDateTime fechaObtencion = LocalDateTime.now();

    @Column(name = "fecha_vencimiento")
    private LocalDateTime fechaVencimiento;

    @Column(name = "fecha_canje")
    private LocalDateTime fechaCanje;

    @Column(name = "codigo_canje", length = 50)
    private String codigoCanje;
}