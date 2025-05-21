package com.bajaj.qualifier;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.HashMap;
import java.util.Map;

@Component
public class StartupRunner {
    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        try {
            String url = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
            RestTemplate restTemplate = new RestTemplate();
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("name", "John Doe");
            requestBody.put("regNo", "REG12347");
            requestBody.put("email", "john@example.com");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String webhook = (String) response.getBody().get("webhook");
                String accessToken = (String) response.getBody().get("accessToken");
                System.out.println("Webhook: " + webhook);
                System.out.println("AccessToken: " + accessToken);

                String finalQuery = "SELECT p.AMOUNT AS SALARY, CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS NAME, TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE, d.DEPARTMENT_NAME FROM PAYMENTS p JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID WHERE DAY(p.PAYMENT_TIME) <> 1 ORDER BY p.AMOUNT DESC LIMIT 1;";

                HttpHeaders submitHeaders = new HttpHeaders();
                submitHeaders.setContentType(MediaType.APPLICATION_JSON);
                submitHeaders.set("Authorization", accessToken);
                Map<String, String> submitBody = new HashMap<>();
                submitBody.put("finalQuery", finalQuery);
                HttpEntity<Map<String, String>> submitEntity = new HttpEntity<>(submitBody, submitHeaders);
                ResponseEntity<String> submitResponse = restTemplate.postForEntity(webhook, submitEntity, String.class);
                System.out.println("Submission response: " + submitResponse.getStatusCode());
                System.out.println("Submission body: " + submitResponse.getBody());
            } else {
                System.out.println("Failed to generate webhook: " + response.getStatusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 