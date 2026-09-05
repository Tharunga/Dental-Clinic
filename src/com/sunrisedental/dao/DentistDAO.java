package com.sunrisedental.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.sunrisedental.model.Dentist;
import com.sunrisedental.util.DBConnection;

public class DentistDAO {

    public List<Dentist> findAll() throws SQLException {
        List<Dentist> list = new ArrayList<>();
        String sql = "SELECT dentist_id, dentist_name, specialization FROM dentists ORDER BY dentist_name";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Dentist d = new Dentist();
                d.setDentistId(rs.getInt("dentist_id"));
                d.setDentistName(rs.getString("dentist_name"));
                d.setSpecialization(rs.getString("specialization"));
                list.add(d);
            }
        }
        return list;
    }
}
