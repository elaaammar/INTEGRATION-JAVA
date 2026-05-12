package com.gestionaudit.controllers;

import com.gestionaudit.utils.BadWordEnforcement;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONObject;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import javafx.geometry.Pos;

import javax.sound.sampled.*;
import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.*;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.example.mindjavafx.controller.UserDashboardController;
import com.example.mindjavafx.model.User;
import com.example.mindjavafx.util.ConfigLoader;
import com.audit.auditaifx.model.RapportAudit;
import com.audit.auditaifx.model.StatutRapport;
import com.audit.auditaifx.service.RapportService;
import java.time.LocalDate;
import java.util.ArrayList;

public class ChatbotController {

    private UserDashboardController dashboardController;

    public void setDashboardController(UserDashboardController controller) {
        this.dashboardController = controller;
    }

    public void setInitialPrompt(String prompt) {
        if (prompt != null && !prompt.isEmpty()) {
            messageField.setText(prompt);
            handleSend();
        }
    }

    @FXML private ScrollPane scrollPane;
    @FXML private VBox chatHistory;
    @FXML private TextField messageField;
    @FXML private Button btnMic;
    @FXML private VBox welcomeHeader;

    private boolean isRecording = false;
    private TargetDataLine micLine;
    private File audioFile;

    private static final String API_KEY = ConfigLoader.getProperty("groq.api.key", "YOUR_GROQ_API_KEY");
    private static final String CHAT_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String WHISPER_URL = "https://api.groq.com/openai/v1/audio/transcriptions";
    private static final String CHAT_MODEL = ConfigLoader.getProperty("groq.chat.model", "llama-3.3-70b-versatile");
    private static final String WHISPER_MODEL = "whisper-large-v3";

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(java.time.Duration.ofSeconds(15))
            .build();

    @FXML
    public void initialize() {
        // nothing to setup
    }

    @FXML
    public void toggleRecording() {
        if (!isRecording) {
            startRecording();
        } else {
            stopRecordingAndTranscribe();
        }
    }

