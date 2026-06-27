package com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfiguracionRequestDTO {

    @NotNull
    private Long categoriaId;

    @NotNull
    private Boolean activa;

    @NotNull
    @Min(1)
    private Integer meta;

    @NotNull
    private Boolean mostrarSiempre;

    @NotNull
    private Boolean crearTarjetaAutomatica;
    private Long ruletaId;
}
