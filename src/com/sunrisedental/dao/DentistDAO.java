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

    private static final String SELECT_COLS =
            "dentist_id, dentist_name, specialization, mobile_number, email, is_active";

    public List<Dentist> findAll() throws SQLException {
        List<Dentist> list = new ArrayList<>();
        String sql = "SELECT " + SELECT_COLS + " FROM dentists ORDER BY dentist_name";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public List<Dentist> findAllActive() throws SQLException {
        List<Dentist> list = new ArrayList<>();
        String sql = "SELECT " + SELECT_COLS + " FROM dentists WHERE is_active = 1 ORDER BY dentist_name";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public Dentist findById(int dentistId) throws SQLException {
        String sql = "SELECT " + SELECT_COLS + " FROM dentists WHERE dentist_id = ?";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, dentistId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public void create(Dentist dentist) throws SQLException {
        String sql = "INSERT INTO dentists (dentist_name, specialization, mobile_number, email, is_active) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dentist.getDentistName());
            ps.setString(2, dentist.getSpecialization());
            ps.setString(3, dentist.getMobileNumber());
            ps.setString(4, dentist.getEmail());
            ps.setInt(5, dentist.isActive() ? 1 : 0);
            ps.executeUpdate();
        }
    }

    public void update(Dentist dentist) throws SQLException {
        String sql = "UPDATE dentists SET dentist_name = ?, specialization = ?, mobile_number = ?, email = ?, "
                + "is_active = ? WHERE dentist_id = ?";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dentist.getDentistName());
            ps.setString(2, dentist.getSpecialization());
            ps.setString(3, dentist.getMobileNumber());
            ps.setString(4, dentist.getEmail());
            ps.setInt(5, dentist.isActive() ? 1 : 0);
            ps.setInt(6, dentist.getDentistId());
            ps.executeUpdate();
        }
    }

    private static Dentist map(ResultSet rs) throws SQLException {
        Dentist d = new Dentist();
        d.setDentistId(rs.getInt("dentist_id"));
        d.setDentistName(rs.getString("dentist_name"));
        d.setSpecialization(rs.getString("specialization"));
        d.setMobileNumber(rs.getString("mobile_number"));
        d.setEmail(rs.getString("email"));
        d.setActive(rs.getInt("is_active") == 1);
        return d;
    }
}
