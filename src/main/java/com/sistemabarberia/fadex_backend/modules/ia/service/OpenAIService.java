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

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    @jakarta.annotation.PostConstruct
    public void init() {
        System.out.println(" OPENAI CONFIG ");
        System.out.println("URL: " + apiUrl);
        System.out.println("KEY: " + (apiKey != null ? apiKey.substring(0, 10) + "..." : "NULL"));
    }
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
