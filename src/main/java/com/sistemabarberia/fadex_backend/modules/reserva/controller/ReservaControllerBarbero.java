package com.sistemabarberia.fadex_backend.modules.reserva.controller;

import com.sistemabarberia.fadex_backend.auth.security.service.CustomUserDetails;
import com.sistemabarberia.fadex_backend.auth.usuario.Entity.Usuario;
import com.sistemabarberia.fadex_backend.modules.reserva.dto.Response.ReservaDTO;
import com.sistemabarberia.fadex_backend.modules.reserva.service.ReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/barbero/reservas")
public class ReservaControllerBarbero {

    private final ReservaService  reservaService;

    @PreAuthorize("hasAuthority('RESERVA_READ_ASSIGNED')")
    @GetMapping
    public ResponseEntity<List<ReservaDTO>> ListarReservasBarbero(@AuthenticationPrincipal CustomUserDetails details) {



        return ResponseEntity.ok(reservaService.ListarReservasBarbero(details.getUsuario()));
    }
}
