package com.example.mindjavafx.integration.service;

import com.example.mindjavafx.integration.client.ApiConfig;
import com.example.mindjavafx.integration.client.SymfonyApiClient;
import com.example.mindjavafx.integration.dto.EntrepriseDTO;
import com.example.mindjavafx.integration.dto.DocumentDTO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Service de synchronisation CRUD pour le module Entreprises et Documents.
 */
public class EntrepriseSyncService {

    private static final Gson gson = new Gson();

    public static List<EntrepriseDTO> getAll() {
        try {
            HttpResponse<String> response = SymfonyApiClient.get(ApiConfig.ENTREPRISES_LIST);
            if (response.statusCode() == 200) {
                Type listType = new TypeToken<List<EntrepriseDTO>>() {}.getType();
                List<EntrepriseDTO> list = gson.fromJson(response.body(), listType);
                System.out.println("[EntrepriseSync] ✅ " + list.size() + " entreprises récupérées");
                return list;
            }
        } catch (java.net.ConnectException e) {
            System.err.println("[EntrepriseSync] ⚠️ Serveur Symfony non accessible");
        } catch (Exception e) {
            System.err.println("[EntrepriseSync] ❌ Erreur: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    public static EntrepriseDTO getById(int id) {
        try {
            String endpoint = ApiConfig.ENTREPRISES_GET.replace("{id}", String.valueOf(id));
            HttpResponse<String> response = SymfonyApiClient.get(endpoint);
            if (response.statusCode() == 200) {
                return gson.fromJson(response.body(), EntrepriseDTO.class);
            }
        } catch (Exception e) {
            System.err.println("[EntrepriseSync] ❌ GET #" + id + ": " + e.getMessage());
        }
        return null;
    }

    public static EntrepriseDTO create(EntrepriseDTO entreprise) {
        try {
            HttpResponse<String> response = SymfonyApiClient.post(
                    ApiConfig.ENTREPRISES_CREATE, gson.toJson(entreprise));
            if (response.statusCode() == 201 || response.statusCode() == 200) {
                System.out.println("[EntrepriseSync] ✅ Entreprise créée: " + entreprise.getNom());
                return gson.fromJson(response.body(), EntrepriseDTO.class);
            }
        } catch (java.net.ConnectException e) {
            System.err.println("[EntrepriseSync] ⚠️ Serveur non accessible");
        } catch (Exception e) {
            System.err.println("[EntrepriseSync] ❌ Erreur: " + e.getMessage());
        }
        return null;
    }

    public static EntrepriseDTO update(int id, EntrepriseDTO entreprise) {
        try {
            String endpoint = ApiConfig.ENTREPRISES_UPDATE.replace("{id}", String.valueOf(id));
            HttpResponse<String> response = SymfonyApiClient.put(endpoint, gson.toJson(entreprise));
            if (response.statusCode() == 200) {
                System.out.println("[EntrepriseSync] ✅ Entreprise #" + id + " mise à jour");
                return gson.fromJson(response.body(), EntrepriseDTO.class);
            }
        } catch (Exception e) {
            System.err.println("[EntrepriseSync] ❌ Erreur: " + e.getMessage());
        }
        return null;
    }

    public static boolean delete(int id) {
        try {
            String endpoint = ApiConfig.ENTREPRISES_DELETE.replace("{id}", String.valueOf(id));
            HttpResponse<String> response = SymfonyApiClient.delete(endpoint);
            if (response.statusCode() == 200 || response.statusCode() == 204) {
                System.out.println("[EntrepriseSync] ✅ Entreprise #" + id + " supprimée");
                return true;
            }
        } catch (Exception e) {
            System.err.println("[EntrepriseSync] ❌ Erreur: " + e.getMessage());
        }
        return false;
    }

    // ── Documents ──────────────────────────────────────────────────

    public static List<DocumentDTO> getDocuments(int entrepriseId) {
        try {
            String endpoint = ApiConfig.DOCUMENTS_LIST.replace("{id}", String.valueOf(entrepriseId));
            HttpResponse<String> response = SymfonyApiClient.get(endpoint);
            if (response.statusCode() == 200) {
                Type listType = new TypeToken<List<DocumentDTO>>() {}.getType();
                return gson.fromJson(response.body(), listType);
            }
        } catch (Exception e) {
            System.err.println("[EntrepriseSync] ❌ GET documents: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    public static DocumentDTO createDocument(int entrepriseId, DocumentDTO document) {
        try {
            String endpoint = ApiConfig.DOCUMENTS_CREATE.replace("{id}", String.valueOf(entrepriseId));
            HttpResponse<String> response = SymfonyApiClient.post(endpoint, gson.toJson(document));
            if (response.statusCode() == 201 || response.statusCode() == 200) {
                System.out.println("[EntrepriseSync] ✅ Document ajouté à l'entreprise #" + entrepriseId);
                return gson.fromJson(response.body(), DocumentDTO.class);
            }
        } catch (Exception e) {
            System.err.println("[EntrepriseSync] ❌ Erreur: " + e.getMessage());
        }
        return null;
    }
}
