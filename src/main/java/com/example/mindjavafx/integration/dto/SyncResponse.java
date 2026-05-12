package com.example.mindjavafx.integration.dto;

import java.util.List;

/**
 * Réponse générique de l'API Symfony.
 * Le champ "data" est un objet JSON flexible (peut être un objet, une liste, etc.)
 */
public class SyncResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private int statusCode;
    private List<String> errors;

    public SyncResponse() {}

    public SyncResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public SyncResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // ── Getters & Setters ──────────────────────────────────────────
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public int getStatusCode() { return statusCode; }
    public void setStatusCode(int statusCode) { this.statusCode = statusCode; }

    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }

    @Override
    public String toString() {
        return "SyncResponse{success=" + success + ", message='" + message + "', statusCode=" + statusCode + "}";
    }
}
