package com.example.mindjavafx.controller;

import com.example.mindjavafx.model.User;
import javafx.fxml.FXML;
import javafx.scene.web.WebView;
import java.io.File;

public class UserDashboardHomeController {

    @FXML private WebView webView;

    @FXML
    public void initialize() {
        File file = new File("index.html");
        if (file.exists()) {
            webView.getEngine().load(file.toURI().toString());
        } else {
            // Tentative de chargement depuis les ressources si non trouvé à la racine
            java.net.URL url = getClass().getResource("/index.html");
            if (url != null) {
                webView.getEngine().load(url.toExternalForm());
            } else {
                webView.getEngine().loadContent("<html><body style='background:#0f172a; color:white; display:flex; justify-content:center; align-items:center; height:100vh; font-family:sans-serif;'><h1>Index.html non trouvé</h1></body></html>");
            }
        }
        
        // Activer la console JS pour le debug
        webView.getEngine().setOnError(event -> System.err.println("WebView Error: " + event.getMessage()));
        
        // Synchroniser le thème initial avec l'application JavaFX
        webView.getEngine().getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                // On récupère l'état du mode sombre de l'application parente si possible
                // Ici on force le thème selon l'état actuel (simulé par une variable statique ou via le controller parent)
                // Pour simplifier, on laisse le JS gérer le localStorage, mais on peut forcer ici :
                // webView.getEngine().executeScript("setTheme('dark');");
            }
        });
    }

    public void setCurrentUser(User user) {
        // On peut passer le nom de l'utilisateur au JS
        if (user != null && webView != null) {
            webView.getEngine().getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                    webView.getEngine().executeScript("if(document.getElementById('welcomeUserLabel')) document.getElementById('welcomeUserLabel').innerText = '" + user.getNom() + "';");
                    webView.getEngine().executeScript("if(document.getElementById('welcomeTitle')) document.getElementById('welcomeTitle').innerText = 'Bienvenue, " + user.getNom() + "';");
                }
            });
        }
    }

    public void toggleTheme() {
        if (webView != null) {
            webView.getEngine().executeScript("if(window.toggleTheme) window.toggleTheme();");
        }
    }

    public void setDashboardController(Object dashboardController) {
    }
    
    // Méthodes dummy pour éviter les erreurs de compilation si appelées ailleurs
    @FXML private void handleAISearch() {}
    @FXML private void handleMicSearch() {}
    @FXML private void handleGenerateReport() {}
    @FXML private void handleScanDocument() {}
    @FXML private void showRapportList() {}
    @FXML private void showEntrepriseManagement() {}
}
