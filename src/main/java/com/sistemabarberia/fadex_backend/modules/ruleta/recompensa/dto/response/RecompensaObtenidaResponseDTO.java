package com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.dto.response;

import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.entity.enums.EstadoRecompensa;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RecompensaObtenidaResponseDTO {
    private Long id;
    private Long giroId;
    private Long clienteId;
    private String clienteNombre;
    private Long itemId;
    private String itemNombre;
    private EstadoRecompensa estado;
    private String observacion;
    private Long usuarioCanjeId;
    private LocalDateTime fechaObtencion;
    private LocalDateTime fechaVencimiento;
    private LocalDateTime fechaCanje;
    private String codigoCanje;
    private LocalDateTime createdAt;
}