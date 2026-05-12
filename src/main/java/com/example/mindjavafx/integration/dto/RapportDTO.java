package com.example.mindjavafx.integration.dto;

import java.util.List;

/**
 * DTO pour les Rapports (questionnaires d'audit) échangés avec Symfony.
 */
public class RapportDTO {
    private int id;
    private String title;
    private String description;
    private String date;
    private String type;
    private int duration;
    private List<QuestionDTO> questions;

    public RapportDTO() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public List<QuestionDTO> getQuestions() { return questions; }
    public void setQuestions(List<QuestionDTO> questions) { this.questions = questions; }

    /**
     * DTO interne pour les questions d'audit.
     */
    public static class QuestionDTO {
        private int id;
        private String content;
        private String type;
        private String bonneReponse;
        private int timeLimit;

        public QuestionDTO() {}

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getBonneReponse() { return bonneReponse; }
        public void setBonneReponse(String bonneReponse) { this.bonneReponse = bonneReponse; }

        public int getTimeLimit() { return timeLimit; }
        public void setTimeLimit(int timeLimit) { this.timeLimit = timeLimit; }
    }
}
