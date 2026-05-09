package com.sistemabarberia.fadex_backend.modules.reserva.entity;

import com.sistemabarberia.fadex_backend.modules.servicio.entity.Corte;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "detalle_reservas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleReserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_reserva")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reservas")
    private Reserva reserva;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_corte")
    private Corte corte;
}
