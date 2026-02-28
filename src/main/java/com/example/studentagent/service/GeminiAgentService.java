package com.example.studentagent.service;

import com.example.studentagent.dto.FilterRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class GeminiAgentService {

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Value("${gemini.api.token}")
    private String apiToken;

    private final WebClient webClient;
    private final ObjectMapper mapper;

    @Autowired
    public GeminiAgentService(WebClient.Builder webClientBuilder, ObjectMapper mapper) {
        this.webClient = webClientBuilder.build();
        this.mapper = mapper;
    }

    public FilterRequest getFilterFromNaturalLanguage(String userQuestion) {
        String prompt = """
You are a specialized API assistant. Convert user questions into a JSON `filters` object.


### Available Fields (Matches Student Database) ###

- "name" (String)

- "major" (String)

- "age" (Integer)

- "gpa" (Float)

- "enrollmentDate" (Date: YYYY-MM-DD)


### Rules ###

1. Output ONLY valid JSON.

2. For sorting requests (e.g. "ascending"), add "sortBy": "fieldName" and "sortOrder": "ASC" or "DESC" to the root.

3. Use operators: "equals", "contains", "greaterThan", "lessThan".


### Example ###

User: "Show me CS students with GPA over 3.5"

Response: {

"conditionalOp": "AND",

"filters": [

{"op": "equals", "field": "major", "value": "Computer Science"},

{"op": "greaterThan", "field": "gpa", "value": "3.5"}

]

}


User Question: "%s"

JSON Response:

""".formatted(userQuestion);

        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of("role", "user", "parts", List.of(Map.of("text", prompt)))
                ),
                "generationConfig", Map.of(
                        "temperature", 0.1,
                        "maxOutputTokens", 1024,
                        "responseMimeType", "application/json",
                        "topP", 0.95,
                        "candidateCount", 1
                )
        );

        String jsonResponse = webClient.post()
                .uri(apiUrl)
                .header("Authorization", "Bearer " + apiToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        try {
            JsonNode root = mapper.readTree(jsonResponse);
            String innerJson = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
            return mapper.readValue(innerJson, FilterRequest.class);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing Gemini response", e);
        }
    }
}