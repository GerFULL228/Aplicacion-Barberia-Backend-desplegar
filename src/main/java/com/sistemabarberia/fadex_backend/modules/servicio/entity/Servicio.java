package com.sistemabarberia.fadex_backend.modules.servicio.entity;

import com.sistemabarberia.fadex_backend.modules.categoria.entity.Categoria;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "cortes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Servicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_corte")
    private Long servicioId;

    @Column(name = "nombre", length = 100)
    private String nombre;

    @Column(name = "precio")
    private BigDecimal precio;

    @ManyToOne
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoriaId;

    private Integer duracion;

}
