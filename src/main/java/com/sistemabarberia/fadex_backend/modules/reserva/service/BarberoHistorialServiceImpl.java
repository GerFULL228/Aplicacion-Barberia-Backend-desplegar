package com.sistemabarberia.fadex_backend.modules.reserva.service;


import com.sistemabarberia.fadex_backend.modules.reserva.dto.Response.HistorialCorteDTO;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.Reserva;
import com.sistemabarberia.fadex_backend.modules.reserva.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BarberoHistorialServiceImpl implements BarberoHistorialService {

    private final ReservaRepository reservaRepository;

    @Override
    public List<HistorialCorteDTO> getHistorial(String username, String desde, String hasta, String clienteNombre) {
        List<Reserva> reservas = reservaRepository.findHistorialByBarberoUsername(
                username, desde, hasta,
                (clienteNombre != null && !clienteNombre.isBlank()) ? clienteNombre : null
        );

        return reservas.stream().map(r -> {
            String nombre  = r.getCliente() != null && r.getCliente().getPersona() != null
                    ? r.getCliente().getPersona().getNombre() : "—";
            String apellido = r.getCliente() != null && r.getCliente().getPersona() != null
                    ? r.getCliente().getPersona().getApellido() : "";

            String servicio  = r.getServicio() != null ? r.getServicio().getNombre() : "—";
            Integer duracion = r.getServicio() != null ? r.getServicio().getDuracion() : null;

            if (duracion == null && r.getHoraInicio() != null && r.getHoraFin() != null) {
                duracion = (int) java.time.Duration.between(r.getHoraInicio(), r.getHoraFin()).toMinutes();
            }

            return HistorialCorteDTO.builder()
                    .reservaId(r.getId())
                    .clienteNombre(nombre)
                    .clienteApellido(apellido)
                    .servicioNombre(servicio)
                    .fecha(r.getFecha())
                    .horaInicio(r.getHoraInicio())
                    .horaFin(r.getHoraFin())
                    .duracion(duracion)
                    .precio(r.getTotal())
                    .estado(r.getEstadoReserva())
                    .build();
        }).toList();
    }
}