package com.example.mindjavafx.integration.dto;

/**
 * DTO pour les Réponses aux Réclamations échangées avec Symfony.
 */
public class ReponseReclamationDTO {
    private int id;
    private String contenu;
    private String dateCreation;
    private int reclamationId;
    private String auteurType;
    private String avisUtilisateur;
    private String nom;

    public ReponseReclamationDTO() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public String getDateCreation() { return dateCreation; }
    public void setDateCreation(String dateCreation) { this.dateCreation = dateCreation; }

    public int getReclamationId() { return reclamationId; }
    public void setReclamationId(int reclamationId) { this.reclamationId = reclamationId; }

    public String getAuteurType() { return auteurType; }
    public void setAuteurType(String auteurType) { this.auteurType = auteurType; }

    public String getAvisUtilisateur() { return avisUtilisateur; }
    public void setAvisUtilisateur(String avisUtilisateur) { this.avisUtilisateur = avisUtilisateur; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
}
