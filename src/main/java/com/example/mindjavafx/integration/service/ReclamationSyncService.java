package com.example.mindjavafx.integration.service;

import com.example.mindjavafx.integration.client.ApiConfig;
import com.example.mindjavafx.integration.client.SymfonyApiClient;
import com.example.mindjavafx.integration.dto.ReclamationDTO;
import com.example.mindjavafx.integration.dto.ReponseReclamationDTO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Service de synchronisation CRUD pour le module Réclamations.
 * Communication JavaFX ↔ Symfony via API REST.
 */
public class ReclamationSyncService {

    private static final Gson gson = new Gson();

    // ═══════════════════════════════════════════════════════════════
    //  RÉCLAMATIONS — CRUD
    // ═══════════════════════════════════════════════════════════════

    public static List<ReclamationDTO> getAll() {
        try {
            HttpResponse<String> response = SymfonyApiClient.get(ApiConfig.RECLAMATIONS_LIST);
            if (response.statusCode() == 200) {
                Type listType = new TypeToken<List<ReclamationDTO>>() {}.getType();
                List<ReclamationDTO> list = gson.fromJson(response.body(), listType);
                System.out.println("[ReclamationSync] ✅ " + list.size() + " réclamations récupérées");
                return list;
            }
        } catch (java.net.ConnectException e) {
            System.err.println("[ReclamationSync] ⚠️ Serveur Symfony non accessible");
        } catch (Exception e) {
            System.err.println("[ReclamationSync] ❌ Erreur: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    public static ReclamationDTO getById(int id) {
        try {
            HttpResponse<String> response = SymfonyApiClient.get(
                    ApiConfig.RECLAMATIONS_GET.replace("{id}", String.valueOf(id)));
            if (response.statusCode() == 200) {
                return gson.fromJson(response.body(), ReclamationDTO.class);
            }
        } catch (Exception e) {
            System.err.println("[ReclamationSync] ❌ Erreur GET #" + id + ": " + e.getMessage());
        }
        return null;
    }

    public static ReclamationDTO create(ReclamationDTO reclamation) {
        try {
            String json = gson.toJson(reclamation);
            HttpResponse<String> response = SymfonyApiClient.post(ApiConfig.RECLAMATIONS_CREATE, json);
            if (response.statusCode() == 201 || response.statusCode() == 200) {
                ReclamationDTO created = gson.fromJson(response.body(), ReclamationDTO.class);
                System.out.println("[ReclamationSync] ✅ Réclamation créée: " + created.getTitre());
                return created;
            } else {
                System.err.println("[ReclamationSync] ❌ CREATE - Status: " + response.statusCode());
            }
        } catch (java.net.ConnectException e) {
            System.err.println("[ReclamationSync] ⚠️ Serveur non accessible — synchronisation différée");
        } catch (Exception e) {
            System.err.println("[ReclamationSync] ❌ Erreur: " + e.getMessage());
        }
        return null;
    }

    public static ReclamationDTO update(int id, ReclamationDTO reclamation) {
        try {
            String json = gson.toJson(reclamation);
            HttpResponse<String> response = SymfonyApiClient.put(
                    ApiConfig.RECLAMATIONS_UPDATE.replace("{id}", String.valueOf(id)), json);
            if (response.statusCode() == 200) {
                System.out.println("[ReclamationSync] ✅ Réclamation #" + id + " mise à jour");
                return gson.fromJson(response.body(), ReclamationDTO.class);
            }
        } catch (java.net.ConnectException e) {
            System.err.println("[ReclamationSync] ⚠️ Serveur non accessible");
        } catch (Exception e) {
            System.err.println("[ReclamationSync] ❌ Erreur: " + e.getMessage());
        }
        return null;
    }

    public static boolean delete(int id) {
        try {
            HttpResponse<String> response = SymfonyApiClient.delete(
                    ApiConfig.RECLAMATIONS_DELETE.replace("{id}", String.valueOf(id)));
            if (response.statusCode() == 200 || response.statusCode() == 204) {
                System.out.println("[ReclamationSync] ✅ Réclamation #" + id + " supprimée");
                return true;
            }
        } catch (java.net.ConnectException e) {
            System.err.println("[ReclamationSync] ⚠️ Serveur non accessible");
        } catch (Exception e) {
            System.err.println("[ReclamationSync] ❌ Erreur: " + e.getMessage());
        }
        return false;
    }

    // ═══════════════════════════════════════════════════════════════
    //  RÉPONSES AUX RÉCLAMATIONS
    // ═══════════════════════════════════════════════════════════════

    public static List<ReponseReclamationDTO> getReponses(int reclamationId) {
        try {
            HttpResponse<String> response = SymfonyApiClient.get(
                    ApiConfig.REPONSES_LIST.replace("{id}", String.valueOf(reclamationId)));
            if (response.statusCode() == 200) {
                Type listType = new TypeToken<List<ReponseReclamationDTO>>() {}.getType();
                return gson.fromJson(response.body(), listType);
            }
        } catch (Exception e) {
            System.err.println("[ReclamationSync] ❌ Erreur GET réponses: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    public static ReponseReclamationDTO createReponse(int reclamationId, ReponseReclamationDTO reponse) {
        try {
            String json = gson.toJson(reponse);
            HttpResponse<String> response = SymfonyApiClient.post(
                    ApiConfig.REPONSES_CREATE.replace("{id}", String.valueOf(reclamationId)), json);
            if (response.statusCode() == 201 || response.statusCode() == 200) {
                System.out.println("[ReclamationSync] ✅ Réponse ajoutée à la réclamation #" + reclamationId);
                return gson.fromJson(response.body(), ReponseReclamationDTO.class);
            }
        } catch (Exception e) {
            System.err.println("[ReclamationSync] ❌ Erreur création réponse: " + e.getMessage());
        }
        return null;
    }
}
