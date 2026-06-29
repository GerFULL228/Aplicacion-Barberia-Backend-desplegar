package com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.dto;

import com.sistemabarberia.fadex_backend.modules.ruleta.recompensa.entity.enums.EstadoRecompensa;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecompensaObtenidaFiltro {
    private Long clienteId;
    private EstadoRecompensa estado;
    private Long itemId;
}