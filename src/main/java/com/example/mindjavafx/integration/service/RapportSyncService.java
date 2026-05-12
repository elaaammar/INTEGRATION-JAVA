package com.example.mindjavafx.integration.service;

import com.example.mindjavafx.integration.client.ApiConfig;
import com.example.mindjavafx.integration.client.SymfonyApiClient;
import com.example.mindjavafx.integration.dto.RapportDTO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Service de synchronisation CRUD pour le module Rapports et Questions.
 */
public class RapportSyncService {

    private static final Gson gson = new Gson();

    public static List<RapportDTO> getAll() {
        try {
            HttpResponse<String> response = SymfonyApiClient.get(ApiConfig.RAPPORTS_LIST);
            if (response.statusCode() == 200) {
                Type listType = new TypeToken<List<RapportDTO>>() {}.getType();
                List<RapportDTO> list = gson.fromJson(response.body(), listType);
                System.out.println("[RapportSync] ✅ " + list.size() + " rapports récupérés");
                return list;
            }
        } catch (java.net.ConnectException e) {
            System.err.println("[RapportSync] ⚠️ Serveur Symfony non accessible");
        } catch (Exception e) {
            System.err.println("[RapportSync] ❌ Erreur: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    public static RapportDTO getById(int id) {
        try {
            String endpoint = ApiConfig.RAPPORTS_GET.replace("{id}", String.valueOf(id));
            HttpResponse<String> response = SymfonyApiClient.get(endpoint);
            if (response.statusCode() == 200) {
                return gson.fromJson(response.body(), RapportDTO.class);
            }
        } catch (Exception e) {
            System.err.println("[RapportSync] ❌ GET #" + id + ": " + e.getMessage());
        }
        return null;
    }

    public static RapportDTO create(RapportDTO rapport) {
        try {
            HttpResponse<String> response = SymfonyApiClient.post(ApiConfig.RAPPORTS_CREATE, gson.toJson(rapport));
            if (response.statusCode() == 201 || response.statusCode() == 200) {
                System.out.println("[RapportSync] ✅ Rapport créé");
                return gson.fromJson(response.body(), RapportDTO.class);
            }
        } catch (java.net.ConnectException e) {
            System.err.println("[RapportSync] ⚠️ Serveur non accessible");
        } catch (Exception e) {
            System.err.println("[RapportSync] ❌ Erreur: " + e.getMessage());
        }
        return null;
    }

    public static RapportDTO update(int id, RapportDTO rapport) {
        try {
            String endpoint = ApiConfig.RAPPORTS_UPDATE.replace("{id}", String.valueOf(id));
            HttpResponse<String> response = SymfonyApiClient.put(endpoint, gson.toJson(rapport));
            if (response.statusCode() == 200) {
                System.out.println("[RapportSync] ✅ Rapport #" + id + " mis à jour");
                return gson.fromJson(response.body(), RapportDTO.class);
            }
        } catch (Exception e) {
            System.err.println("[RapportSync] ❌ Erreur: " + e.getMessage());
        }
        return null;
    }

    public static boolean delete(int id) {
        try {
            String endpoint = ApiConfig.RAPPORTS_DELETE.replace("{id}", String.valueOf(id));
            HttpResponse<String> response = SymfonyApiClient.delete(endpoint);
            if (response.statusCode() == 200 || response.statusCode() == 204) {
                System.out.println("[RapportSync] ✅ Rapport #" + id + " supprimé");
                return true;
            }
        } catch (Exception e) {
            System.err.println("[RapportSync] ❌ Erreur: " + e.getMessage());
        }
        return false;
    }

    public static List<RapportDTO.QuestionDTO> getQuestions(int rapportId) {
        try {
            String endpoint = ApiConfig.QUESTIONS_LIST.replace("{id}", String.valueOf(rapportId));
            HttpResponse<String> response = SymfonyApiClient.get(endpoint);
            if (response.statusCode() == 200) {
                Type listType = new TypeToken<List<RapportDTO.QuestionDTO>>() {}.getType();
                return gson.fromJson(response.body(), listType);
            }
        } catch (Exception e) {
            System.err.println("[RapportSync] ❌ GET questions: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    public static RapportDTO.QuestionDTO createQuestion(int rapportId, RapportDTO.QuestionDTO question) {
        try {
            String endpoint = ApiConfig.QUESTIONS_CREATE.replace("{id}", String.valueOf(rapportId));
            HttpResponse<String> response = SymfonyApiClient.post(endpoint, gson.toJson(question));
            if (response.statusCode() == 201 || response.statusCode() == 200) {
                System.out.println("[RapportSync] ✅ Question ajoutée au rapport #" + rapportId);
                return gson.fromJson(response.body(), RapportDTO.QuestionDTO.class);
            }
        } catch (Exception e) {
            System.err.println("[RapportSync] ❌ Erreur: " + e.getMessage());
        }
        return null;
    }
}
