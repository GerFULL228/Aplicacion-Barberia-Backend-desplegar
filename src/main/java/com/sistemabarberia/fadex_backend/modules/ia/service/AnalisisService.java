package com.sistemabarberia.fadex_backend.modules.ia.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.Reserva;
import com.sistemabarberia.fadex_backend.modules.reserva.repository.ReservaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class AnalisisService {

    private final ReservaRepository reservaRepository;
    private final ObjectMapper objectMapper;
    private final OpenAIService openAIService;

    public AnalisisService(ReservaRepository reservaRepository,
                           ObjectMapper objectMapper,
                           OpenAIService openAIService) {
        this.reservaRepository = reservaRepository;
        this.objectMapper = objectMapper;
        this.openAIService = openAIService;
    }

    public String analizarClientesEnRiesgo() throws Exception {

        List<Reserva> reservas = reservaRepository.findAll();
        Map<String, Object> datosAnalisis = new LinkedHashMap<>();
        LocalDate hoy = LocalDate.now();

        for (Reserva r : reservas) {
            if (r.getCliente() == null || r.getCliente().getPersona() == null) continue;

            String nombre = r.getCliente().getPersona().getNombre()
                    + " " + r.getCliente().getPersona().getApellido();

            datosAnalisis.computeIfAbsent(nombre, k -> {
                Map<String, Object> info = new LinkedHashMap<>();
                info.put("visitas", new ArrayList<String>());
                return info;
            });

            @SuppressWarnings("unchecked")
            Map<String, Object> info = (Map<String, Object>) datosAnalisis.get(nombre);
            @SuppressWarnings("unchecked")
            List<String> visitas = (List<String>) info.get("visitas");
            if (r.getFecha() != null) visitas.add(r.getFecha().toString());
        }


        for (Map.Entry<String, Object> entry : datosAnalisis.entrySet()) {
            @SuppressWarnings("unchecked")
            Map<String, Object> info = (Map<String, Object>) entry.getValue();
            @SuppressWarnings("unchecked")
            List<String> visitas = (List<String>) info.get("visitas");

            info.put("totalVisitas", visitas.size());

            visitas.stream()
                    .map(LocalDate::parse)
                    .max(Comparator.naturalOrder())
                    .ifPresent(ultima -> {
                        info.put("ultimaVisita", ultima.toString());
                        info.put("diasSinVisita", hoy.toEpochDay() - ultima.toEpochDay());
                    });
        }

        String historialJson = objectMapper.writeValueAsString(datosAnalisis);

        String prompt = """
                Eres un experto en fidelización de clientes para barberías.
                Analiza el siguiente historial de visitas y determina cuáles están en riesgo de no volver.

                Reglas de clasificación:
                - Más de 30 días sin visita: riesgo alto
                - Entre 20 y 30 días sin visita: riesgo medio
                - Menos de 20 días sin visita: riesgo bajo
                - Solo 1 visita en total: riesgo alto (no fidelizado)

                Responde ÚNICAMENTE con un JSON válido, sin texto adicional, sin bloques de código markdown:
                {
                  "resumen": "descripción breve del estado general de los clientes",
                  "clientesEnRiesgo": [
                    {
                      "nombreCliente": "...",
                      "razon": "...",
                      "nivelRiesgo": "alto|medio|bajo",
                      "diasSinVisita": 0,
                      "totalVisitas": 0
                    }
                  ]
                }

                Historial de clientes:
                %s
                """.formatted(historialJson);

        return openAIService.consultarOpenAI(prompt);
    }
}
