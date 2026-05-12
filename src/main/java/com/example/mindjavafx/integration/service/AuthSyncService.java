package com.example.mindjavafx.integration.service;

import com.example.mindjavafx.integration.client.ApiConfig;
import com.example.mindjavafx.integration.client.SymfonyApiClient;
import com.example.mindjavafx.integration.dto.LoginDTO;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.http.HttpResponse;

/**
 * Service d'authentification pour la synchronisation JavaFX ↔ Symfony.
 * Gère le login JWT et stocke le token pour les appels suivants.
 */
public class AuthSyncService {

    private static final Gson gson = new Gson();
    private static String currentToken = null;
    private static JsonObject currentUserData = null;

    /**
     * Authentifie un utilisateur auprès de Symfony et stocke le token JWT.
     *
     * @param email    L'email de l'utilisateur
     * @param password Le mot de passe
     * @return true si l'authentification a réussi
     */
    public static boolean login(String email, String password) {
        try {
            LoginDTO loginDTO = new LoginDTO(email, password);
            String json = gson.toJson(loginDTO);

            HttpResponse<String> response = SymfonyApiClient.post(ApiConfig.AUTH_LOGIN, json);

            if (response.statusCode() == 200) {
                JsonObject body = JsonParser.parseString(response.body()).getAsJsonObject();

                // Récupérer le token JWT (selon le format Symfony: {"token": "xxx"} ou {"data":{"token":"xxx"}})
                if (body.has("token")) {
                    currentToken = body.get("token").getAsString();
                } else if (body.has("data") && body.getAsJsonObject("data").has("token")) {
                    currentToken = body.getAsJsonObject("data").get("token").getAsString();
                }

                // Stocker les données utilisateur si disponibles
                if (body.has("user")) {
                    currentUserData = body.getAsJsonObject("user");
                } else if (body.has("data") && body.getAsJsonObject("data").has("user")) {
                    currentUserData = body.getAsJsonObject("data").getAsJsonObject("user");
                }

                // Mettre à jour le client HTTP avec le token
                if (currentToken != null) {
                    SymfonyApiClient.setJwtToken(currentToken);
                    System.out.println("[AuthSync] ✅ Authentification Symfony réussie");
                    return true;
                }
            }

            System.err.println("[AuthSync] ❌ Échec authentification Symfony - Status: " + response.statusCode());
            System.err.println("[AuthSync] Réponse: " + response.body());
            return false;

        } catch (java.net.ConnectException e) {
            System.err.println("[AuthSync] ⚠️ Serveur Symfony non accessible à " + ApiConfig.getBaseUrl());
            System.err.println("[AuthSync] Assurez-vous que Symfony tourne: symfony server:start");
            return false;
        } catch (Exception e) {
            System.err.println("[AuthSync] ❌ Erreur d'authentification: " + e.getMessage());
            return false;
        }
    }

    /**
     * Déconnecte l'utilisateur côté Symfony.
     */
    public static void logout() {
        currentToken = null;
        currentUserData = null;
        SymfonyApiClient.setJwtToken(null);
        System.out.println("[AuthSync] Déconnexion Symfony");
    }

    /**
     * Vérifie si l'utilisateur est connecté côté Symfony.
     */
    public static boolean isAuthenticated() {
        return currentToken != null && !currentToken.isEmpty();
    }

    /**
     * Retourne le token JWT courant.
     */
    public static String getToken() {
        return currentToken;
    }

    /**
     * Retourne les données de l'utilisateur connecté.
     */
    public static JsonObject getCurrentUserData() {
        return currentUserData;
    }

    /**
     * Teste la connexion au serveur Symfony.
     *
     * @return true si le serveur est accessible
     */
    public static boolean testConnection() {
        try {
            HttpResponse<String> response = SymfonyApiClient.get("/ping");
            boolean ok = response.statusCode() == 200;
            System.out.println("[AuthSync] Test connexion Symfony: " + (ok ? "✅ OK" : "❌ ÉCHEC"));
            return ok;
        } catch (Exception e) {
            System.err.println("[AuthSync] ⚠️ Serveur Symfony non accessible: " + e.getMessage());
            return false;
        }
    }
}
