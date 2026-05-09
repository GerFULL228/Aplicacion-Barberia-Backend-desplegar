package com.sistemabarberia.fadex_backend.modules.reserva.entity;
import com.sistemabarberia.fadex_backend.modules.barbero.entity.Barbero;
import com.sistemabarberia.fadex_backend.modules.cliente.entity.Cliente;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "reservas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reservas")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_barbero")
    private Barbero barbero;

    @Column(name = "fecha")
    private LocalDate fecha;

    @Column(name = "hora_inicio")
    private LocalTime horaInicio;

    @Column(name = "hora_fin")
    private LocalTime horaFin;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoReserva estado;

    @Column(name = "tipo_reservas")
    private String tipoReserva;

    @OneToMany(mappedBy = "reserva", fetch = FetchType.LAZY)
    private List<DetalleReserva> detalles;
}
