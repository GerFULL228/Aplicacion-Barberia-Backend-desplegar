package com.sistemabarberia.fadex_backend.modules.venta.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "periodo_pago")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeriodoPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_periodo")
    private Integer idPeriodo;

    private String nombre;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    private String estado;
}