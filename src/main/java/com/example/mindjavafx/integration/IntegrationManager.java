package com.example.mindjavafx.integration;

import com.example.mindjavafx.integration.client.ApiConfig;
import com.example.mindjavafx.integration.service.*;

/**
 * ══════════════════════════════════════════════════════════════════
 *  IntegrationManager — Point d'entrée central pour l'intégration
 *  JavaFX ↔ Symfony (PIDEV 3A — MindAudit)
 * ══════════════════════════════════════════════════════════════════
 *
 * Ce gestionnaire centralise :
 * - L'initialisation de la connexion avec Symfony
 * - L'accès à tous les services de synchronisation
 * - Le diagnostic de connexion
 *
 * Utilisation dans un contrôleur JavaFX :
 *
 *   // Au démarrage de l'application
 *   IntegrationManager.initialize();
 *
 *   // Login synchronisé
 *   IntegrationManager.login("admin@mindaudit.com", "password");
 *
 *   // CRUD synchronisé (exemple: audits)
 *   List<AuditDTO> audits = AuditSyncService.getAll();
 *   AuditSyncService.create(newAudit);
 */
public class IntegrationManager {

    private static boolean initialized = false;
    private static boolean symfonyAvailable = false;

    /**
     * Initialise l'intégration avec le serveur Symfony.
     * À appeler au démarrage de l'application JavaFX (dans Main ou SplashController).
     */
    public static void initialize() {
        if (initialized) return;

        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║  MindAudit — Intégration JavaFX ↔ Symfony       ║");
        System.out.println("║  PIDEV 3A — 2025/2026                           ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("[Integration] URL Symfony: " + ApiConfig.getBaseUrl());
        System.out.println("[Integration] Test de connexion...");

        symfonyAvailable = AuthSyncService.testConnection();

        if (symfonyAvailable) {
            System.out.println("[Integration] ✅ Serveur Symfony accessible — synchronisation activée");
        } else {
            System.out.println("[Integration] ⚠️ Serveur Symfony non accessible");
            System.out.println("[Integration] L'application JavaFX fonctionnera en mode local (base de données directe)");
            System.out.println("[Integration] Pour activer la synchronisation, démarrez Symfony :");
            System.out.println("[Integration]   cd <votre-projet-symfony>");
            System.out.println("[Integration]   symfony server:start");
        }

        initialized = true;
        System.out.println();
    }

    /**
     * Authentifie l'utilisateur auprès de Symfony.
     * Peut être appelé depuis le LoginController après un login local réussi.
     *
     * @return true si l'authentification Symfony a réussi
     */
    public static boolean login(String email, String password) {
        if (!symfonyAvailable) {
            System.out.println("[Integration] Mode local — pas de sync auth Symfony");
            return false;
        }
        return AuthSyncService.login(email, password);
    }

    /**
     * Déconnecte l'utilisateur côté Symfony.
     */
    public static void logout() {
        AuthSyncService.logout();
    }

    /**
     * Vérifie si le serveur Symfony est accessible.
     */
    public static boolean isSymfonyAvailable() {
        return symfonyAvailable;
    }

    /**
     * Force un re-test de la connexion Symfony.
     */
    public static void refreshConnection() {
        symfonyAvailable = AuthSyncService.testConnection();
    }

    /**
     * Affiche un diagnostic complet de l'intégration.
     */
    public static void printDiagnostic() {
        System.out.println("┌─────────────────────────────────────────────┐");
        System.out.println("│  DIAGNOSTIC INTÉGRATION MINDAUDIT           │");
        System.out.println("├─────────────────────────────────────────────┤");
        System.out.println("│  URL Symfony : " + padRight(ApiConfig.getBaseUrl(), 29) + " │");
        System.out.println("│  Connexion   : " + padRight(symfonyAvailable ? "✅ OK" : "❌ NON", 29) + "│");
        System.out.println("│  Auth        : " + padRight(AuthSyncService.isAuthenticated() ? "✅ Connecté" : "❌ Non connecté", 29) + "│");
        System.out.println("│  Token       : " + padRight(AuthSyncService.getToken() != null ? "Présent" : "Absent", 29) + "│");
        System.out.println("├─────────────────────────────────────────────┤");
        System.out.println("│  MODULES SYNCHRONISÉS :                     │");
        System.out.println("│  • Utilisateurs (User)                      │");
        System.out.println("│  • Audits                                   │");
        System.out.println("│  • Réclamations & Réponses                  │");
        System.out.println("│  • Rapports & Questions                     │");
        System.out.println("│  • Entreprises & Documents                  │");
        System.out.println("│  • Rapports d'Audit IA (Risques, Reco.)     │");
        System.out.println("└─────────────────────────────────────────────┘");
    }

    private static String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }
}
