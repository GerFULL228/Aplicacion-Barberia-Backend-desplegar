package com.sistemabarberia.fadex_backend.modules.reserva.controller;

import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.reserva.dto.ReservaFiltro;
import com.sistemabarberia.fadex_backend.modules.reserva.dto.Response.ReservaDTO;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.EstadoReserva;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.TipoReserva;
import com.sistemabarberia.fadex_backend.modules.reserva.service.ReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/admin/reservas")
@RequiredArgsConstructor
public class ReservaControllerAdmin {

    private final ReservaService reservaService;

    @PreAuthorize("hasAuthority('RESERVA_READ_ALL')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ReservaDTO>>> listarReservas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String clienteNombre,
            @RequestParam(required = false) String barberoNombre,
            @RequestParam(required = false) EstadoReserva estadoReserva,
            @RequestParam(required = false) TipoReserva tipoReserva,
            @RequestParam(required = false) LocalDate fecha,
            @RequestParam(required = false) LocalDate fechaDesde,
            @RequestParam(required = false) LocalDate fechaHasta) {

        ReservaFiltro filtro = new ReservaFiltro();
        filtro.setClienteNombre(clienteNombre);
        filtro.setBarberoNombre(barberoNombre);
        filtro.setEstadoReserva(estadoReserva);
        filtro.setTipoReserva(tipoReserva);
        filtro.setFecha(fecha);
        filtro.setFechaDesde(fechaDesde);
        filtro.setFechaHasta(fechaHasta);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fecha").and(Sort.by(Sort.Direction.ASC, "horaInicio")));

        PageResponse<ReservaDTO> result = reservaService.listarReservasAdmin(filtro, pageable);

        return ResponseEntity.ok(
                ApiResponse.ok("Reservas obtenidas correctamente", result)
        );
    }
}