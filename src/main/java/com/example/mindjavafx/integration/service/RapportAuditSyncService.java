package com.example.mindjavafx.integration.service;

import com.example.mindjavafx.integration.client.ApiConfig;
import com.example.mindjavafx.integration.client.SymfonyApiClient;
import com.example.mindjavafx.integration.dto.RapportAuditDTO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Service de synchronisation CRUD pour le module Rapports d'Audit IA.
 * (module auditaifx — RapportAudit, Risques, Recommandations)
 */
public class RapportAuditSyncService {

    private static final Gson gson = new Gson();

    public static List<RapportAuditDTO> getAll() {
        try {
            HttpResponse<String> response = SymfonyApiClient.get(ApiConfig.RAPPORT_AUDIT_LIST);
            if (response.statusCode() == 200) {
                Type listType = new TypeToken<List<RapportAuditDTO>>() {}.getType();
                List<RapportAuditDTO> list = gson.fromJson(response.body(), listType);
                System.out.println("[RapportAuditSync] ✅ " + list.size() + " rapports IA récupérés");
                return list;
            }
        } catch (java.net.ConnectException e) {
            System.err.println("[RapportAuditSync] ⚠️ Serveur Symfony non accessible");
        } catch (Exception e) {
            System.err.println("[RapportAuditSync] ❌ Erreur: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    public static RapportAuditDTO getById(String id) {
        try {
            String endpoint = ApiConfig.RAPPORT_AUDIT_GET.replace("{id}", id);
            HttpResponse<String> response = SymfonyApiClient.get(endpoint);
            if (response.statusCode() == 200) {
                return gson.fromJson(response.body(), RapportAuditDTO.class);
            }
        } catch (Exception e) {
            System.err.println("[RapportAuditSync] ❌ GET #" + id + ": " + e.getMessage());
        }
        return null;
    }

    public static RapportAuditDTO create(RapportAuditDTO rapportAudit) {
        try {
            HttpResponse<String> response = SymfonyApiClient.post(
                    ApiConfig.RAPPORT_AUDIT_CREATE, gson.toJson(rapportAudit));
            if (response.statusCode() == 201 || response.statusCode() == 200) {
                System.out.println("[RapportAuditSync] ✅ Rapport IA créé: " + rapportAudit.getTitre());
                return gson.fromJson(response.body(), RapportAuditDTO.class);
            }
        } catch (java.net.ConnectException e) {
            System.err.println("[RapportAuditSync] ⚠️ Serveur non accessible");
        } catch (Exception e) {
            System.err.println("[RapportAuditSync] ❌ Erreur: " + e.getMessage());
        }
        return null;
    }

    public static RapportAuditDTO update(String id, RapportAuditDTO rapportAudit) {
        try {
            String endpoint = ApiConfig.RAPPORT_AUDIT_UPDATE.replace("{id}", id);
            HttpResponse<String> response = SymfonyApiClient.put(endpoint, gson.toJson(rapportAudit));
            if (response.statusCode() == 200) {
                System.out.println("[RapportAuditSync] ✅ Rapport IA #" + id + " mis à jour");
                return gson.fromJson(response.body(), RapportAuditDTO.class);
            }
        } catch (Exception e) {
            System.err.println("[RapportAuditSync] ❌ Erreur: " + e.getMessage());
        }
        return null;
    }

    public static boolean delete(String id) {
        try {
            String endpoint = ApiConfig.RAPPORT_AUDIT_DELETE.replace("{id}", id);
            HttpResponse<String> response = SymfonyApiClient.delete(endpoint);
            if (response.statusCode() == 200 || response.statusCode() == 204) {
                System.out.println("[RapportAuditSync] ✅ Rapport IA #" + id + " supprimé");
                return true;
            }
        } catch (Exception e) {
            System.err.println("[RapportAuditSync] ❌ Erreur: " + e.getMessage());
        }
        return false;
    }
}
