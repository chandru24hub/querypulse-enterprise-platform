package com.querypulse.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class QueryOptimizationService {

    @Value("${anthropic.api.key:}")
    private String anthropicApiKey;

    @Value("${anthropic.api.url:https://api.anthropic.com/v1/messages}")
    private String anthropicApiUrl;

    @Value("${anthropic.api.model:claude-sonnet-4-6}")
    private String anthropicModel;

    private final RestTemplate restTemplate = new RestTemplate();

    public String getQueryOptimizationSuggestions(String sqlQuery) {
        if (anthropicApiKey == null || anthropicApiKey.isEmpty()) {
            log.warn("Anthropic API key not configured. Query optimization disabled.");
            return "Query optimization requires API key configuration.";
        }

        try {
            String prompt = String.format(
                    "You are a database optimization expert. Analyze the following SQL query and provide specific, actionable optimization suggestions. " +
                            "Consider indexing, query structure, and performance improvements. Keep your response concise (max 200 words).\n\n" +
                            "SQL Query:\n%s",
                    sqlQuery
            );

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", anthropicModel);
            requestBody.put("max_tokens", 500);
            requestBody.put("messages", new Object[]{
                    new HashMap<String, String>() {{
                        put("role", "user");
                        put("content", prompt);
                    }}
            });

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-api-key", anthropicApiKey);
            headers.set("anthropic-version", "2023-06-01");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            Map<String, Object> response = restTemplate.postForObject(anthropicApiUrl, entity, Map.class);

            if (response != null && response.containsKey("content")) {
                Object content = response.get("content");
                if (content instanceof java.util.List) {
                    java.util.List<?> contentList = (java.util.List<?>) content;
                    if (!contentList.isEmpty() && contentList.get(0) instanceof Map) {
                        Map<?, ?> firstContent = (Map<?, ?>) contentList.get(0);
                        return (String) firstContent.get("text");
                    }
                }
            }

            return "Unable to generate optimization suggestions.";
        } catch (Exception e) {
            log.error("Error getting query optimization suggestions", e);
            return "Error analyzing query. Please try again later.";
        }
    }
}
