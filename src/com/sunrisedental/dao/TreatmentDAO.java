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

    public List<Treatment> findAll() throws SQLException {
        List<Treatment> list = new ArrayList<>();
        String sql = "SELECT treatment_id, treatment_name, cost FROM treatments ORDER BY treatment_name";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Treatment t = new Treatment();
                t.setTreatmentId(rs.getInt("treatment_id"));
                t.setTreatmentName(rs.getString("treatment_name"));
                t.setCost(rs.getBigDecimal("cost"));
                list.add(t);
            }
        }
        return list;
    }
}
