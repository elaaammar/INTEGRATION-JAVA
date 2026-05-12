package com.gestion_audit.util;

import java.sql.*;

public class DBConnection {
    private String url = "jdbc:mysql://127.0.0.1:3306/mindaudit_java?useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=UTF-8";
    private String user = "root";
    private String password = "";
    private Connection conn;
    private static DBConnection instance;

    public static DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    public Connection getConn() {
        try {
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection(url, user, password);
                System.out.println("Connection re-established");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    private DBConnection() {
        try {
            this.conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connection established to unified database: mindaudit_java");
            initializeTables();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void initializeTables() {
        try (Statement stmt = conn.createStatement()) {
            // Table rapport
            stmt.execute("CREATE TABLE IF NOT EXISTS rapport (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "title VARCHAR(255), " +
                    "description TEXT, " +
                    "date VARCHAR(50), " +
                    "type VARCHAR(100), " +
                    "duration INT DEFAULT 0)");

            // Table question_audit
            stmt.execute("CREATE TABLE IF NOT EXISTS question_audit (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "content TEXT, " +
                    "type VARCHAR(100), " +
                    "bonne_reponse TEXT, " +
                    "time_limit INT DEFAULT 60)");

            // Table reponse_question - columns must match service queries
            stmt.execute("CREATE TABLE IF NOT EXISTS reponse_question (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "question_id INT, " +
                    "rapport_id INT, " +
                    "reponse TEXT, " +
                    "date_reponse TIMESTAMP DEFAULT CURRENT_TIMESTAMP)" );

            // --- RECLAMATIONS TABLES (Unified) ---
            stmt.execute("CREATE TABLE IF NOT EXISTS reclamation (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "titre VARCHAR(255), " +
                    "description TEXT, " +
                    "date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "statut VARCHAR(50), " +
                    "priorite VARCHAR(50), " +
                    "categorie VARCHAR(50), " +
                    "nom VARCHAR(100), " +
                    "email VARCHAR(100), " +
                    "telephone VARCHAR(20))");

            stmt.execute("CREATE TABLE IF NOT EXISTS reponse_reclamation (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "reclamation_id INT, " +
                    "contenu TEXT, " +
                    "auteur_type VARCHAR(50), " +
                    "avis_utilisateur VARCHAR(50), " +
                    "nom VARCHAR(100), " +
                    "date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
            
            System.out.println("All Audit & Reclamation tables initialized successfully.");
        } catch (SQLException e) {
            System.err.println("Database table initialization failed: " + e.getMessage());
        }
    }

}
