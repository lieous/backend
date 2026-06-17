package com.diploma.backend.stats.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.*;
import java.net.http.HttpClient;
import java.security.cert.X509Certificate;
import java.util.*;

@Service
public class GigaChatService {

    @Value("${gigachat.credentials:}")
    private String credentials;

    private final RestTemplate restTemplate = createInsecureRestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String accessToken = null;
    private long tokenExpiresAt = 0;


    private RestTemplate createInsecureRestTemplate() {
        try {

            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                    }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());


            HttpClient httpClient = HttpClient.newBuilder()
                    .sslContext(sslContext)
                    .build();


            JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
            return new RestTemplate(requestFactory);
        } catch (Exception e) {
            System.err.println("Не удалось настроить обход SSL: " + e.getMessage());
            return new RestTemplate();
        }
    }

    private String getAccessToken() {
        if (accessToken != null && System.currentTimeMillis() < tokenExpiresAt) {
            return accessToken;
        }
        if (credentials == null || credentials.isBlank()) {
            System.err.println("GigaChat credentials пустые! Заполните их в application.yml");
            return null;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + credentials);
        headers.set("RqUID", UUID.randomUUID().toString());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> request = new HttpEntity<>("scope=GIGACHAT_API_PERS", headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://ngw.devices.sberbank.ru:9443/api/v2/oauth",
                    request,
                    String.class
            );

            JsonNode root = objectMapper.readTree(response.getBody());
            accessToken = root.path("access_token").asText();
            tokenExpiresAt = root.path("expires_at").asLong();
            return accessToken;
        } catch (Exception e) {
            System.err.println("Ошибка получения токена GigaChat:");
            e.printStackTrace();
            return null;
        }
    }

    public String generateInsight(String prompt) {
        String token = getAccessToken();
        if (token == null) return "Нейросеть временно недоступна. Ошибка авторизации.";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            Map<String, Object> requestBodyMap = new HashMap<>();
            requestBodyMap.put("model", "GigaChat-2");
            requestBodyMap.put("temperature", 0.7);

            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);
            messages.add(message);

            requestBodyMap.put("messages", messages);

            String jsonBody = objectMapper.writeValueAsString(requestBodyMap);
            HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://gigachat.devices.sberbank.ru/api/v1/chat/completions",
                    request,
                    String.class
            );

            JsonNode root = objectMapper.readTree(response.getBody());
            return root.path("choices").get(0).path("message").path("content").asText();

        } catch (Exception e) {
            System.err.println("Ошибка генерации GigaChat:");
            e.printStackTrace();
            return "Не удалось проанализировать неделю.";
        }
    }
}