package com.example.mindjavafx.integration.service;

import com.example.mindjavafx.integration.client.ApiConfig;
import com.example.mindjavafx.integration.client.SymfonyApiClient;
import com.example.mindjavafx.integration.dto.AuditDTO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Service de synchronisation CRUD pour le module Audits.
 * Communication JavaFX ↔ Symfony via API REST.
 */
public class AuditSyncService {

    private static final Gson gson = new Gson();

    // ═══════════════════════════════════════════════════════════════
    //  READ — Récupérer tous les audits depuis Symfony
    // ═══════════════════════════════════════════════════════════════
    public static List<AuditDTO> getAll() {
        try {
            HttpResponse<String> response = SymfonyApiClient.get(ApiConfig.AUDITS_LIST);

            if (response.statusCode() == 200) {
                Type listType = new TypeToken<List<AuditDTO>>() {}.getType();
                List<AuditDTO> audits = gson.fromJson(response.body(), listType);
                System.out.println("[AuditSync] ✅ " + audits.size() + " audits récupérés depuis Symfony");
                return audits;
            } else {
                System.err.println("[AuditSync] ❌ Erreur GET audits - Status: " + response.statusCode());
            }
        } catch (java.net.ConnectException e) {
            System.err.println("[AuditSync] ⚠️ Serveur Symfony non accessible");
        } catch (Exception e) {
            System.err.println("[AuditSync] ❌ Erreur: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    // ═══════════════════════════════════════════════════════════════
    //  READ — Récupérer un audit par ID depuis Symfony
    // ═══════════════════════════════════════════════════════════════
    public static AuditDTO getById(int id) {
        try {
            HttpResponse<String> response = SymfonyApiClient.get(
                    ApiConfig.AUDITS_GET.replace("{id}", String.valueOf(id)));

            if (response.statusCode() == 200) {
                return gson.fromJson(response.body(), AuditDTO.class);
            }
        } catch (Exception e) {
            System.err.println("[AuditSync] ❌ Erreur GET audit #" + id + ": " + e.getMessage());
        }
        return null;
    }

    // ═══════════════════════════════════════════════════════════════
    //  CREATE — Créer un audit dans Symfony
    // ═══════════════════════════════════════════════════════════════
    public static AuditDTO create(AuditDTO audit) {
        try {
            String json = gson.toJson(audit);
            HttpResponse<String> response = SymfonyApiClient.post(ApiConfig.AUDITS_CREATE, json);

            if (response.statusCode() == 201 || response.statusCode() == 200) {
                AuditDTO created = gson.fromJson(response.body(), AuditDTO.class);
                System.out.println("[AuditSync] ✅ Audit créé dans Symfony: " + created.getName());
                return created;
            } else {
                System.err.println("[AuditSync] ❌ Erreur CREATE audit - Status: " + response.statusCode());
                System.err.println("[AuditSync] Réponse: " + response.body());
            }
        } catch (java.net.ConnectException e) {
            System.err.println("[AuditSync] ⚠️ Serveur Symfony non accessible — l'audit sera synchronisé plus tard");
        } catch (Exception e) {
            System.err.println("[AuditSync] ❌ Erreur: " + e.getMessage());
        }
        return null;
    }

    // ═══════════════════════════════════════════════════════════════
    //  UPDATE — Mettre à jour un audit dans Symfony
    // ═══════════════════════════════════════════════════════════════
    public static AuditDTO update(int id, AuditDTO audit) {
        try {
            String json = gson.toJson(audit);
            HttpResponse<String> response = SymfonyApiClient.put(
                    ApiConfig.AUDITS_UPDATE.replace("{id}", String.valueOf(id)), json);

            if (response.statusCode() == 200) {
                AuditDTO updated = gson.fromJson(response.body(), AuditDTO.class);
                System.out.println("[AuditSync] ✅ Audit #" + id + " mis à jour dans Symfony");
                return updated;
            } else {
                System.err.println("[AuditSync] ❌ Erreur UPDATE audit #" + id + " - Status: " + response.statusCode());
            }
        } catch (java.net.ConnectException e) {
            System.err.println("[AuditSync] ⚠️ Serveur Symfony non accessible");
        } catch (Exception e) {
            System.err.println("[AuditSync] ❌ Erreur: " + e.getMessage());
        }
        return null;
    }

    // ═══════════════════════════════════════════════════════════════
    //  DELETE — Supprimer un audit dans Symfony
    // ═══════════════════════════════════════════════════════════════
    public static boolean delete(int id) {
        try {
            HttpResponse<String> response = SymfonyApiClient.delete(
                    ApiConfig.AUDITS_DELETE.replace("{id}", String.valueOf(id)));

            if (response.statusCode() == 200 || response.statusCode() == 204) {
                System.out.println("[AuditSync] ✅ Audit #" + id + " supprimé dans Symfony");
                return true;
            } else {
                System.err.println("[AuditSync] ❌ Erreur DELETE audit #" + id + " - Status: " + response.statusCode());
            }
        } catch (java.net.ConnectException e) {
            System.err.println("[AuditSync] ⚠️ Serveur Symfony non accessible");
        } catch (Exception e) {
            System.err.println("[AuditSync] ❌ Erreur: " + e.getMessage());
        }
        return false;
    }
}
