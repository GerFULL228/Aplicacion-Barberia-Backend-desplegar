package com.sistemabarberia.fadex_backend.modules.venta.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pago_planilla")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoPlanilla {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago_planilla")
    private Integer idPagoPlanilla;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_planilla", nullable = false)
    private Planilla planilla;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    @Column(name = "monto", precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "metodo")
    private String metodo;

    @Column(name = "observacion")
    private String observacion;
}