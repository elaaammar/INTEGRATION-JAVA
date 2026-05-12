package com.gestion_audit.services;

import com.gestion_audit.models.ReponseQuestion;
import com.gestion_audit.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReponseQuestionService implements IService<ReponseQuestion> {
    Connection conn;

    public ReponseQuestionService() {
        this.conn = DBConnection.getInstance().getConn();
    }

    @Override
    public void add(ReponseQuestion reponse) {
        String SQL = "INSERT INTO reponse_question (question_id, rapport_id, reponse) VALUES (?, ?, ?)";
        try {
            PreparedStatement pstmt = conn.prepareStatement(SQL);
            pstmt.setInt(1, reponse.getQuestionId());
            pstmt.setInt(2, reponse.getRapportId());
            pstmt.setString(3, reponse.getReponse());
            pstmt.executeUpdate();
            System.out.println("Answer saved!");
        } catch (SQLException e) {
            System.out.println("Error saving answer: " + e.getMessage());
        }
    }

    @Override
    public void update(ReponseQuestion reponse) {
        String SQL = "UPDATE reponse_question SET reponse = ? WHERE question_id = ? AND rapport_id = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(SQL);
            pstmt.setString(1, reponse.getReponse());
            pstmt.setInt(2, reponse.getQuestionId());
            pstmt.setInt(3, reponse.getRapportId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating answer: " + e.getMessage());
        }
    }

    @Override
    public void delete(ReponseQuestion reponse) {
        String SQL = "DELETE FROM reponse_question WHERE id = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(SQL);
            pstmt.setInt(1, reponse.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<ReponseQuestion> getAll() {
        String SQL = "SELECT * FROM reponse_question";
        List<ReponseQuestion> list = new ArrayList<>();
        try {
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery(SQL);
            while (rs.next()) {
                ReponseQuestion r = new ReponseQuestion(
                        rs.getInt("id"),
                        rs.getInt("question_id"),
                        rs.getInt("rapport_id"),
                        rs.getString("reponse"),
                        rs.getString("date_reponse")
                );
                list.add(r);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public ReponseQuestion getAnswerForQuestionAndReport(int questionId, int rapportId) {
        String SQL = "SELECT * FROM reponse_question WHERE question_id = ? AND rapport_id = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(SQL);
            pstmt.setInt(1, questionId);
            pstmt.setInt(2, rapportId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new ReponseQuestion(
                        rs.getInt("id"),
                        rs.getInt("question_id"),
                        rs.getInt("rapport_id"),
                        rs.getString("reponse"),
                        rs.getString("date_reponse")
                );
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}

