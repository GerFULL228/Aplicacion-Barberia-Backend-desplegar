package com.sistemabarberia.fadex_backend.modules.reclamo.dto;


import com.sistemabarberia.fadex_backend.modules.reclamo.entity.enums.CausaReclamo;
import com.sistemabarberia.fadex_backend.modules.reclamo.entity.enums.EstadoReclamo;
import com.sistemabarberia.fadex_backend.modules.reclamo.entity.enums.TipoProblema;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReclamoFiltro {
    private String numeroReclamo;
    private EstadoReclamo estado;
    private TipoProblema tipoProblema;
    private CausaReclamo causa;
    private Boolean esPublico;
    private String  numeroDocumentoCliente;
    private Integer idResponsable;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
}
