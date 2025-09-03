package com.bajaj.test.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    
    @Bean
    public CommandLineRunner run(RestTemplate restTemplate) {
        return args -> {
            System.out.println("🚀 Starting the hiring challenge process...");

            
            String generateWebhookUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";;
            GenerateWebhookRequest initialRequest = new GenerateWebhookRequest(
                "SHASHANK RAGHURAM",      
                "IRF23IS407",       
                "SHASHANKR829@GMAIL.COM" 
            );

            System.out.println("   [1/3] Sending request to generate webhook...");
            GenerateWebhookResponse webhookResponse = restTemplate.postForObject(
                generateWebhookUrl,
                initialRequest,
                GenerateWebhookResponse.class
            );

            if (webhookResponse == null || webhookResponse.accessToken() == null || webhookResponse.webhookURL() == null) {
                System.err.println("Failed to get webhook URL or access token. Exiting.");
                return;
            }

            String submitUrl = webhookResponse.webhookURL();
            String accessToken = webhookResponse.accessToken();
            System.out.println("   Webhook URL and Access Token received!");

            
            String finalQuery = """
            SELECT
                p.AMOUNT AS SALARY,
                CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS NAME,
                TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE,
                d.DEPARTMENT_NAME
            FROM
                PAYMENTS p
            JOIN
                EMPLOYEE e ON p.EMP_ID = e.EMP_ID
            JOIN
                DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID
            WHERE
                EXTRACT(DAY FROM p.PAYMENT_TIME) <> 1
            ORDER BY
                p.AMOUNT DESC
            LIMIT 1;
            """;
            
            SubmitQueryRequest solutionRequest = new SubmitQueryRequest(finalQuery);

            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", accessToken);

            HttpEntity<SubmitQueryRequest> entity = new HttpEntity<>(solutionRequest, headers);

            System.out.println("   [2/3] Submitting the final SQL query...");
            String result = restTemplate.postForObject(submitUrl, entity, String.class);

            System.out.println("   [3/3] Response from server: " + result);
            System.out.println("Process completed successfully!");
        };
    }
}