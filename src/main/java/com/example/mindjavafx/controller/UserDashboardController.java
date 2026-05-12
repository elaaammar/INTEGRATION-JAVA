package com.example.mindjavafx.controller;

import com.example.mindjavafx.model.User;
import com.example.mindjavafx.service.AuthenticationService;
import com.example.mindjavafx.service.NotificationService;
import com.example.mindjavafx.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Labeled;
import javafx.event.ActionEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.Node;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

import com.audit.auditaifx.model.RapportAudit;
import com.audit.auditaifx.model.StatutRapport;
import com.audit.auditaifx.service.RapportService;
import com.audit.auditaifx.controller.MainController;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class UserDashboardController {

    @FXML private Button notificationButton;
    @FXML private Label notificationBadge;
    @FXML private Circle profileImage;
    @FXML private Label userNameLabel;
    @FXML private Button logoutButton;
    private boolean isDarkMode = false;
    
    private Object currentSectionController;

    @FXML private Button entrepriseButtonTop;
    @FXML private Button rapportManagementButtonTop;
    @FXML private Button auditManagementButtonTop;
    @FXML private Button reclamationButtonTop;
    @FXML private Button fullChatbotButton;

    @FXML private BorderPane mainBorderPane;
    @FXML private StackPane contentArea;
    @FXML private Pane moonlightOverlay;
    @FXML private Circle moonlightCircle;

    private AuthenticationService authService;
    private NotificationService notificationService;
    private UserService userService;
    private User currentUser;
    private com.audit.auditaifx.service.AIService aiService = new com.audit.auditaifx.service.AIService();

    @FXML
    public void initialize() {
        // Set the global SessionManager to user role so admin buttons are hidden
        com.gestion.util.SessionManager.getInstance().setAdmin(false);
        
        // ... rest of the code is unchanged (we can just add it at the top of initialize)
        notificationService = new NotificationService();
        userService = new UserService();
        setupTranslationContextMenu();
    }

    public void setAuthService(AuthenticationService authService) {
        this.authService = authService;
        this.currentUser = authService.getCurrentUser();
        if (currentUser != null) {
            userNameLabel.setText(currentUser.getNom());
            updateNotificationBadge();
            showEntrepriseManagement();  // Default landing: Entreprise
        }
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            userNameLabel.setText(user.getNom());
            updateNotificationBadge();
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    @FXML
    public void showDashboardHome() {
        // Dashboard removed for user - redirect to Entreprise
        showEntrepriseManagement();
    }

    @FXML
    public void showEntrepriseManagement() {
        highlightButton(entrepriseButtonTop);
        loadSection("entreprise-view.fxml");
    }

    @FXML
    public void showRapportManagement() {
        highlightButton(rapportManagementButtonTop);
        loadSection("rapports_reco/main-view.fxml", controller -> {
            if (controller instanceof com.audit.auditaifx.controller.MainController) {
                com.audit.auditaifx.controller.MainController mainCtrl = (com.audit.auditaifx.controller.MainController) controller;
                mainCtrl.setCurrentUser(currentUser);
                mainCtrl.setRole("USER"); // This will show the report list and details correctly
            }
        });
    }

    @FXML
    public void showAuditManagement() {
        highlightButton(auditManagementButtonTop);
        loadSection("gestion_audit/DashboardTemplate.fxml");
    }

    public void showAddRapport() {
        showRapportManagement();
    }

    @FXML
    public void showReclamations() {
        highlightButton(reclamationButtonTop);
        loadSection("/views/client_main.fxml");
    }

    @FXML
    public void showReclamationChatbot() {
        showReclamationChatbot(null);
    }

    public void showReclamationChatbot(String prompt) {
        highlightButton(fullChatbotButton);
        loadSection("/views/chatbot.fxml", controller -> {
            if (controller instanceof com.gestionaudit.controllers.ChatbotController) {
                com.gestionaudit.controllers.ChatbotController chatCtrl = (com.gestionaudit.controllers.ChatbotController) controller;
                chatCtrl.setDashboardController(this);
                if (prompt != null) {
                    chatCtrl.setInitialPrompt(prompt);
                }
            }
        });
    }

    public void openChatbotWithReport(RapportAudit report) {
        if (report == null) return;
        String prompt = "Bonjour, j'aimerais avoir des recommandations intelligentes pour mon rapport intitulé : " + report.getTitre() + 
                       ".\n\nVoici la description du rapport :\n" + report.getDescription() + 
                       "\n\nPeux-tu analyser les risques et me suggérer des actions concrètes ?";
        showReclamationChatbot(prompt);
    }

    public void showReclamationChatbotWithVoice() {
        highlightButton(fullChatbotButton);
        loadSection("/views/chatbot.fxml", controller -> {
            if (controller instanceof com.gestionaudit.controllers.ChatbotController) {
                com.gestionaudit.controllers.ChatbotController chatCtrl = (com.gestionaudit.controllers.ChatbotController) controller;
                chatCtrl.setDashboardController(this);
                // Trigger recording after a short delay to ensure UI is ready
                PauseTransition pause = new PauseTransition(Duration.millis(500));
                pause.setOnFinished(e -> chatCtrl.toggleRecording());
                pause.play();
            }
        });
    }

    @FXML
    public void handleScanDocument() {
        highlightButton(null);
        loadSection("rapports_reco/client-view.fxml", controller -> {
            if (controller instanceof com.audit.auditaifx.controller.ClientController) {
                com.audit.auditaifx.controller.ClientController clientCtrl = (com.audit.auditaifx.controller.ClientController) controller;
                clientCtrl.setCurrentUser(currentUser);
                clientCtrl.showScanView();
            }
        });
    }

    @FXML
    private void showSettings() {
        loadSection("profile.fxml");
    }

    private void loadSection(String fxmlFile) {
        loadSection(fxmlFile, null);
    }

    private void loadSection(String fxmlFile, java.util.function.Consumer<Object> controllerConsumer) {
        try {
            String path = fxmlFile.startsWith("/") ? fxmlFile : "/fxml/" + fxmlFile;
            java.net.URL resource = getClass().getResource(path);
            if (resource == null) {
                resource = getClass().getResource(fxmlFile);
            }
            
            if (resource == null) {
                throw new IOException("FXML resource not found: " + fxmlFile);
            }
            
            FXMLLoader loader = new FXMLLoader(resource);
            Node node = loader.load();
            Object controller = loader.getController();
            this.currentSectionController = controller;
            
            if (controllerConsumer != null) {
                controllerConsumer.accept(controller);
            }
            
            contentArea.getChildren().setAll(node);
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to load section [" + fxmlFile + "]: " + e.getMessage());
            e.printStackTrace();
            showErrorDialog("Erreur", "Impossible de charger la section: " + fxmlFile + "\nCause: " + e.getMessage());
        }
    }

    private void highlightButton(Button button) {
        String inactiveStyle = "-fx-background-color: transparent; -fx-text-fill: #475569; -fx-padding: 10px 18px; -fx-font-size: 14px; -fx-cursor: hand; -fx-font-weight: 600;";
        String activeStyle = "-fx-background-color: #0ea5e9; -fx-text-fill: white; -fx-padding: 10px 18px; -fx-font-size: 14px; -fx-cursor: hand; -fx-font-weight: bold; -fx-background-radius: 999px;";

        Button[] navButtons = {entrepriseButtonTop, rapportManagementButtonTop, auditManagementButtonTop, reclamationButtonTop, fullChatbotButton};
        for (Button b : navButtons) {
            if (b != null) b.setStyle(b == button ? activeStyle : inactiveStyle);
        }
    }

    public void updateNotificationBadge() {
        if (currentUser != null && notificationService != null) {
            int unreadCount = notificationService.getUnreadCount(currentUser.getId());
            if (unreadCount > 0) {
                notificationBadge.setText("+" + unreadCount);
                notificationBadge.setVisible(true);
            } else {
                notificationBadge.setVisible(false);
            }
        }
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void showNotificationDropdown() {
        if (currentUser == null || notificationService == null) return;
        java.util.List<com.example.mindjavafx.model.Notification> notifs = notificationService.getNotificationsByUserId(currentUser.getId());
        StringBuilder content = new StringBuilder();
        if (notifs.isEmpty()) {
            content.append("Alerte : E-mail de sécurité envoyé.");
        } else {
            for (com.example.mindjavafx.model.Notification n : notifs) {
                content.append(n.isRead() ? "✓ " : " ").append(n.getTitle()).append(" : ").append(n.getMessage()).append("\n");
            }
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notifications");
        alert.setHeaderText("Notifications de " + currentUser.getNom());
        alert.setContentText(content.toString());
        notificationService.markAllAsRead(currentUser.getId());
        updateNotificationBadge();
        alert.showAndWait();
    }

    private void setupTranslationContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem itemFr = new MenuItem("Traduire en Français");
        itemFr.setOnAction(e -> translateEntirePage("Français"));
        MenuItem itemEn = new MenuItem("Traduire en Anglais");
        itemEn.setOnAction(e -> translateEntirePage("Anglais"));
        MenuItem itemAr = new MenuItem("Traduire en Arabe");
        itemAr.setOnAction(e -> translateEntirePage("Arabe"));
        contextMenu.getItems().addAll(itemFr, itemEn, itemAr);
        mainBorderPane.setOnContextMenuRequested(event -> {
            contextMenu.show(mainBorderPane, event.getScreenX(), event.getScreenY());
        });
    }

    private void translateEntirePage(String targetLang) {
        translateNode(mainBorderPane, targetLang);
        if (!contentArea.getChildren().isEmpty()) {
            translateNode(contentArea.getChildren().get(0), targetLang);
        }
    }

    private void translateNode(Node node, String targetLang) {
        if (node instanceof Labeled) {
            Labeled labeled = (Labeled) node;
            String original = labeled.getText();
            if (original != null && !original.isEmpty()) {
                labeled.setText(aiService.translate(original, targetLang));
            }
        } else if (node instanceof TextField) {
            TextField tf = (TextField) node;
            String prompt = tf.getPromptText();
            if (prompt != null && !prompt.isEmpty()) {
                tf.setPromptText(aiService.translate(prompt, targetLang));
            }
        }
        if (node instanceof javafx.scene.Parent) {
            for (Node child : ((javafx.scene.Parent) node).getChildrenUnmodifiable()) {
                translateNode(child, targetLang);
            }
        }
    }

    @FXML
    public void toggleDarkMode(ActionEvent event) {
        System.out.println("[DEBUG] Bouton Lune cliqué. Mode Sombre Actuel: " + isDarkMode);
        Scene scene = contentArea.getScene();
        if (scene == null) return;
        
        // Get button position for the ripple start
        Node source = (Node) event.getSource();
        javafx.geometry.Point2D pos = source.localToScene(source.getBoundsInLocal().getWidth()/2, source.getBoundsInLocal().getHeight()/2);
        
        double centerX = pos.getX();
        double centerY = pos.getY();
        double w = scene.getWidth();
        double h = scene.getHeight();
        
        // Max radius to cover screen
        double maxRadius = Math.sqrt(Math.pow(Math.max(centerX, w - centerX), 2) + Math.pow(Math.max(centerY, h - centerY), 2)) + 200;
        
        moonlightCircle.setCenterX(centerX);
        moonlightCircle.setCenterY(centerY);
        moonlightCircle.setRadius(0);
        moonlightCircle.setOpacity(1.0);
        moonlightCircle.setVisible(true);
        
        // Use a "Moonlight" color (Deep black for dark, Soft blue-white for light)
        if (isDarkMode) {
            moonlightCircle.setFill(javafx.scene.paint.Color.web("#f1f5f9"));
        } else {
            moonlightCircle.setFill(javafx.scene.paint.Color.web("#020617"));
        }

        // Circular expansion animation
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
            new javafx.animation.KeyFrame(Duration.ZERO, 
                new javafx.animation.KeyValue(moonlightCircle.radiusProperty(), 0)
            ),
            new javafx.animation.KeyFrame(Duration.millis(800), 
                new javafx.animation.KeyValue(moonlightCircle.radiusProperty(), maxRadius, javafx.animation.Interpolator.EASE_IN)
            )
        );
        
        timeline.setOnFinished(e -> {
            Parent root = scene.getRoot();
            if (isDarkMode) {
                root.getStyleClass().remove("dark-theme");
                contentArea.setStyle("-fx-background-color: #f8fafc;");
                isDarkMode = false;
            } else {
                root.getStyleClass().add("dark-theme");
                contentArea.setStyle("-fx-background-color: #020617;");
                isDarkMode = true;
            }
            
            // Sync with WebView theme if active
            if (currentSectionController instanceof UserDashboardHomeController) {
                ((UserDashboardHomeController) currentSectionController).toggleTheme();
            }
            
            // Fade out the overlay circle after theme switch
            javafx.animation.FadeTransition fadeOut = new javafx.animation.FadeTransition(Duration.millis(500), moonlightCircle);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(ev -> moonlightCircle.setVisible(false));
            fadeOut.play();
        });
        
        timeline.play();
    }

    @FXML
    private void handleLogout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("MindAudit - Connexion");
        } catch (IOException e) { e.printStackTrace(); }
    }
}
