package com.example.mindjavafx.integration.dto;

import java.util.List;

/**
 * DTO pour les Rapports d'Audit IA (module auditaifx) échangés avec Symfony.
 */
public class RapportAuditDTO {
    private String id;
    private String titre;
    private String auditeur;
    private String entiteAuditee;
    private String dateCreation;
    private String dateMiseAJour;
    private String statut;
    private String description;
    private String scoreAudit;
    private List<RecommandationItemDTO> recommandations;
    private List<RisqueItemDTO> risques;

    public RapportAuditDTO() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getAuditeur() { return auditeur; }
    public void setAuditeur(String auditeur) { this.auditeur = auditeur; }

    public String getEntiteAuditee() { return entiteAuditee; }
    public void setEntiteAuditee(String entiteAuditee) { this.entiteAuditee = entiteAuditee; }

    public String getDateCreation() { return dateCreation; }
    public void setDateCreation(String dateCreation) { this.dateCreation = dateCreation; }

    public String getDateMiseAJour() { return dateMiseAJour; }
    public void setDateMiseAJour(String dateMiseAJour) { this.dateMiseAJour = dateMiseAJour; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getScoreAudit() { return scoreAudit; }
    public void setScoreAudit(String scoreAudit) { this.scoreAudit = scoreAudit; }

    public List<RecommandationItemDTO> getRecommandations() { return recommandations; }
    public void setRecommandations(List<RecommandationItemDTO> recommandations) { this.recommandations = recommandations; }

    public List<RisqueItemDTO> getRisques() { return risques; }
    public void setRisques(List<RisqueItemDTO> risques) { this.risques = risques; }

    // ── Nested DTOs ────────────────────────────────────────────────

    public static class RecommandationItemDTO {
        private String id;
        private String description;
        private String priorite;
        private boolean resolue;

        public RecommandationItemDTO() {}

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getPriorite() { return priorite; }
        public void setPriorite(String priorite) { this.priorite = priorite; }
        public boolean isResolue() { return resolue; }
        public void setResolue(boolean resolue) { this.resolue = resolue; }
    }

    public static class RisqueItemDTO {
        private String id;
        private String description;
        private String niveau;
        private String impact;

        public RisqueItemDTO() {}

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getNiveau() { return niveau; }
        public void setNiveau(String niveau) { this.niveau = niveau; }
        public String getImpact() { return impact; }
        public void setImpact(String impact) { this.impact = impact; }
    }
}
