package com.sistemabarberia.fadex_backend.modules.venta.entity;

import com.sistemabarberia.fadex_backend.modules.barbero.entity.Barbero;
import com.sistemabarberia.fadex_backend.auth.usuario.Entity.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "planilla")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Planilla {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_planilla")
    private Integer idPlanilla;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_barbero", nullable = false)
    private Barbero barbero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_periodo", nullable = false)
    private PeriodoPago periodoPago;

    @Column(name = "sueldo_base", precision = 10, scale = 2)
    private BigDecimal sueldoBase;

    @Column(name = "total_ventas", precision = 10, scale = 2)
    private BigDecimal totalVentas;

    @Column(name = "comision_pct", precision = 5, scale = 2)
    private BigDecimal comisionPct;

    @Column(name = "comision_monto", precision = 10, scale = 2)
    private BigDecimal comisionMonto;

    @Column(name = "sueldo_final", precision = 10, scale = 2)
    private BigDecimal sueldoFinal;

    @Column(name = "fecha_calculo")
    private LocalDateTime fechaCalculo;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aprobado_por")
    private Usuario aprobadoPor;

    @Column(name = "estado")
    private String estado;
}