package com.sistemabarberia.fadex_backend.modules.reserva.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.Reserva;
import com.sistemabarberia.fadex_backend.modules.reserva.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RecordatorioCronService {

    private final ReservaRepository reservaRepository;
    private final String N8N_WEBHOOK_URL = "AQUÍ VA TU URL DE WEBHOOK DE N8N";

    @Scheduled(cron = "0 */5 * * * *")
    @Transactional
    public void procesarRecordatorios() {
        LocalDate hoy = LocalDate.now();
        LocalTime ahora = LocalTime.now();

        LocalTime rangoInicio = ahora.plusMinutes(25);
        LocalTime rangoFin = ahora.plusMinutes(35);

        List<Reserva> reservasParaAvisar = reservaRepository.findReservasParaRecordatorio(hoy, rangoInicio, rangoFin);

        if (reservasParaAvisar.isEmpty()) {
            return;
        }

        for (Reserva reserva : reservasParaAvisar) {
            boolean exitoEnvio = notificarAn8n(reserva);

            if (exitoEnvio) {
                reserva.setRecordatorioEnviado(true);
                reservaRepository.save(reserva);
                System.out.println(" FADEX: Recordatorio enviado a n8n para la reserva ID: " + reserva.getId());
            }
        }
    }

    private boolean notificarAn8n(Reserva reserva) {
        try {
            String telefonoCrudo = reserva.getCliente().getPersona().getTelefono();
            String nombreCliente = reserva.getCliente().getPersona().getNombre();
            String nombreBarbero = reserva.getBarbero().getPersona().getNombre();

            String telefonoFormateado = formatearTelefonoParaWhatsapp(telefonoCrudo);

            if (telefonoFormateado.isEmpty()) {
                System.err.println("FADEX ERROR: El cliente no tiene teléfono válido. Reserva ID: " + reserva.getId());
                return false;
            }

            Map<String, String> payload = new HashMap<>();
            payload.put("numero", telefonoFormateado);
            payload.put("mensaje", String.format(
                    "¡Hola %s! 💈 Te recordamos que tu cita en Fadex con %s comienza a las %s. ¡Te esperamos!",
                    nombreCliente,
                    nombreBarbero,
                    reserva.getHoraInicio().toString()
            ));

            String jsonPayload = new ObjectMapper().writeValueAsString(payload);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(N8N_WEBHOOK_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            return response.statusCode() >= 200 && response.statusCode() < 300;

        } catch (Exception e) {
            System.err.println("FADEX ERROR conectando a n8n: " + e.getMessage());
            return false;
        }
    }

    private String formatearTelefonoParaWhatsapp(String telefono) {
        if (telefono == null || telefono.trim().isEmpty()) {
            return "";
        }
        String limpio = telefono.replaceAll("[\\s\\-+]", "");
        if (limpio.length() == 9) {
            return "51" + limpio;
        }
        return limpio;
    }
}