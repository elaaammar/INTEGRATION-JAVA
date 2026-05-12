package com.example.mindjavafx.integration.dto;

/**
 * DTO pour les Documents d'entreprise échangés avec Symfony.
 */
public class DocumentDTO {
    private int id;
    private String nom;
    private String type;
    private String path;
    private int entrepriseId;

    public DocumentDTO() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public int getEntrepriseId() { return entrepriseId; }
    public void setEntrepriseId(int entrepriseId) { this.entrepriseId = entrepriseId; }
}
