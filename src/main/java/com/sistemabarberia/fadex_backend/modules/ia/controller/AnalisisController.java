package com.sistemabarberia.fadex_backend.modules.ia.controller;


import com.sistemabarberia.fadex_backend.modules.ia.service.AnalisisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/analisis")
@CrossOrigin(origins = "http://localhost:4200")
public class AnalisisController {

    private final AnalisisService analisisService;

    public AnalisisController(AnalisisService analisisService) {
        this.analisisService = analisisService;
    }

    @GetMapping("/clientes-en-riesgo")
    public ResponseEntity<String> clientesEnRiesgo() {
        try {
            String resultado = analisisService.analizarClientesEnRiesgo();
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
