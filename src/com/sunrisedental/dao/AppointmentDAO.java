package com.sunrisedental.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.sunrisedental.model.Appointment;
import com.sunrisedental.util.DBConnection;

public class AppointmentDAO {

    private static final String SELECT_JOIN = """
            SELECT a.appointment_id, a.appointment_number, a.patient_id,
                   p.nic, p.patient_name, p.address, p.contact_number,
                   a.dentist_id, d.dentist_name, a.treatment_id, t.treatment_name, t.cost,
                   a.appointment_date, a.appointment_time, a.status
            FROM appointments a
            JOIN patients p ON a.patient_id = p.patient_id
            JOIN dentists d ON a.dentist_id = d.dentist_id
            JOIN treatments t ON a.treatment_id = t.treatment_id
            """;

    public String insert(Appointment appointment) throws SQLException {
        String number = nextAppointmentNumber();
        String sql = """
                INSERT INTO appointments
                (appointment_number, patient_id, dentist_id, treatment_id,
                 appointment_date, appointment_time, status)
                VALUES (?, ?, ?, ?, ?, ?, 'BOOKED')
                """;
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, number);
            ps.setInt(2, appointment.getPatientId());
            ps.setInt(3, appointment.getDentistId());
            ps.setInt(4, appointment.getTreatmentId());
            ps.setDate(5, Date.valueOf(appointment.getAppointmentDate()));
            ps.setTime(6, Time.valueOf(appointment.getAppointmentTimeValue()));
            ps.executeUpdate();
            return number;
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new SQLException("DOUBLE_BOOKING", e);
        }
    }

    public Appointment findByNumber(String appointmentNumber) throws SQLException {
        String sql = SELECT_JOIN + " WHERE a.appointment_number = ?";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, appointmentNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public List<Appointment> findUpcoming(int limit) throws SQLException {
        List<Appointment> list = new ArrayList<>();
        String sql = SELECT_JOIN
                + " ORDER BY a.appointment_date, a.appointment_time LIMIT ?";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    public int countAll() throws SQLException {
        return scalarCount("SELECT COUNT(*) FROM appointments");
    }

    public int countToday() throws SQLException {
        return scalarCount("SELECT COUNT(*) FROM appointments WHERE appointment_date = CURDATE()");
    }

    public boolean slotTaken(int dentistId, LocalDate date, LocalTime time) throws SQLException {
        String sql = """
                SELECT 1 FROM appointments
                WHERE dentist_id = ? AND appointment_date = ? AND appointment_time = ?
                """;
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, dentistId);
            ps.setDate(2, Date.valueOf(date));
            ps.setTime(3, Time.valueOf(time));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private int scalarCount(String sql) throws SQLException {
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private String nextAppointmentNumber() throws SQLException {
        String prefix = "APT" + LocalDate.now().getYear();
        String sql = "SELECT appointment_number FROM appointments WHERE appointment_number LIKE ? ORDER BY appointment_number DESC LIMIT 1";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, prefix + "%");
            try (ResultSet rs = ps.executeQuery()) {
                int next = 1;
                if (rs.next()) {
                    String last = rs.getString(1);
                    next = Integer.parseInt(last.substring(prefix.length())) + 1;
                }
                return prefix + String.format("%04d", next);
            }
        }
    }

    private Appointment map(ResultSet rs) throws SQLException {
        Appointment a = new Appointment();
        a.setAppointmentId(rs.getInt("appointment_id"));
        a.setAppointmentNumber(rs.getString("appointment_number"));
        a.setPatientId(rs.getInt("patient_id"));
        a.setNic(rs.getString("nic"));
        a.setPatientName(rs.getString("patient_name"));
        a.setAddress(rs.getString("address"));
        a.setContactNumber(rs.getString("contact_number"));
        a.setDentistId(rs.getInt("dentist_id"));
        a.setDentistName(rs.getString("dentist_name"));
        a.setTreatmentId(rs.getInt("treatment_id"));
        a.setTreatmentName(rs.getString("treatment_name"));
        a.setTreatmentCost(rs.getBigDecimal("cost"));
        a.setAppointmentDate(rs.getDate("appointment_date").toLocalDate());
        a.setAppointmentTime(rs.getTime("appointment_time").toLocalTime());
        a.setStatus(rs.getString("status"));
        return a;
    }
}
