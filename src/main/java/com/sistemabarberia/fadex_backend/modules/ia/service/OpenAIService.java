package com.sistemabarberia.fadex_backend.modules.ia.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Service

public class OpenAIService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @jakarta.annotation.PostConstruct
    public void init() {
        System.out.println(" GEMINI CONFIG ");
        System.out.println("URL: " + apiUrl);
        System.out.println("KEY: " + (apiKey != null ? apiKey.substring(0, 10) + "..." : "NULL"));
    }

    public String consultarOpenAI(String prompt) {
        RestTemplate restTemplate = new RestTemplate();

        String promptEscapado = prompt
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");

        String requestBody = """
        {
            "contents": [{
                "parts": [{"text": "%s"}]
            }]
        }
        """.formatted(promptEscapado);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl + "?key=" + apiKey,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            JsonNode root = objectMapper.readTree(response.getBody());
            String textoIA = root
                    .path("candidates").get(0)
                    .path("content")
                    .path("parts").get(0)
                    .path("text").asText();

            textoIA = textoIA.trim();
            if (textoIA.startsWith("```")) {
                textoIA = textoIA.replaceAll("^```(?:json)?\\s*", "").replaceAll("```\\s*$", "").trim();
            }

            return textoIA;

        } catch (HttpStatusCodeException e) {
            return "{\"error\": \"ERROR GEMINI: " + e.getStatusCode() + " - " + e.getResponseBodyAsString() + "\"}";
        } catch (Exception e) {
            return "{\"error\": \"ERROR GENERAL: " + e.getMessage() + "\"}";
        }
    }
}
