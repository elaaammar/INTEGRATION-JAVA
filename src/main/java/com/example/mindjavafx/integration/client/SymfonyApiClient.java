package com.example.mindjavafx.integration.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Client HTTP natif (Java 11+) pour communiquer avec l'API Symfony.
 */
public class SymfonyApiClient {

    // L'URL de base de votre serveur Symfony local
    private static final String BASE_URL = "http://127.0.0.1:8000/api";
    
    // Token JWT pour l'authentification (si l'API Symfony est sécurisée)
    private static String jwtToken = null;

    private static final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public static void setJwtToken(String token) {
        jwtToken = token;
    }

    /**
     * Effectue une requête GET.
     */
    public static HttpResponse<String> get(String endpoint) throws Exception {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .GET()
                .header("Accept", "application/json");
                
        addAuthHeader(requestBuilder);

        HttpRequest request = requestBuilder.build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Effectue une requête POST avec un corps JSON.
     */
    public static HttpResponse<String> post(String endpoint, String jsonBody) throws Exception {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody));

        addAuthHeader(requestBuilder);

        HttpRequest request = requestBuilder.build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Effectue une requête PUT avec un corps JSON.
     */
    public static HttpResponse<String> put(String endpoint, String jsonBody) throws Exception {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody));

        addAuthHeader(requestBuilder);

        HttpRequest request = requestBuilder.build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Effectue une requête DELETE.
     */
    public static HttpResponse<String> delete(String endpoint) throws Exception {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .DELETE()
                .header("Accept", "application/json");

        addAuthHeader(requestBuilder);

        HttpRequest request = requestBuilder.build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Ajoute le header d'authentification Bearer si un token est présent.
     */
    private static void addAuthHeader(HttpRequest.Builder requestBuilder) {
        if (jwtToken != null && !jwtToken.isEmpty()) {
            requestBuilder.header("Authorization", "Bearer " + jwtToken);
        }
    }
}
