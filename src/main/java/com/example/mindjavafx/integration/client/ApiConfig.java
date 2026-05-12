package com.example.mindjavafx.integration.client;

import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration centrale pour l'intégration JavaFX ↔ Symfony.
 * Les URLs et paramètres sont lus depuis config.properties ou définis ici.
 */
public class ApiConfig {

    // ═══════════════════════════════════════════════════════════════
    //  URL de base du serveur Symfony (modifiable dans config.properties)
    // ═══════════════════════════════════════════════════════════════
    private static String SYMFONY_BASE_URL = "http://127.0.0.1:8000";

    // ═══════════════════════════════════════════════════════════════
    //  Endpoints de l'API REST Symfony — Authentification
    // ═══════════════════════════════════════════════════════════════
    public static final String AUTH_LOGIN       = "/api/login";
    public static final String AUTH_REGISTER    = "/api/register";
    public static final String AUTH_PROFILE     = "/api/profile";

    // ═══════════════════════════════════════════════════════════════
    //  Module 1 — Gestion Utilisateurs
    // ═══════════════════════════════════════════════════════════════
    public static final String USERS_LIST       = "/api/users";
    public static final String USERS_GET        = "/api/users/{id}";
    public static final String USERS_CREATE     = "/api/users";
    public static final String USERS_UPDATE     = "/api/users/{id}";
    public static final String USERS_DELETE     = "/api/users/{id}";

    // ═══════════════════════════════════════════════════════════════
    //  Module 2 — Gestion Audits
    // ═══════════════════════════════════════════════════════════════
    public static final String AUDITS_LIST      = "/api/audits";
    public static final String AUDITS_GET       = "/api/audits/{id}";
    public static final String AUDITS_CREATE    = "/api/audits";
    public static final String AUDITS_UPDATE    = "/api/audits/{id}";
    public static final String AUDITS_DELETE    = "/api/audits/{id}";

    // ═══════════════════════════════════════════════════════════════
    //  Module 3 — Gestion Réclamations
    // ═══════════════════════════════════════════════════════════════
    public static final String RECLAMATIONS_LIST    = "/api/reclamations";
    public static final String RECLAMATIONS_GET     = "/api/reclamations/{id}";
    public static final String RECLAMATIONS_CREATE  = "/api/reclamations";
    public static final String RECLAMATIONS_UPDATE  = "/api/reclamations/{id}";
    public static final String RECLAMATIONS_DELETE  = "/api/reclamations/{id}";
    public static final String REPONSES_LIST        = "/api/reclamations/{id}/reponses";
    public static final String REPONSES_CREATE      = "/api/reclamations/{id}/reponses";

    // ═══════════════════════════════════════════════════════════════
    //  Module 4 — Rapports & Questions d'Audit
    // ═══════════════════════════════════════════════════════════════
    public static final String RAPPORTS_LIST    = "/api/rapports";
    public static final String RAPPORTS_GET     = "/api/rapports/{id}";
    public static final String RAPPORTS_CREATE  = "/api/rapports";
    public static final String RAPPORTS_UPDATE  = "/api/rapports/{id}";
    public static final String RAPPORTS_DELETE  = "/api/rapports/{id}";
    public static final String QUESTIONS_LIST   = "/api/rapports/{id}/questions";
    public static final String QUESTIONS_CREATE = "/api/rapports/{id}/questions";

    // ═══════════════════════════════════════════════════════════════
    //  Module 5 — Gestion Entreprises & Documents
    // ═══════════════════════════════════════════════════════════════
    public static final String ENTREPRISES_LIST     = "/api/entreprises";
    public static final String ENTREPRISES_GET      = "/api/entreprises/{id}";
    public static final String ENTREPRISES_CREATE   = "/api/entreprises";
    public static final String ENTREPRISES_UPDATE   = "/api/entreprises/{id}";
    public static final String ENTREPRISES_DELETE   = "/api/entreprises/{id}";
    public static final String DOCUMENTS_LIST       = "/api/entreprises/{id}/documents";
    public static final String DOCUMENTS_CREATE     = "/api/entreprises/{id}/documents";

    // ═══════════════════════════════════════════════════════════════
    //  Module 6 — Rapports d'Audit IA (RapportAudit, Risques, Recommandations)
    // ═══════════════════════════════════════════════════════════════
    public static final String RAPPORT_AUDIT_LIST   = "/api/rapport-audits";
    public static final String RAPPORT_AUDIT_GET    = "/api/rapport-audits/{id}";
    public static final String RAPPORT_AUDIT_CREATE = "/api/rapport-audits";
    public static final String RAPPORT_AUDIT_UPDATE = "/api/rapport-audits/{id}";
    public static final String RAPPORT_AUDIT_DELETE = "/api/rapport-audits/{id}";

    // ═══════════════════════════════════════════════════════════════
    //  Statistiques & Dashboard
    // ═══════════════════════════════════════════════════════════════
    public static final String STATS_DASHBOARD  = "/api/stats/dashboard";

    // ═══════════════════════════════════════════════════════════════
    //  Méthodes utilitaires
    // ═══════════════════════════════════════════════════════════════

    static {
        // Tenter de charger l'URL depuis config.properties
        try {
            Properties props = new Properties();
            InputStream is = ApiConfig.class.getClassLoader().getResourceAsStream("config.properties");
            if (is == null) {
                is = ApiConfig.class.getResourceAsStream("/config.properties");
            }
            if (is != null) {
                props.load(is);
                String url = props.getProperty("symfony.base.url");
                if (url != null && !url.isBlank()) {
                    SYMFONY_BASE_URL = url.trim();
                }
                is.close();
            }
        } catch (Exception e) {
            System.err.println("[ApiConfig] Impossible de lire config.properties, utilisation de l'URL par défaut: " + SYMFONY_BASE_URL);
        }
    }

    /**
     * Retourne l'URL complète pour un endpoint donné.
     * Exemple: getUrl(AUDITS_LIST) → "http://127.0.0.1:8000/api/audits"
     */
    public static String getUrl(String endpoint) {
        return SYMFONY_BASE_URL + endpoint;
    }

    /**
     * Retourne l'URL complète avec un paramètre {id} remplacé.
     * Exemple: getUrl(AUDITS_GET, 5) → "http://127.0.0.1:8000/api/audits/5"
     */
    public static String getUrl(String endpoint, int id) {
        return SYMFONY_BASE_URL + endpoint.replace("{id}", String.valueOf(id));
    }

    /**
     * Retourne l'URL complète avec un paramètre {id} remplacé (String).
     */
    public static String getUrl(String endpoint, String id) {
        return SYMFONY_BASE_URL + endpoint.replace("{id}", id);
    }

    public static String getBaseUrl() {
        return SYMFONY_BASE_URL;
    }

    public static void setBaseUrl(String url) {
        SYMFONY_BASE_URL = url;
    }
}
