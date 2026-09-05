package com.sunrisedental.dao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sunrisedental.model.Appointment;
import com.sunrisedental.model.NamedMetric;
import com.sunrisedental.util.DBConnection;

public class DashboardDAO {

    private static final DateTimeFormatter DAY_LABEL = DateTimeFormatter.ofPattern("EEE d");

    private static final String APT_JOIN = """
            SELECT a.appointment_id, a.appointment_number, a.patient_id,
                   p.nic, p.patient_name, p.address, p.contact_number,
                   a.dentist_id, d.dentist_name, a.treatment_id, t.treatment_name, t.cost,
                   a.appointment_date, a.appointment_time, a.status
            FROM appointments a
            JOIN patients p ON a.patient_id = p.patient_id
            JOIN dentists d ON a.dentist_id = d.dentist_id
            JOIN treatments t ON a.treatment_id = t.treatment_id
            """;

    public int countToday() throws SQLException {
        return scalarInt("SELECT COUNT(*) FROM appointments WHERE appointment_date = CURDATE()");
    }

    public int countBookedToday() throws SQLException {
        return scalarInt("""
                SELECT COUNT(*) FROM appointments
                WHERE appointment_date = CURDATE() AND status = 'BOOKED'
                """);
    }

    public int countCompletedToday() throws SQLException {
        return scalarInt("""
                SELECT COUNT(*) FROM appointments
                WHERE appointment_date = CURDATE() AND status = 'COMPLETED'
                """);
    }

    public int countCancelledThisMonth() throws SQLException {
        return scalarInt("""
                SELECT COUNT(*) FROM appointments
                WHERE status = 'CANCELLED'
                  AND YEAR(appointment_date) = YEAR(CURDATE())
                  AND MONTH(appointment_date) = MONTH(CURDATE())
                """);
    }

    public int countActivePatients() throws SQLException {
        return scalarInt("SELECT COUNT(*) FROM patients WHERE is_active = 1");
    }

    public BigDecimal revenueToday() throws SQLException {
        return scalarDecimal("""
                SELECT COALESCE(SUM(total_amount), 0) FROM bills
                WHERE DATE(billed_at) = CURDATE()
                """);
    }

    public BigDecimal revenueThisMonth() throws SQLException {
        return scalarDecimal("""
                SELECT COALESCE(SUM(total_amount), 0) FROM bills
                WHERE YEAR(billed_at) = YEAR(CURDATE())
                  AND MONTH(billed_at) = MONTH(CURDATE())
                """);
    }

    public BigDecimal avgBillThisMonth() throws SQLException {
        return scalarDecimal("""
                SELECT COALESCE(AVG(total_amount), 0) FROM bills
                WHERE YEAR(billed_at) = YEAR(CURDATE())
                  AND MONTH(billed_at) = MONTH(CURDATE())
                """).setScale(2, RoundingMode.HALF_UP);
    }