    private void startRecording() {
        try {
            AudioFormat format = new AudioFormat(16000f, 16, 1, true, false);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            TargetDataLine selectedLine = null;
            for (Mixer.Info mixerInfo : AudioSystem.getMixerInfo()) {
                Mixer mixer = AudioSystem.getMixer(mixerInfo);
                String name = mixerInfo.getName().toLowerCase();
                if (name.contains("stereo mix") || name.contains("what u hear") || name.contains("loopback")) {
                    continue;
                }
                if (mixer.isLineSupported(info)) {
                    try {
                        selectedLine = (TargetDataLine) mixer.getLine(info);
                        break;
                    } catch (LineUnavailableException ignored) {}
                }
            }

            if (selectedLine == null) {
                addMessageToChat("⚠️ Microphone non disponible.", false);
                return;
            }

            micLine = selectedLine;
            micLine.open(format);
            micLine.start();

            audioFile = File.createTempFile("groq_audio_", ".wav");
            isRecording = true;
            btnMic.setText("🛑");
            btnMic.setStyle("-fx-font-size: 18px; -fx-text-fill: #ef4444; -fx-border-color: #ef4444;");

            CompletableFuture.runAsync(() -> {
                try (AudioInputStream ais = new AudioInputStream(micLine)) {
                    AudioSystem.write(ais, AudioFileFormat.Type.WAVE, audioFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            addMessageToChat("⚠️ Erreur microphone : " + e.getMessage(), false);
        }
    }

    private void stopRecordingAndTranscribe() {
        if (micLine != null) {
            micLine.stop();
            micLine.close();
        }

        isRecording = false;
        btnMic.setText("🎤");
        btnMic.setStyle("-fx-font-size: 18px;");
        addMessageToChat("🎙️ Transcription...", false);

        CompletableFuture.supplyAsync(() -> transcribeWithWhisper(audioFile))
                .thenAccept(transcript -> {
                    javafx.application.Platform.runLater(() -> {
                        if (!chatHistory.getChildren().isEmpty()) {
                            chatHistory.getChildren().remove(chatHistory.getChildren().size() - 1);
                        }
                        if (transcript == null || transcript.isBlank()) {
                            addMessageToChat("⚠️ Aucun texte détecté.", false);
                            return;
                        }
                        messageField.setText(transcript);
                        // handleSend(); // Ne pas envoyer automatiquement pour permettre la vérification
                    });
                })
                .exceptionally(ex -> {
                    javafx.application.Platform.runLater(() ->
                            addMessageToChat("⚠️ Erreur : " + ex.getMessage(), false));
                    return null;
                });
    }

    private String transcribeWithWhisper(File wavFile) {
        try {
            String boundary = UUID.randomUUID().toString().replace("-", "");
            byte[] fileBytes = Files.readAllBytes(wavFile.toPath());
            ByteArrayOutputStream body = new ByteArrayOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(body, "UTF-8"), true);

            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"model\"").append("\r\n\r\n");
            writer.append(WHISPER_MODEL).append("\r\n");
            writer.flush();

            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"language\"").append("\r\n\r\n");
            writer.append("fr").append("\r\n");
            writer.flush();

            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"audio.wav\"").append("\r\n");
            writer.append("Content-Type: audio/wav").append("\r\n\r\n");
            writer.flush();
            body.write(fileBytes);
            writer.append("\r\n");
            writer.append("--").append(boundary).append("--").append("\r\n");
            writer.flush();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(WHISPER_URL))
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(body.toByteArray()))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return new JSONObject(response.body()).getString("text").trim();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @FXML
    private void handleSend() {
        String msg = messageField.getText().trim();
        if (msg.isEmpty()) return;

        javafx.stage.Window win = messageField.getScene() != null ? messageField.getScene().getWindow() : null;
        if (BadWordEnforcement.blockIfViolating(msg, win)) return;

        addMessageToChat(msg, true);
        messageField.clear();

        callGroqAPI(msg).thenAccept(reply -> {
            javafx.application.Platform.runLater(() -> {
                addMessageToChat(reply, false);
                processAIAction(reply); // Reactivated and enhanced
                Notifications.create()
                        .title("IA Gestion Audit")
                        .text("Nouvelle réponse.")
                        .position(Pos.TOP_RIGHT)
                        .hideAfter(Duration.seconds(3))
                        .showInformation();
            });
        }).exceptionally(ex -> {
            javafx.application.Platform.runLater(() ->
                    addMessageToChat("Erreur technique : " + ex.getMessage(), false));
            return null;
        });
    }

