package com.sunrisedental.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.sunrisedental.model.Appointment;
import com.sunrisedental.model.Bill;
import com.sunrisedental.util.ClinicConfig;
import com.sunrisedental.util.DBConnection;

public class BillDAO {

    public Bill findByAppointmentId(int appointmentId) throws SQLException {
        String sql = """
                SELECT bill_id, bill_number, appointment_id, consultation_fee, treatment_cost, total_amount, billed_at
                FROM bills WHERE appointment_id = ?
                """;
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, appointmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public Bill createFor(Appointment appointment) throws SQLException {
        Bill existing = findByAppointmentId(appointment.getAppointmentId());
        if (existing != null) {
            existing.setAppointment(appointment);
            return existing;
        }

        BigDecimal consult = ClinicConfig.CONSULTATION_FEE;
        BigDecimal treatment = appointment.getTreatmentCost();
        BigDecimal total = consult.add(treatment);
        String billNumber = nextBillNumber();

        String sql = """
                INSERT INTO bills (bill_number, appointment_id, consultation_fee, treatment_cost, total_amount)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, billNumber);
            ps.setInt(2, appointment.getAppointmentId());
            ps.setBigDecimal(3, consult);
            ps.setBigDecimal(4, treatment);
            ps.setBigDecimal(5, total);
            ps.executeUpdate();
        }

        Bill bill = new Bill();
        bill.setBillNumber(billNumber);
        bill.setAppointmentId(appointment.getAppointmentId());
        bill.setConsultationFee(consult);
        bill.setTreatmentCost(treatment);
        bill.setTotalAmount(total);
        bill.setBilledAt(LocalDateTime.now());
        bill.setAppointment(appointment);
        return bill;
    }

    private String nextBillNumber() throws SQLException {
        String prefix = "BILL" + LocalDate.now().getYear();
        String sql = "SELECT bill_number FROM bills WHERE bill_number LIKE ? ORDER BY bill_number DESC LIMIT 1";
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

    private Bill map(ResultSet rs) throws SQLException {
        Bill b = new Bill();
        b.setBillId(rs.getInt("bill_id"));
        b.setBillNumber(rs.getString("bill_number"));
        b.setAppointmentId(rs.getInt("appointment_id"));
        b.setConsultationFee(rs.getBigDecimal("consultation_fee"));
        b.setTreatmentCost(rs.getBigDecimal("treatment_cost"));
        b.setTotalAmount(rs.getBigDecimal("total_amount"));
        b.setBilledAt(rs.getTimestamp("billed_at").toLocalDateTime());
        return b;
    }
}
