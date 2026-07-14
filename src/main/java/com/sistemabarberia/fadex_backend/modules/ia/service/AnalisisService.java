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
        Map<String, Map<String, Object>> datosAnalisis = new LinkedHashMap<>();
        LocalDate hoy = LocalDate.now();

        for (Reserva r : reservas) {
            if (r.getCliente() == null || r.getCliente().getPersona() == null) continue;

            String nombre = r.getCliente().getPersona().getNombre()
                    + " " + r.getCliente().getPersona().getApellido();

            datosAnalisis.computeIfAbsent(nombre, k -> {
                Map<String, Object> info = new LinkedHashMap<>();
                info.put("visitas", new ArrayList<LocalDate>());
                return info;
            });

            Map<String, Object> info = datosAnalisis.get(nombre);
            @SuppressWarnings("unchecked")
            List<LocalDate> visitas = (List<LocalDate>) info.get("visitas");
            if (r.getFecha() != null) visitas.add(r.getFecha());
        }

        // Construir datos ya calculados para el prompt
        List<Map<String, Object>> clientesCalculados = new ArrayList<>();

        for (Map.Entry<String, Map<String, Object>> entry : datosAnalisis.entrySet()) {
            Map<String, Object> info = entry.getValue();
            @SuppressWarnings("unchecked")
            List<LocalDate> visitas = (List<LocalDate>) info.get("visitas");

            // Solo contar visitas pasadas
            List<LocalDate> visitasPasadas = visitas.stream()
                    .filter(f -> !f.isAfter(hoy))
                    .collect(java.util.stream.Collectors.toList());

            // Última visita pasada
            LocalDate ultimaVisita = visitasPasadas.stream()
                    .max(Comparator.naturalOrder())
                    .orElse(null);

            long diasSinVisita = ultimaVisita != null
                    ? hoy.toEpochDay() - ultimaVisita.toEpochDay()
                    : -1;

            // Clasificar en Java directamente
            String nivelRiesgo;
            String razon;

            if (visitasPasadas.size() == 0) {
                continue;
            }

            if (diasSinVisita < 0) {
                continue;
            }

            if (visitasPasadas.size() == 1 && diasSinVisita > 30) {
                nivelRiesgo = "alto";
                razon = "Cliente nuevo sin fidelizar y lleva más de 30 días sin visita";
            } else if (visitasPasadas.size() == 1) {
                nivelRiesgo = "alto";
                razon = "Cliente nuevo, solo ha visitado una vez, no fidelizado";
            } else if (diasSinVisita > 30) {
                nivelRiesgo = "alto";
                razon = "Lleva más de 30 días sin visita";
            } else if (diasSinVisita >= 20) {
                nivelRiesgo = "medio";
                razon = "Entre 20 y 30 días sin visita";
            } else {
                nivelRiesgo = "bajo";
                razon = "Visitas recientes, cliente activo";
            }

            Map<String, Object> cliente = new LinkedHashMap<>();
            cliente.put("nombreCliente", entry.getKey());
            cliente.put("razon", razon);
            cliente.put("nivelRiesgo", nivelRiesgo);
            cliente.put("diasSinVisita", diasSinVisita);
            cliente.put("totalVisitas", visitasPasadas.size());
            clientesCalculados.add(cliente);
        }

        String historialJson = objectMapper.writeValueAsString(clientesCalculados);

        String prompt = """
                Eres un experto en fidelización de clientes para barberías.
                Los siguientes clientes ya tienen calculado su nivel de riesgo, días sin visita y total de visitas pasadas.
                Tu tarea es:
                1. Redactar una razón más natural y descriptiva en español para cada cliente
                2. Escribir un resumen general del estado de fidelización
                3. Respetar el nivelRiesgo ya asignado, no lo cambies
                
                Devuelve SOLAMENTE este JSON sin markdown ni texto extra:
                {
                  "resumen": "...",
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
                
                Datos de clientes:
                %s
                """.formatted(historialJson);

        return openAIService.consultarOpenAI(prompt);
    }
}
