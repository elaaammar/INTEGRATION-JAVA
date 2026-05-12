package com.example.mindjavafx.integration.dto;

/**
 * DTO pour les données Audit échangées avec Symfony.
 * Mapping JSON ↔ Java pour la synchronisation CRUD.
 */
public class AuditDTO {
    private int id;
    private int userId;
    private String name;
    private String category;
    private int globalScore;
    private int securityScore;
    private int complianceScore;
    private int performanceScore;
    private String findings;
    private String status;
    private String auditDate;
    private String createdAt;
    private String updatedAt;

    public AuditDTO() {}

    // ── Getters & Setters ──────────────────────────────────────────
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getGlobalScore() { return globalScore; }
    public void setGlobalScore(int globalScore) { this.globalScore = globalScore; }

    public int getSecurityScore() { return securityScore; }
    public void setSecurityScore(int securityScore) { this.securityScore = securityScore; }

    public int getComplianceScore() { return complianceScore; }
    public void setComplianceScore(int complianceScore) { this.complianceScore = complianceScore; }

    public int getPerformanceScore() { return performanceScore; }
    public void setPerformanceScore(int performanceScore) { this.performanceScore = performanceScore; }

    public String getFindings() { return findings; }
    public void setFindings(String findings) { this.findings = findings; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAuditDate() { return auditDate; }
    public void setAuditDate(String auditDate) { this.auditDate = auditDate; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
