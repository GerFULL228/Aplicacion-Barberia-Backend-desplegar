package com.sistemabarberia.fadex_backend.modules.venta.entity;



import com.sistemabarberia.fadex_backend.modules.barbero.entity.Barbero;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "comision_venta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComisionVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comision_venta")
    private Integer idComisionVenta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_venta", nullable = false)
    private Venta venta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_barbero", nullable = false)
    private Barbero barbero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_planilla")
    private Planilla planilla;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_periodo")
    private PeriodoPago periodoPago;

    @Column(name = "monto_venta", precision = 10, scale = 2)
    private BigDecimal montoVenta;

    @Column(name = "comision_pct", precision = 5, scale = 2)
    private BigDecimal comisionPct;

    @Column(name = "comision_ganada", precision = 10, scale = 2)
    private BigDecimal comisionGanada;

    @Column(name = "fecha")
    private LocalDateTime fecha;
}