package com.sistemabarberia.fadex_backend.modules.persona.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "persona")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_persona")
    private Integer personaId;

    //@ManyToOne(fetch = FetchType.EAGER)
    //@JoinColumn(name = "id_usuario")

    @Column(name = "id_usuario")
    private Integer usuarioId;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "apellido")
    private String apellido;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "email")
    private String email;
}