    /** Appointments per calendar day for the last 7 days (oldest → newest). */
    public List<NamedMetric> appointmentsLast7Days() throws SQLException {
        Map<LocalDate, Long> byDay = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            byDay.put(today.minusDays(i), 0L);
        }
        String sql = """
                SELECT appointment_date AS d, COUNT(*) AS c
                FROM appointments
                WHERE appointment_date BETWEEN DATE_SUB(CURDATE(), INTERVAL 6 DAY) AND CURDATE()
                GROUP BY appointment_date
                """;
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                LocalDate d = rs.getDate("d").toLocalDate();
                byDay.put(d, rs.getLong("c"));
            }
        }
        List<NamedMetric> list = new ArrayList<>();
        for (Map.Entry<LocalDate, Long> e : byDay.entrySet()) {
            list.add(new NamedMetric(e.getKey().format(DAY_LABEL), e.getValue()));
        }
        return list;
    }

    /** Revenue per calendar day for the last 7 days (oldest → newest). */
    public List<NamedMetric> revenueLast7Days() throws SQLException {
        Map<LocalDate, BigDecimal> byDay = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            byDay.put(today.minusDays(i), BigDecimal.ZERO);
        }
        String sql = """
                SELECT DATE(billed_at) AS d, COALESCE(SUM(total_amount), 0) AS amt
                FROM bills
                WHERE DATE(billed_at) BETWEEN DATE_SUB(CURDATE(), INTERVAL 6 DAY) AND CURDATE()
                GROUP BY DATE(billed_at)
                """;
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                LocalDate d = rs.getDate("d").toLocalDate();
                byDay.put(d, rs.getBigDecimal("amt"));
            }
        }
        List<NamedMetric> list = new ArrayList<>();
        for (Map.Entry<LocalDate, BigDecimal> e : byDay.entrySet()) {
            list.add(new NamedMetric(e.getKey().format(DAY_LABEL), 0, e.getValue()));
        }
        return list;
    }

    public List<NamedMetric> statusMixThisMonth() throws SQLException {
        List<NamedMetric> list = new ArrayList<>();
        String sql = """
                SELECT status, COUNT(*) AS c
                FROM appointments
                WHERE YEAR(appointment_date) = YEAR(CURDATE())
                  AND MONTH(appointment_date) = MONTH(CURDATE())
                GROUP BY status
                ORDER BY FIELD(status, 'BOOKED', 'COMPLETED', 'CANCELLED'), status
                """;
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new NamedMetric(rs.getString("status"), rs.getLong("c")));
            }
        }
        return list;
    }

    public List<NamedMetric> revenueByTreatmentThisMonth() throws SQLException {
        List<NamedMetric> list = new ArrayList<>();
        String sql = """
                SELECT t.treatment_name AS label,
                       COUNT(*) AS c,
                       COALESCE(SUM(b.total_amount), 0) AS amt
                FROM bills b
                JOIN appointments a ON b.appointment_id = a.appointment_id
                JOIN treatments t ON a.treatment_id = t.treatment_id
                WHERE YEAR(b.billed_at) = YEAR(CURDATE())
                  AND MONTH(b.billed_at) = MONTH(CURDATE())
                GROUP BY t.treatment_id, t.treatment_name
                ORDER BY amt DESC
                """;
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new NamedMetric(
                        rs.getString("label"),
                        rs.getLong("c"),
                        rs.getBigDecimal("amt")));
            }
        }
        return list;
    }

    public List<Appointment> findTodaySchedule() throws SQLException {
        List<Appointment> list = new ArrayList<>();
        String sql = APT_JOIN
                + " WHERE a.appointment_date = CURDATE() ORDER BY a.appointment_time";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapAppointment(rs));
            }
        }
        return list;
    }

    public List<Appointment> findUpcomingBooked(int limit) throws SQLException {
        List<Appointment> list = new ArrayList<>();
        String sql = APT_JOIN
                + """
                         WHERE a.appointment_date >= CURDATE()
                           AND a.status = 'BOOKED'
                         ORDER BY a.appointment_date, a.appointment_time
                         LIMIT ?
                        """;
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapAppointment(rs));
                }
            }
        }
        return list;
    }

    public List<NamedMetric> dentistWorkloadThisMonth() throws SQLException {
        List<NamedMetric> list = new ArrayList<>();
        String sql = """
                SELECT d.dentist_name AS label,
                       SUM(CASE WHEN a.status = 'BOOKED' THEN 1 ELSE 0 END) AS booked,
                       SUM(CASE WHEN a.status = 'COMPLETED' THEN 1 ELSE 0 END) AS completed,
                       SUM(CASE WHEN a.status = 'CANCELLED' THEN 1 ELSE 0 END) AS cancelled,
                       COUNT(*) AS total
                FROM dentists d
                LEFT JOIN appointments a ON d.dentist_id = a.dentist_id
                  AND YEAR(a.appointment_date) = YEAR(CURDATE())
                  AND MONTH(a.appointment_date) = MONTH(CURDATE())
                WHERE d.is_active = 1
                GROUP BY d.dentist_id, d.dentist_name
                ORDER BY total DESC, d.dentist_name
                """;
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                NamedMetric m = new NamedMetric(rs.getString("label"), rs.getLong("total"));
                m.setBooked(rs.getLong("booked"));
                m.setCompleted(rs.getLong("completed"));
                m.setCancelled(rs.getLong("cancelled"));
                list.add(m);
            }
        }
        return list;
    }

    private int scalarInt(String sql) throws SQLException {
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private BigDecimal scalarDecimal(String sql) throws SQLException {
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            rs.next();
            BigDecimal v = rs.getBigDecimal(1);
            return v != null ? v : BigDecimal.ZERO;
        }
    }

    private Appointment mapAppointment(ResultSet rs) throws SQLException {
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
