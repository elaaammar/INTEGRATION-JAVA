package com.example.mindjavafx.controller;

import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import com.example.mindjavafx.service.UserService;
import com.example.mindjavafx.service.NotificationService;
import com.audit.auditaifx.service.RapportService;
import com.example.mindjavafx.model.User;
import com.audit.auditaifx.model.RapportAudit;
import com.audit.auditaifx.model.StatutRapport;
import com.audit.auditaifx.model.Recommandation;
import com.audit.auditaifx.model.Risque;
import javafx.application.Platform;
import javafx.scene.Node;
import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.format.TextStyle;
import javafx.animation.*;
import javafx.util.Duration;
import com.example.mindjavafx.service.AuditService;

public class AdminDashboardHomeController {

    // ── HEADER ──────────────────────────────────────────────────────
    @FXML private Label welcomeNameLabel;
    @FXML private Label lblLastRefresh;

    // ── KPI CARDS ───────────────────────────────────────────────────
    @FXML private Label lblScoreGlobal;
    @FXML private Label lblScoreSubtitle;
    @FXML private Label lblScoreTrend;

    @FXML private Label lblTotalReports;
    @FXML private Label lblAuditsTrend;

    @FXML private Label lblTotalRecos;
    @FXML private Label lblResolutionRate;
    @FXML private Label lblRecosTrend;

    @FXML private Label lblTotalEntreprises;
    @FXML private Label lblEntrepriseTrend;

    @FXML private Label lblTotalUsers;
    @FXML private Label lblActiveUsers;

    @FXML private Label lblNotifications;

    // ── CHARTS ──────────────────────────────────────────────────────
    @FXML private LineChart<String, Number>  chartTendance;     // audit scores over time
    @FXML private PieChart                   chartStatus;       // report statuses
    @FXML private BarChart<String, Number>   chartPriorities;   // recommendations by priority
    @FXML private PieChart                   chartUserRoles;    // user roles distribution

    // ── PROGRESS BARS ───────────────────────────────────────────────
    @FXML private ProgressBar pbHaute;
    @FXML private Label       lblPbHaute;
    @FXML private ProgressBar pbMoyenne;
    @FXML private Label       lblPbMoyenne;
    @FXML private ProgressBar pbFaible;
    @FXML private Label       lblPbFaible;

    // ── RECENT ACTIVITY ─────────────────────────────────────────────
    @FXML private Label lblRecent1;
    @FXML private Label lblRecent2;
    @FXML private Label lblRecent3;
    @FXML private Label lblRecent4;
    @FXML private Label lblRecent5;

    // ── SERVICES ────────────────────────────────────────────────────
    private DashboardController dashboardController;
    private UserService userService;
    private RapportService rapportService;
    private NotificationService notificationService;
    private AuditService auditService;
    private com.gestion.service.EntrepriseService entrepriseService;
    private Timeline refreshTimeline;

    @FXML
    public void initialize() {
        try { userService         = new UserService(); }         catch (Throwable e) { System.err.println("UserService: " + e.getMessage()); }
        try { rapportService      = new RapportService(); }      catch (Throwable e) { System.err.println("RapportService: " + e.getMessage()); }
        try { notificationService = new NotificationService(); } catch (Throwable e) { System.err.println("NotifService: " + e.getMessage()); }
        try { auditService        = new AuditService(); }        catch (Throwable e) { System.err.println("AuditService: " + e.getMessage()); }
        try { entrepriseService   = new com.gestion.service.EntrepriseService(); } catch (Throwable e) { System.err.println("EntrepriseService: " + e.getMessage()); }

        Platform.runLater(this::loadStatistics);

        refreshTimeline = new Timeline(new KeyFrame(Duration.seconds(30), e -> loadStatistics()));
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
        refreshTimeline.play();
    }

    @FXML public void refreshData() { loadStatistics(); }

    public void setDashboardController(DashboardController dc) {
        this.dashboardController = dc;
        updateWelcomeMessage();
    }

    private void updateWelcomeMessage() {
        try {
            if (dashboardController != null && welcomeNameLabel != null) {
                User u = dashboardController.getCurrentUser();
                if (u != null) welcomeNameLabel.setText(u.getNom());
            }
        } catch (Throwable ignored) {}
    }

