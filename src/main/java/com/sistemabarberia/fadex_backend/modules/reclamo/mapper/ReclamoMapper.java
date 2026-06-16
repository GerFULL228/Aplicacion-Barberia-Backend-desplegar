package com.sistemabarberia.fadex_backend.modules.reclamo.mapper;

import com.sistemabarberia.fadex_backend.modules.reclamo.dto.response.ReclamoResponse;
import com.sistemabarberia.fadex_backend.modules.reclamo.entity.Reclamo;
import org.springframework.stereotype.Component;

@Component
public class ReclamoMapper {
    public ReclamoResponse toResponse(Reclamo reclamo) {
        return ReclamoResponse.builder()
                .idReclamo(reclamo.getIdReclamo())
                .numeroReclamo(reclamo.getNumeroReclamo())
                .nombreCliente(reclamo.getNombreCliente())
                .correoCliente(reclamo.getCorreoCliente())
                .telefonoCliente(reclamo.getTelefonoCliente())
                .tipoReclamacion(reclamo.getTipoReclamacion())
                .tipoProblema(reclamo.getTipoProblema())
                .causaReclamo(reclamo.getCausaReclamo())
                .estadoReclamo(reclamo.getEstadoReclamo())
                .solucionReclamo(reclamo.getSolucionReclamo())
                .descripcion(reclamo.getDescripcion())
                .notasInternas(reclamo.getNotasInternas())
                .montoReclamado(reclamo.getMontoReclamado())
                .montoCompensado(reclamo.getMontoCompensado())
                .fechaOcurrencia(reclamo.getFechaOcurrencia())
                .fechaReclamo(reclamo.getFechaReclamo())
                .fechaResolucion(reclamo.getFechaResolucion())
                .esPublico(reclamo.isEsPublico())
                .build();
    }
}