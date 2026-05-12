package com.gestion_audit.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {

    @FXML
    private StackPane contentArea;

    @FXML
    private Label lblViewTitle;

    @FXML
    private Label lblUserRole;

    @FXML
    private Button btnAddRapport;

    @FXML
    private Button btnAddQuestion;

    private String currentRole = "Administrator";

    public void setUserRole(String role) {
        this.currentRole = role.equals("ADMIN") ? "Administrator" : role;
        lblUserRole.setText(this.currentRole);
        
        // Role-based access control for sidebar
        if ("Consultant".equals(this.currentRole) || "USER".equals(this.currentRole)) {
            btnAddRapport.setVisible(false);
            btnAddRapport.setManaged(false);
            btnAddQuestion.setVisible(false);
            btnAddQuestion.setManaged(false);
        }
        
        // Reload reports so role takes effect
        showReports();
    }

    @FXML
    public void initialize() {
        // Use Platform.runLater to ensure the view loads after the container is ready
        javafx.application.Platform.runLater(() -> {
            boolean isAdmin = com.gestion.util.SessionManager.getInstance().isAdmin();
            setUserRole(isAdmin ? "ADMIN" : "USER");
        });
    }

    @FXML
    void showReports() {
        loadView("/fxml/gestion_audit/ListRapports.fxml", "Dashboard Overview");
    }

    @FXML
    void showAddRapport() {
        loadView("/fxml/gestion_audit/AddRapport.fxml", "Create New Audit Report");
    }

    @FXML
    void showAddQuestion() {
        loadView("/fxml/gestion_audit/AddQuestion.fxml", "Add Audit Question");
    }

    @FXML
    void showStatistics() {
        loadView("/fxml/gestion_audit/Statistics.fxml", "Statistics & Analytics");
    }

    private void loadView(String fxmlPath, String title) {
        try {
            System.out.println("DEBUG: Loading view " + fxmlPath);
            java.net.URL res = getClass().getResource(fxmlPath);
            if (res == null) {
                throw new IOException("FXML file not found: " + fxmlPath);
            }
            
            FXMLLoader loader = new FXMLLoader(res);
            Parent view = loader.load();
            
            // Pass the role to child controllers if they need it
            if (fxmlPath.contains("ListRapports.fxml")) {
                Object controller = loader.getController();
                if (controller instanceof ListRapportsController) {
                    ((ListRapportsController) controller).setRole(this.currentRole.equals("Administrator") ? "ADMIN" : "USER");
                }
            }
            
            // Clear current content and add new view
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
            
            // Update title
            lblViewTitle.setText(title);
            
        } catch (Exception e) {
            System.err.println("CRITICAL ERROR loading view: " + fxmlPath);
            e.printStackTrace();
            
            // Show error in the UI so the user knows what's wrong
            Label errorLabel = new Label("Error loading " + fxmlPath + "\nCause: " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14; -fx-padding: 20;");
            contentArea.getChildren().clear();
            contentArea.getChildren().add(errorLabel);
        }
    }

}

