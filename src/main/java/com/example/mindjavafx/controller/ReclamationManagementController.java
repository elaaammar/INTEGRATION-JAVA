package com.example.mindjavafx.controller;

import com.gestionaudit.models.Reclamation;
import com.gestionaudit.models.ReponseReclamation;
import com.gestionaudit.services.ReclamationService;
import com.gestionaudit.services.ReponseReclamationService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class ReclamationManagementController {

    @FXML private Label  lblTotal;
    @FXML private Label  lblEnAttente;
    @FXML private Label  lblResolues;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterStatut;
    @FXML private ComboBox<String> filterPriorite;
    @FXML private ListView<Reclamation> listReclamations;
    @FXML private VBox  placeholderPanel;
    @FXML private VBox  contentPanel;
    @FXML private Label detailStatutBadge;
    @FXML private Label detailPrioriteBadge;
    @FXML private Label detailCategorie;
    @FXML private Label detailTitre;
    @FXML private Label detailDate;
    @FXML private Label detailNom;
    @FXML private Label detailEmail;
    @FXML private Label detailTel;
    @FXML private Label detailDescription;
    @FXML private ComboBox<String> comboNewStatut;
    @FXML private ScrollPane scrollReplies;
    @FXML private VBox repliesContainer;
    @FXML private TextArea replyField;

    private final ReclamationService recService = new ReclamationService();
    private final ReponseReclamationService repService = new ReponseReclamationService();

    private List<Reclamation> allReclamations;
    private Reclamation selected;

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        filterStatut.getItems().addAll("Tous", "en_attente", "en_cours", "resolue", "cloturee");
        filterStatut.setValue("Tous");
        filterPriorite.getItems().addAll("Tous", "haute", "moyenne", "basse");
        filterPriorite.setValue("Tous");
        comboNewStatut.getItems().addAll("en_attente", "en_cours", "resolue", "cloturee");

        listReclamations.setCellFactory(lv -> new ListCell<Reclamation>() {
            @Override
            protected void updateItem(Reclamation item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent; -fx-padding: 0;");
                    return;
                }
                setStyle("-fx-background-color: transparent; -fx-padding: 4 0;");
                setGraphic(buildCard(item));
            }
        });

        listReclamations.getSelectionModel().selectedItemProperty().addListener(
            (obs, old, neu) -> { if (neu != null) showDetail(neu); });

        searchField.textProperty().addListener((obs, o, n) -> applyFilter());

        Platform.runLater(this::loadAll);
    }

    private void loadAll() {
        try {
            allReclamations = recService.getAll();
            applyFilter();
            updateCounters(allReclamations);
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de charger : " + e.getMessage());
        }
    }

    @FXML private void refreshList() { loadAll(); }

    @FXML private void resetFilter() {
        searchField.clear();
        filterStatut.setValue("Tous");
        filterPriorite.setValue("Tous");
        applyFilter();
    }

    @FXML private void applyFilter() {
        if (allReclamations == null) return;
        String q  = searchField.getText() == null ? "" : searchField.getText().toLowerCase();
        String st = filterStatut.getValue();
        String pr = filterPriorite.getValue();

        List<Reclamation> filtered = allReclamations.stream().filter(r -> {
            boolean matchQ  = q.isEmpty()
                || safe(r.getTitre()).toLowerCase().contains(q)
                || safe(r.getNom()).toLowerCase().contains(q)
                || safe(r.getEmail()).toLowerCase().contains(q)
                || safe(r.getCategorie()).toLowerCase().contains(q);
            boolean matchSt = "Tous".equals(st) || safe(r.getStatut()).equals(st);
            boolean matchPr = "Tous".equals(pr) || safe(r.getPriorite()).equals(pr);
            return matchQ && matchSt && matchPr;
        }).collect(Collectors.toList());

        listReclamations.getItems().setAll(filtered);
        updateCounters(filtered);
    }

    private void updateCounters(List<Reclamation> list) {
        long enAttente = list.stream().filter(r -> "en_attente".equals(r.getStatut())).count();
        long resolues  = list.stream().filter(r -> "resolue".equals(r.getStatut())).count();
        if (lblTotal    != null) lblTotal.setText(list.size() + " total");
        if (lblEnAttente!= null) lblEnAttente.setText(enAttente + " en attente");
        if (lblResolues != null) lblResolues.setText(resolues + " résolues");
    }

    private void showDetail(Reclamation r) {
        this.selected = r;
        if (placeholderPanel != null) { placeholderPanel.setVisible(false); placeholderPanel.setManaged(false); }
        if (contentPanel     != null) { contentPanel.setVisible(true);      contentPanel.setManaged(true); }

        if (detailTitre       != null) detailTitre.setText(safe(r.getTitre()));
        if (detailDate        != null) detailDate.setText(
            r.getDateCreation() != null ? "Soumis le " + r.getDateCreation().format(FMT) : "");
        if (detailCategorie   != null) detailCategorie.setText(safe(r.getCategorie()));
        if (detailNom         != null) detailNom.setText(safe(r.getNom()));
        if (detailEmail       != null) detailEmail.setText(safe(r.getEmail()));
        if (detailTel         != null) detailTel.setText(safe(r.getTelephone()));
        if (detailDescription != null) detailDescription.setText(safe(r.getDescription()));

        if (detailStatutBadge   != null) styleBadgeStatut(detailStatutBadge, r.getStatut());
        if (detailPrioriteBadge != null) styleBadgePriorite(detailPrioriteBadge, r.getPriorite());
        if (comboNewStatut      != null) comboNewStatut.setValue(r.getStatut());

        loadReplies(r.getId());
    }

    private void loadReplies(int reclamationId) {
        if (repliesContainer == null) return;
        repliesContainer.getChildren().clear();
        try {
            List<ReponseReclamation> replies = repService.getByReclamationId(reclamationId);
            if (replies.isEmpty()) {
                Label empty = new Label("Aucune réponse pour l'instant.");
                empty.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12; -fx-padding: 10;");
                repliesContainer.getChildren().add(empty);
            } else {
                for (ReponseReclamation rep : replies) {
                    repliesContainer.getChildren().add(buildReplyBubble(rep));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (scrollReplies != null)
            Platform.runLater(() -> scrollReplies.setVvalue(1.0));
    }

    @FXML private void sendReply() {
        if (selected == null || replyField == null) return;
        String text = replyField.getText();
        if (text == null || text.isBlank()) return;
        try {
            ReponseReclamation rep = new ReponseReclamation();
            rep.setContenu(text.trim());
            rep.setReclamationId(selected.getId());
            rep.setAuteurType("admin");
            rep.setNom("Administrateur");
            repService.add(rep);
            replyField.clear();
            loadReplies(selected.getId());
        } catch (Exception e) {
            showAlert("Erreur", "Envoi impossible : " + e.getMessage());
        }
    }

    @FXML private void applyStatutChange() {
        if (selected == null || comboNewStatut == null) return;
        String newStatut = comboNewStatut.getValue();
        if (newStatut == null) return;
        try {
            selected.setStatut(newStatut);
            recService.update(selected);
            if (detailStatutBadge != null) styleBadgeStatut(detailStatutBadge, newStatut);
            listReclamations.refresh();
            updateCounters(listReclamations.getItems());
        } catch (Exception e) {
            showAlert("Erreur", e.getMessage());
        }
    }

    @FXML private void deleteReclamation() {
        if (selected == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
            "Supprimer définitivement cette réclamation ?", ButtonType.YES, ButtonType.CANCEL);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText(safe(selected.getTitre()));
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                try {
                    recService.delete(selected.getId());
                    allReclamations.remove(selected);
                    selected = null;
                    if (placeholderPanel != null) { placeholderPanel.setVisible(true);  placeholderPanel.setManaged(true); }
                    if (contentPanel     != null) { contentPanel.setVisible(false);     contentPanel.setManaged(false); }
                    applyFilter();
                } catch (Exception e) {
                    showAlert("Erreur", e.getMessage());
                }
            }
        });
    }

    // ── Card builder ──────────────────────────────────────────────────
    private VBox buildCard(Reclamation r) {
        VBox card = new VBox(8);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 14 16;"
                    + "-fx-border-color: #e2e8f0; -fx-border-width: 1; -fx-border-radius: 12;"
                    + "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.04),8,0,0,2); -fx-cursor: hand;");

        HBox badgeRow = new HBox(8);
        badgeRow.setAlignment(Pos.CENTER_LEFT);

        Label statBadge = new Label();
        styleBadgeStatut(statBadge, r.getStatut());
        Label prioBadge = new Label();
        styleBadgePriorite(prioBadge, r.getPriorite());
        Label catLabel = new Label(safe(r.getCategorie()));
        catLabel.setStyle("-fx-font-size: 10; -fx-text-fill: #94a3b8;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        String dateStr = r.getDateCreation() != null ? r.getDateCreation().format(FMT) : "";
        Label dateLabel = new Label(dateStr);
        dateLabel.setStyle("-fx-font-size: 10; -fx-text-fill: #94a3b8;");
        badgeRow.getChildren().addAll(statBadge, prioBadge, catLabel, spacer, dateLabel);

        Label title = new Label(safe(r.getTitre()));
        title.setStyle("-fx-font-size: 14; -fx-font-weight: 800; -fx-text-fill: #0f172a; -fx-wrap-text: true;");
        title.setMaxWidth(Double.MAX_VALUE);

        Label client = new Label("👤  " + safe(r.getNom()) + "   ·   " + safe(r.getEmail()));
        client.setStyle("-fx-font-size: 11; -fx-text-fill: #64748b;");

        card.getChildren().addAll(badgeRow, title, client);
        return card;
    }

    // ── Reply bubble ──────────────────────────────────────────────────
    private HBox buildReplyBubble(ReponseReclamation rep) {
        boolean isAdmin = "admin".equals(rep.getAuteurType());
        VBox bubble = new VBox(4);
        bubble.setMaxWidth(320);

        if (isAdmin) {
            bubble.setStyle("-fx-background-color: #2563eb; -fx-background-radius: 14 14 4 14; -fx-padding: 10 14;");
        } else {
            bubble.setStyle("-fx-background-color: white; -fx-background-radius: 14 14 14 4; -fx-padding: 10 14;"
                          + "-fx-border-color: #e2e8f0; -fx-border-width: 1; -fx-border-radius: 14 14 14 4;");
        }

        Label author = new Label(isAdmin ? "🛡  Administrateur" : "👤  " + safe(rep.getNom()));
        author.setStyle("-fx-font-size: 10; -fx-font-weight: bold; -fx-text-fill: "
                      + (isAdmin ? "rgba(255,255,255,0.7)" : "#94a3b8") + ";");

        Label content = new Label(safe(rep.getContenu()));
        content.setWrapText(true);
        content.setStyle("-fx-font-size: 13; -fx-text-fill: " + (isAdmin ? "white" : "#0f172a") + ";");

        String dateStr = rep.getDateCreation() != null ? rep.getDateCreation().format(FMT) : "";
        Label date = new Label(dateStr);
        date.setStyle("-fx-font-size: 9; -fx-text-fill: " + (isAdmin ? "rgba(255,255,255,0.5)" : "#cbd5e1") + ";");

        bubble.getChildren().addAll(author, content, date);

        HBox row = new HBox(bubble);
        row.setAlignment(isAdmin ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        return row;
    }

    // ── Style helpers ─────────────────────────────────────────────────
    private void styleBadgeStatut(Label lbl, String statut) {
        String s = statut != null ? statut : "";
        String text;
        String bg;
        String fg;
        if ("en_attente".equals(s))      { text = "EN ATTENTE"; bg = "#fef3c7"; fg = "#b45309"; }
        else if ("en_cours".equals(s))   { text = "EN COURS";   bg = "#dbeafe"; fg = "#1d4ed8"; }
        else if ("resolue".equals(s))    { text = "RÉSOLUE";    bg = "#d1fae5"; fg = "#047857"; }
        else if ("cloturee".equals(s))   { text = "CLÔTURÉE";   bg = "#f1f5f9"; fg = "#475569"; }
        else                             { text = s.toUpperCase(); bg = "#f1f5f9"; fg = "#475569"; }
        lbl.setText(text);
        lbl.setStyle("-fx-background-color: " + bg + "; -fx-text-fill: " + fg
                   + "; -fx-font-size: 10; -fx-font-weight: 800; -fx-padding: 4 10; -fx-background-radius: 20;");
    }

    private void styleBadgePriorite(Label lbl, String prio) {
        String p = prio != null ? prio.toLowerCase() : "";
        String text;
        String bg;
        String fg;
        if ("haute".equals(p))          { text = "🔴 HAUTE";   bg = "#fee2e2"; fg = "#b91c1c"; }
        else if ("moyenne".equals(p))   { text = "🟡 MOYENNE"; bg = "#fef3c7"; fg = "#b45309"; }
        else if ("basse".equals(p))     { text = "🟢 BASSE";   bg = "#d1fae5"; fg = "#047857"; }
        else                            { text = p.toUpperCase(); bg = "#f1f5f9"; fg = "#64748b"; }
        lbl.setText(text);
        lbl.setStyle("-fx-background-color: " + bg + "; -fx-text-fill: " + fg
                   + "; -fx-font-size: 10; -fx-font-weight: 800; -fx-padding: 4 10; -fx-background-radius: 20;");
    }

    private String safe(String s) { return s != null ? s : ""; }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg != null ? msg : "Erreur inconnue", ButtonType.OK);
        a.setTitle(title);
        a.setHeaderText(null);
        a.showAndWait();
    }
}
