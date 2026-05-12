package com.gestion_audit.services;

import com.gestion_audit.models.Rapport;
import com.gestion_audit.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RapportService implements IService<Rapport> {
    Connection conn;

    private QuestionAuditService questionService;

    public RapportService() {
        this.conn = DBConnection.getInstance().getConn();
    }

    private void initQuestionService() {
        if (questionService == null) {
            questionService = new QuestionAuditService();
        }
    }

    @Override
    public void add(Rapport rapport) {
        String SQL = "INSERT INTO rapport (title, description, date, type, duration) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement pstmt = conn.prepareStatement(SQL);
            pstmt.setString(1, rapport.getTitle());
            pstmt.setString(2, rapport.getDescription());
            pstmt.setString(3, rapport.getDate());
            pstmt.setString(4, rapport.getType());
            pstmt.setInt(5, rapport.getDuration());
            pstmt.executeUpdate();
            System.out.println("Rapport added successfully!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update(Rapport rapport) {
        String SQL = "UPDATE rapport SET title = ?, description = ?, date = ?, type = ?, duration = ? WHERE id = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(SQL);
            pstmt.setString(1, rapport.getTitle());
            pstmt.setString(2, rapport.getDescription());
            pstmt.setString(3, rapport.getDate());
            pstmt.setString(4, rapport.getType());
            pstmt.setInt(5, rapport.getDuration());
            pstmt.setInt(6, rapport.getId());
            pstmt.executeUpdate();
            System.out.println("Rapport updated successfully!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(Rapport rapport) {
        String SQL = "DELETE FROM rapport WHERE id = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(SQL);
            pstmt.setInt(1, rapport.getId());
            pstmt.executeUpdate();
            System.out.println("Rapport deleted successfully!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Rapport> getAll() {
        String SQL = "SELECT * FROM rapport";
        List<Rapport> rapports = new ArrayList<>();
        try {
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery(SQL);
            while (rs.next()) {
                Rapport r = new Rapport();
                r.setId(rs.getInt("id"));
                r.setTitle(rs.getString("title"));
                r.setDescription(rs.getString("description"));
                r.setDate(rs.getString("date"));
                r.setType(rs.getString("type"));
                r.setDuration(rs.getInt("duration"));
                
                // Fetch associated questions by type
                initQuestionService();
                r.setQuestions(questionService.getByType(r.getType()));
                
                rapports.add(r);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return rapports;
    }
}

