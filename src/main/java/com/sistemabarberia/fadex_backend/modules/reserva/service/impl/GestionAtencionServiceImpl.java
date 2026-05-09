package com.sistemabarberia.fadex_backend.modules.reserva.service.impl;

import com.sistemabarberia.fadex_backend.modules.reserva.dto.reponse.ReservaDTO;
import com.sistemabarberia.fadex_backend.modules.reserva.dto.reponse.ResumenDiarioDTO;
import com.sistemabarberia.fadex_backend.modules.reserva.dto.reponse.ResumenSemanalDTO;

import com.sistemabarberia.fadex_backend.modules.reserva.entity.EstadoReserva;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.Reserva;
import com.sistemabarberia.fadex_backend.modules.reserva.mapper.ReservaMapper;
import com.sistemabarberia.fadex_backend.modules.reserva.repository.ReservaRepository;
import com.sistemabarberia.fadex_backend.modules.reserva.service.IGestionAtencionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GestionAtencionServiceImpl implements IGestionAtencionService {

    private final ReservaRepository reservaRepository;
    private final ReservaMapper reservaMapper;

    @Override
    public ResumenDiarioDTO obtenerResumenDiario(Integer barberoId) {

        LocalDate inicio = LocalDate.now();
        LocalDate fin = inicio;

        List<Reserva> reservas = reservaRepository
                .findReservasDiarias(barberoId, inicio, fin);

        List<ReservaDTO> dtos = reservas.stream()
                .map(reservaMapper::toDTO)
                .collect(Collectors.toList());

        return ResumenDiarioDTO.builder()
                .totalDia(dtos.size())
                .enEspera(contar(reservas, EstadoReserva.PENDIENTE))
                .enProceso(contar(reservas, EstadoReserva.EN_PROCESO))
                .completados(contar(reservas, EstadoReserva.COMPLETADO))
                .reservas(dtos)
                .build();
    }

    @Override
    @Transactional
    public ReservaDTO iniciarAtencion(Integer reservaId) {
        Reserva reserva = buscarOFallar(reservaId);

        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new IllegalStateException(
                    "La reserva debe estar en PENDIENTE. Estado actual: "
                            + reserva.getEstado()
            );
        }

        reserva.setEstado(EstadoReserva.EN_PROCESO);
        reserva.setHoraInicio(LocalTime.now());
        return reservaMapper.toDTO(reservaRepository.save(reserva));
    }

    @Override
    @Transactional
    public ReservaDTO finalizarAtencion(Integer reservaId) {
        Reserva reserva = buscarOFallar(reservaId);

        if (reserva.getEstado() != EstadoReserva.EN_PROCESO) {
            throw new IllegalStateException(
                    "La reserva debe estar EN_PROCESO. Estado actual: "
                            + reserva.getEstado()
            );
        }

        reserva.setEstado(EstadoReserva.COMPLETADO);
        reserva.setHoraFin(LocalTime.now());
        return reservaMapper.toDTO(reservaRepository.save(reserva));
    }

    @Override
    public ResumenSemanalDTO obtenerResumenSemanal(Integer barberoId) {

        LocalDate hoy = LocalDate.now();

        List<ResumenSemanalDTO.DiaSemana> dias = hoy.minusDays(6)
                .datesUntil(hoy.plusDays(1))
                .map(fecha -> {

                    LocalDate d = fecha;
                    LocalDate h = fecha;

                    List<Reserva> r = reservaRepository
                            .findByBarberoIdAndFechaBetween(barberoId, d, h);

                    return new ResumenSemanalDTO.DiaSemana(
                            fecha.toString(),
                            contar(r, EstadoReserva.COMPLETADO),
                            contar(r, EstadoReserva.CANCELADO)
                    );
                })
                .collect(Collectors.toList());

        return ResumenSemanalDTO.builder()
                .dias(dias)
                .build();
    }

    private Reserva buscarOFallar(Integer id) {
        return reservaRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Reserva no encontrada: " + id)
                );
    }

    private long contar(List<Reserva> lista, EstadoReserva estado) {
        return lista.stream()
                .filter(r -> r.getEstado() == estado)
                .count();
    }
    @Override
    @Transactional
    public ReservaDTO cancelarReserva(Integer reservaId) {
        Reserva reserva = buscarOFallar(reservaId);

        if (reserva.getEstado() == EstadoReserva.COMPLETADO) {
            throw new IllegalStateException(
                    "No se puede cancelar una reserva COMPLETADA."
            );
        }

        if (reserva.getEstado() == EstadoReserva.CANCELADO) {
            throw new IllegalStateException(
                    "La reserva ya está CANCELADA."
            );
        }

        reserva.setEstado(EstadoReserva.CANCELADO);
        return reservaMapper.toDTO(reservaRepository.save(reserva));
    }

}