    // ════════════════════════════════════════════════════════════════
    //  MAIN LOAD
    // ════════════════════════════════════════════════════════════════
    private void loadStatistics() {
        try {
            updateWelcomeMessage();
            if (lblLastRefresh != null)
                lblLastRefresh.setText("Mis à jour : " + java.time.LocalTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")));

            // ── Fetch all reports ──────────────────────────────────
            List<RapportAudit> reports = new ArrayList<>();
            try { if (rapportService != null) reports = rapportService.getTous(); } catch (Throwable ignored) {}

            // ── Fetch users ────────────────────────────────────────
            List<User> users = new ArrayList<>();
            try { if (userService != null) users = userService.getAllUsers(); } catch (Throwable ignored) {}

            // ── Fetch enterprises ──────────────────────────────────
            int enterpriseCount = 0;
            try { if (entrepriseService != null) enterpriseCount = entrepriseService.findAll().size(); } catch (Throwable ignored) {}

            // ── Crunch numbers ────────────────────────────────────
            int reportCount = reports.size();
            int userCount   = users.size();
            int activeCount = (int) users.stream().filter(User::isActif).count();

            // Score parsing
            double scoreSum = 0; int scoredCount = 0;
            for (RapportAudit r : reports) {
                String s = r.getScoreAudit();
                if (s != null) {
                    try {
                        double v = Double.parseDouble(s.split("/")[0].replaceAll("[^0-9.]", ""));
                        if (v <= 100) { scoreSum += v; scoredCount++; }
                    } catch (Exception ignored) {}
                }
            }
            double avgScore = scoredCount > 0 ? scoreSum / scoredCount : 0;

            // Status counts
            long cBrouillon   = reports.stream().filter(r -> r.getStatut() == StatutRapport.BROUILLON).count();
            long cEnCours     = reports.stream().filter(r -> r.getStatut() == StatutRapport.EN_COURS).count();
            long cFinalise    = reports.stream().filter(r -> r.getStatut() == StatutRapport.FINALISE).count();

            // Recommendations
            List<Recommandation> allRecos = reports.stream()
                .flatMap(r -> r.getRecommandations() != null ? r.getRecommandations().stream() : java.util.stream.Stream.empty())
                .collect(Collectors.toList());
            int totalRecos  = allRecos.size();
            int resolues    = (int) allRecos.stream().filter(Recommandation::isResolue).count();
            long pHaute     = allRecos.stream().filter(r -> r.getPriorite() != null && r.getPriorite().toLowerCase().contains("haut")).count();
            long pMoyenne   = allRecos.stream().filter(r -> r.getPriorite() != null && r.getPriorite().toLowerCase().contains("moy")).count();
            long pFaible    = allRecos.stream().filter(r -> r.getPriorite() != null && (r.getPriorite().toLowerCase().contains("fa") || r.getPriorite().toLowerCase().contains("bas"))).count();

            // Notifications
            int notifCount = 0;
            try {
                if (notificationService != null && dashboardController != null) {
                    User cu = dashboardController.getCurrentUser();
                    if (cu != null) notifCount = notificationService.getUnreadCount(cu.getId());
                }
            } catch (Throwable ignored) {}

            // ── Update KPI Labels ─────────────────────────────────
            set(lblTotalReports, String.valueOf(reportCount));
            set(lblAuditsTrend, cFinalise + " finalisés");
            set(lblTotalRecos, String.valueOf(totalRecos));
            int resoRate = totalRecos > 0 ? (resolues * 100 / totalRecos) : 0;
            set(lblResolutionRate, resoRate + "% résolues");
            set(lblRecosTrend, pHaute + " priorité haute");
            set(lblTotalEntreprises, String.valueOf(enterpriseCount));
            set(lblEntrepriseTrend, "inscrites");
            set(lblTotalUsers, String.valueOf(userCount));
            set(lblActiveUsers, activeCount + " actifs");
            set(lblNotifications, String.valueOf(notifCount));

            if (scoredCount > 0) {
                set(lblScoreGlobal, String.format(Locale.ROOT, "%.1f", avgScore));
                set(lblScoreSubtitle, "/ 100  (" + scoredCount + " audits)");
                String trend = avgScore >= 75 ? "✅ Bon" : avgScore >= 50 ? "⚠️ Moyen" : "🔴 Critique";
                set(lblScoreTrend, trend);
            } else {
                set(lblScoreGlobal, "--");
                set(lblScoreSubtitle, "/ 100  (aucun audit scoré)");
                set(lblScoreTrend, "—");
            }

            // ── Progress bars (reco priorities) ───────────────────
            double total = Math.max(1, pHaute + pMoyenne + pFaible);
            if (pbHaute   != null) pbHaute.setProgress(pHaute   / total);
            if (pbMoyenne != null) pbMoyenne.setProgress(pMoyenne / total);
            if (pbFaible  != null) pbFaible.setProgress(pFaible  / total);
            set(lblPbHaute,   pHaute   + " (" + Math.round(pHaute   * 100 / total) + "%)");
            set(lblPbMoyenne, pMoyenne + " (" + Math.round(pMoyenne * 100 / total) + "%)");
            set(lblPbFaible,  pFaible  + " (" + Math.round(pFaible  * 100 / total) + "%)");

            // ── CHARTS ────────────────────────────────────────────
            buildLineChart(reports);
            buildStatusPie(cBrouillon, cEnCours, cFinalise);
            buildPriorityBar(pHaute, pMoyenne, pFaible);
            buildRolePie(users);
            buildRecentActivity(reports);

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  CHART BUILDERS
    // ════════════════════════════════════════════════════════════════

    /** Line chart: last 6 months — number of audits created per month */
    private void buildLineChart(List<RapportAudit> reports) {
        try {
            if (chartTendance == null) return;
            chartTendance.getData().clear();

            // Group by month name (last 6 months order)
            Map<String, Long> byMonth = new LinkedHashMap<>();
            LocalDate now = LocalDate.now();
            for (int i = 5; i >= 0; i--) {
                LocalDate m = now.minusMonths(i);
                String key = m.getMonth().getDisplayName(TextStyle.SHORT, Locale.FRENCH)
                             + " " + m.getYear();
                byMonth.put(key, 0L);
            }
            for (RapportAudit r : reports) {
                if (r.getDateCreation() != null) {
                    LocalDate d = r.getDateCreation();
                    if (!d.isBefore(now.minusMonths(6))) {
                        String key = d.getMonth().getDisplayName(TextStyle.SHORT, Locale.FRENCH)
                                     + " " + d.getYear();
                        byMonth.merge(key, 1L, Long::sum);
                    }
                }
            }

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Audits créés");
            byMonth.forEach((k, v) -> series.getData().add(new XYChart.Data<>(k, v)));
            chartTendance.getData().add(series);

            // Style points
            Platform.runLater(() -> {
                for (XYChart.Data<String, Number> d : series.getData()) {
                    Node node = d.getNode();
                    if (node != null) {
                        node.setStyle("-fx-background-color: #2563eb, white; -fx-background-insets: 0, 2; -fx-background-radius: 5px; -fx-padding: 5px;");
                    }
                }
            });
        } catch (Throwable e) { System.err.println("lineChart: " + e.getMessage()); }
    }

    /** Pie chart: report statuses */
    private void buildStatusPie(long draft, long inProgress, long finalised) {
        try {
            if (chartStatus == null) return;
            chartStatus.getData().clear();
            if (draft + inProgress + finalised == 0) {
                chartStatus.getData().add(new PieChart.Data("Aucun rapport", 1));
                return;
            }
            if (draft      > 0) chartStatus.getData().add(new PieChart.Data("Brouillon ("   + draft      + ")", draft));
            if (inProgress > 0) chartStatus.getData().add(new PieChart.Data("En cours ("    + inProgress + ")", inProgress));
            if (finalised  > 0) chartStatus.getData().add(new PieChart.Data("Finalisé ("    + finalised  + ")", finalised));

            Platform.runLater(() -> {
                String[] colors = {"#f59e0b", "#3b82f6", "#10b981"};
                List<PieChart.Data> data = chartStatus.getData();
                for (int i = 0; i < data.size(); i++) {
                    Node node = data.get(i).getNode();
                    if (node != null) node.setStyle("-fx-pie-color: " + colors[i % colors.length] + ";");
                }
            });
        } catch (Throwable e) { System.err.println("statusPie: " + e.getMessage()); }
    }

    /** Bar chart: recommendations by priority */
    private void buildPriorityBar(long haute, long moyenne, long faible) {
        try {
            if (chartPriorities == null) return;
            chartPriorities.getData().clear();
            XYChart.Series<String, Number> s = new XYChart.Series<>();
            s.setName("Recommandations");
            s.getData().add(new XYChart.Data<>("🔴 Haute",   haute));
            s.getData().add(new XYChart.Data<>("🟡 Moyenne", moyenne));
            s.getData().add(new XYChart.Data<>("🟢 Faible",  faible));
            chartPriorities.getData().add(s);

            Platform.runLater(() -> {
                String[] colors = {"#ef4444", "#f59e0b", "#10b981"};
                List<XYChart.Data<String, Number>> data = s.getData();
                for (int i = 0; i < data.size(); i++) {
                    Node node = data.get(i).getNode();
                    if (node != null) node.setStyle("-fx-bar-fill: " + colors[i] + ";");
                }
            });
        } catch (Throwable e) { System.err.println("barChart: " + e.getMessage()); }
    }

    /** Pie chart: users by role */
    private void buildRolePie(List<User> users) {
        try {
            if (chartUserRoles == null) return;
            chartUserRoles.getData().clear();
            if (users.isEmpty()) {
                chartUserRoles.getData().add(new PieChart.Data("Aucun utilisateur", 1));
                return;
            }
            Map<String, Long> byRole = users.stream()
                .filter(u -> u.getRole() != null)
                .collect(Collectors.groupingBy(u -> u.getRole().getNom(), Collectors.counting()));

            if (byRole.isEmpty()) {
                chartUserRoles.getData().add(new PieChart.Data("Sans rôle", users.size()));
            } else {
                byRole.forEach((role, count) ->
                    chartUserRoles.getData().add(new PieChart.Data(role + " (" + count + ")", count)));
            }
        } catch (Throwable e) { System.err.println("rolePie: " + e.getMessage()); }
    }

    /** Recent activity feed */
    private void buildRecentActivity(List<RapportAudit> reports) {
        try {
            Label[] labels = {lblRecent1, lblRecent2, lblRecent3, lblRecent4, lblRecent5};
            List<RapportAudit> sorted = reports.stream()
                .filter(r -> r.getDateCreation() != null)
                .sorted(Comparator.comparing(RapportAudit::getDateCreation).reversed())
                .limit(5)
                .collect(Collectors.toList());

            for (int i = 0; i < labels.length; i++) {
                if (labels[i] == null) continue;
                if (i < sorted.size()) {
                    RapportAudit r = sorted.get(i);
                    String icon = r.getStatut() == StatutRapport.FINALISE ? "✅" :
                                  r.getStatut() == StatutRapport.EN_COURS ? "🔄" : "📝";
                    labels[i].setText(icon + " " + r.getTitre() + "  —  " + r.getAuditeur()
                                      + "  ·  " + r.getDateCreation());
                } else {
                    labels[i].setText("");
                }
            }
        } catch (Throwable e) { System.err.println("activity: " + e.getMessage()); }
    }

    // ── HELPERS ──────────────────────────────────────────────────────
    private void set(Label lbl, String txt) {
        if (lbl != null) lbl.setText(txt);
    }

    // ── NAVIGATION ───────────────────────────────────────────────────
    @FXML private void goToUserManagement()      { if (dashboardController != null) dashboardController.showUserManagement(); }
    @FXML private void goToRolePermission()      { if (dashboardController != null) dashboardController.showRolePermission(); }
    @FXML private void goToEntrepriseManagement(){ if (dashboardController != null) dashboardController.showEntrepriseManagement(); }
}
