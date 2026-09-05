package com.sunrisedental.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.sunrisedental.model.Treatment;
import com.sunrisedental.util.DBConnection;

public class TreatmentDAO {

    private static final String SELECT_COLS = "treatment_id, treatment_name, cost";

    public List<Treatment> findAll() throws SQLException {
        List<Treatment> list = new ArrayList<>();
        String sql = "SELECT " + SELECT_COLS + " FROM treatments ORDER BY treatment_name";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public Treatment findById(int treatmentId) throws SQLException {
        String sql = "SELECT " + SELECT_COLS + " FROM treatments WHERE treatment_id = ?";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, treatmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public void create(Treatment treatment) throws SQLException {
        String sql = "INSERT INTO treatments (treatment_name, cost) VALUES (?, ?)";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, treatment.getTreatmentName());
            ps.setBigDecimal(2, treatment.getCost());
            ps.executeUpdate();
        }
    }

    public void update(Treatment treatment) throws SQLException {
        String sql = "UPDATE treatments SET treatment_name = ?, cost = ? WHERE treatment_id = ?";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, treatment.getTreatmentName());
            ps.setBigDecimal(2, treatment.getCost());
            ps.setInt(3, treatment.getTreatmentId());
            ps.executeUpdate();
        }
    }

    private static Treatment map(ResultSet rs) throws SQLException {
        Treatment t = new Treatment();
        t.setTreatmentId(rs.getInt("treatment_id"));
        t.setTreatmentName(rs.getString("treatment_name"));
        t.setCost(rs.getBigDecimal("cost"));
        return t;
    }
}
