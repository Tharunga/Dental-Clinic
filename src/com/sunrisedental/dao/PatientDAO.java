package com.sunrisedental.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.sunrisedental.model.Patient;
import com.sunrisedental.util.DBConnection;

public class PatientDAO {

    private static final String SELECT_COLS =
            "patient_id, nic, patient_name, address, contact_number, is_active, created_at, updated_at";

    public Patient findByNic(String nic) throws SQLException {
        String sql = "SELECT " + SELECT_COLS + " FROM patients WHERE nic = ?";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nic);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public Patient findById(int patientId) throws SQLException {
        String sql = "SELECT " + SELECT_COLS + " FROM patients WHERE patient_id = ?";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public List<Patient> findAll() throws SQLException {
        List<Patient> list = new ArrayList<>();
        String sql = """
                SELECT p.patient_id, p.nic, p.patient_name, p.address, p.contact_number, p.is_active,
                       p.created_at, p.updated_at, MAX(a.appointment_date) AS last_visit_date
                FROM patients p
                LEFT JOIN appointments a ON a.patient_id = p.patient_id
                GROUP BY p.patient_id, p.nic, p.patient_name, p.address, p.contact_number, p.is_active,
                         p.created_at, p.updated_at
                ORDER BY p.patient_name
                """;
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapWithLastVisit(rs));
            }
        }
        return list;
    }

    public List<Patient> search(String q) throws SQLException {
        String trimmed = q == null ? "" : q.trim();
        if (trimmed.isEmpty()) {
            return findAll();
        }
        String nicNorm = trimmed.replaceAll("[\\s\\-]", "").toUpperCase();
        String nameLike = "%" + trimmed + "%";
        List<Patient> list = new ArrayList<>();
        String sql = """
                SELECT p.patient_id, p.nic, p.patient_name, p.address, p.contact_number, p.is_active,
                       p.created_at, p.updated_at, MAX(a.appointment_date) AS last_visit_date
                FROM patients p
                LEFT JOIN appointments a ON a.patient_id = p.patient_id
                WHERE p.nic LIKE ? OR p.patient_name LIKE ?
                GROUP BY p.patient_id, p.nic, p.patient_name, p.address, p.contact_number, p.is_active,
                         p.created_at, p.updated_at
                ORDER BY p.patient_name
                """;
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nicNorm + "%");
            ps.setString(2, nameLike);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapWithLastVisit(rs));
                }
            }
        }
        return list;
    }

    public LocalDate findLastVisitDate(int patientId) throws SQLException {
        String sql = "SELECT MAX(appointment_date) FROM appointments WHERE patient_id = ?";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Date d = rs.getDate(1);
                    return d == null ? null : d.toLocalDate();
                }
            }
        }
        return null;
    }

    public int create(Patient patient) throws SQLException {
        String sql = """
                INSERT INTO patients (nic, patient_name, address, contact_number, is_active)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, patient.getNic());
            ps.setString(2, patient.getPatientName());
            ps.setString(3, patient.getAddress());
            ps.setString(4, patient.getContactNumber());
            ps.setInt(5, patient.isActive() ? 1 : 0);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    patient.setPatientId(id);
                    return id;
                }
            }
        }
        throw new SQLException("Could not create patient.");
    }

    public void update(Patient patient) throws SQLException {
        String sql = """
                UPDATE patients
                SET patient_name = ?, address = ?, contact_number = ?, is_active = ?
                WHERE patient_id = ?
                """;
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, patient.getPatientName());
            ps.setString(2, patient.getAddress());
            ps.setString(3, patient.getContactNumber());
            ps.setInt(4, patient.isActive() ? 1 : 0);
            ps.setInt(5, patient.getPatientId());
            ps.executeUpdate();
        }
    }

    private static Patient map(ResultSet rs) throws SQLException {
        Patient p = new Patient();
        p.setPatientId(rs.getInt("patient_id"));
        p.setNic(rs.getString("nic"));
        p.setPatientName(rs.getString("patient_name"));
        p.setAddress(rs.getString("address"));
        p.setContactNumber(rs.getString("contact_number"));
        p.setActive(rs.getInt("is_active") == 1);
        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) {
            p.setCreatedAt(created.toLocalDateTime());
        }
        Timestamp updated = rs.getTimestamp("updated_at");
        if (updated != null) {
            p.setUpdatedAt(updated.toLocalDateTime());
        }
        return p;
    }

    private static Patient mapWithLastVisit(ResultSet rs) throws SQLException {
        Patient p = map(rs);
        Date last = rs.getDate("last_visit_date");
        if (last != null) {
            p.setLastVisitDate(last.toLocalDate());
        }
        return p;
    }
}