    private void addMessageToChat(String content, boolean isUser) {
        if (!scrollPane.isVisible()) {
            scrollPane.setVisible(true);
            scrollPane.setManaged(true);
            if (welcomeHeader != null) {
                welcomeHeader.setVisible(false);
                welcomeHeader.setManaged(false);
            }
        }
        HBox row = new HBox();
        row.setAlignment(isUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        VBox bubble = new VBox(5);
        bubble.getStyleClass().add(isUser ? "chat-bubble-client" : "chat-bubble-admin");
        bubble.setMaxWidth(450);
        Label textLabel = new Label(content);
        textLabel.setWrapText(true);
        textLabel.getStyleClass().add("chat-bubble-text");
        textLabel.setStyle(isUser ? "-fx-text-fill: white;" : "-fx-text-fill: black;");
        bubble.getChildren().add(textLabel);
        row.getChildren().add(bubble);
        chatHistory.getChildren().add(row);
        javafx.application.Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }

    private CompletableFuture<String> callGroqAPI(String prompt) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", CHAT_MODEL);
        JSONArray messages = new JSONArray();
        JSONObject systemMsg = new JSONObject();
        systemMsg.put("content", "Tu es MindAudit AI, un assistant intelligent spécialisé en audit et conformité. " +
                "Ton objectif est d'aider l'utilisateur et d'automatiser des tâches. " +
                "TU PEUX CRÉER DES RAPPORTS D'AUDIT. Si l'utilisateur te donne des informations sur un audit, propose de créer le rapport. " +
                "Pour créer un rapport, inclus dans ta réponse le tag : [ACTION:CREATE_REPORT:Titre|Description] " +
                "Pour naviguer, utilise : [ACTION:REPORTS], [ACTION:ENTREPRISE], [ACTION:RECLAMATION]. " +
                "Réponds sous forme de courts messages de chat. Sois concis et direct.");
        messages.put(systemMsg);
        JSONObject userMsg = new JSONObject();
        userMsg.put("role", "user");
        userMsg.put("content", prompt);
        messages.put(userMsg);
        requestBody.put("messages", messages);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(CHAT_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    int code = response.statusCode();
                    if (code == 200) {
                        return new JSONObject(response.body())
                                .getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content").trim();
                    }
                    System.err.println("Groq API Error: " + code + " - " + response.body());
                    if (code == 401) return "Erreur API : Clé API invalide ou expirée (401).";
                    if (code == 429) return "Erreur API : Trop de requêtes (Rate limit 429).";
                    if (code == 400) return "Erreur API : Requête mal formée (400). " + response.body();
                    if (code == 404) return "Erreur API : Modèle non trouvé (404). Vérifiez le nom du modèle.";
                    return "Erreur API (Code " + code + "): " + response.body();
                }).exceptionally(ex -> {
                    System.err.println("Groq API Exception: " + ex.getMessage());
                    ex.printStackTrace();
                    return "Exception technique: " + ex.getMessage();
                });
    }

    private void processAIAction(String reply) {
        if (dashboardController == null) return;

        if (reply.contains("[ACTION:CREATE_REPORT:")) {
            try {
                int start = reply.indexOf("[ACTION:CREATE_REPORT:") + 22;
                int end = reply.indexOf("]", start);
                String data = reply.substring(start, end);
                String[] parts = data.split("\\|", 2);
                if (parts.length == 2) {
                    createReportAutomatically(parts[0], parts[1]);
                }
            } catch (Exception e) {
                System.err.println("Error parsing CREATE_REPORT action: " + e.getMessage());
            }
        }

        if (reply.contains("[ACTION:REPORTS]")) {
            dashboardController.showRapportManagement();
        } else if (reply.contains("[ACTION:ENTREPRISE]")) {
            dashboardController.showEntrepriseManagement();
        } else if (reply.contains("[ACTION:RECLAMATION]")) {
            dashboardController.showReclamations();
        }
    }

    private void createReportAutomatically(String titre, String description) {
        try {
            RapportService service = new RapportService();
            RapportAudit rapport = new RapportAudit();
            rapport.setTitre(titre);
            rapport.setDescription(description);
            rapport.setAuditeur(dashboardController.getCurrentUser() != null ? dashboardController.getCurrentUser().getNom() : "AI Assistant");
            rapport.setEntiteAuditee("Non spécifié");
            rapport.setStatut(StatutRapport.EN_COURS);
            rapport.setDateCreation(LocalDate.now());
            rapport.setDateMiseAJour(LocalDate.now());
            rapport.setRecommandations(new ArrayList<>());
            rapport.setRisques(new ArrayList<>());
            
            service.ajouter(rapport);
            
            javafx.application.Platform.runLater(() -> {
                Notifications.create()
                    .title("Succès")
                    .text("Rapport '" + titre + "' créé avec succès !")
                    .position(Pos.BOTTOM_RIGHT)
                    .showConfirm();
                
                // Show the reports list to let user see the new report
                dashboardController.showRapportManagement();
            });
        } catch (Exception e) {
            e.printStackTrace();
            javafx.application.Platform.runLater(() -> 
                addMessageToChat("⚠️ Erreur lors de la création du rapport : " + e.getMessage(), false)
            );
        }
    }
}
