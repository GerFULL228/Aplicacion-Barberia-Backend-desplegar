package com.sistemabarberia.fadex_backend.modules.ia.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.util.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Service

public class OpenAIService {

    private String apiKey = "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";
    private String apiUrl = "https://api.openai.com/v1/responses";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String consultarOpenAI(String prompt) {
        RestTemplate restTemplate = new RestTemplate();

        String requestBody = """
        {
            "model": "gpt-4o-mini",
            "input": "%s"
        }
        """.formatted(prompt
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", ""));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            JsonNode root = objectMapper.readTree(response.getBody());
            String textoIA = root
                    .path("output").get(0)
                    .path("content").get(0)
                    .path("text").asText();

            return textoIA;

        } catch (HttpStatusCodeException e) {
            return "{\"error\": \"ERROR OPENAI: " + e.getStatusCode() + "\"}";
        } catch (Exception e) {
            return "{\"error\": \"ERROR GENERAL: " + e.getMessage() + "\"}";
        }
    }
}
