package com.sunrisedental.servlet;

import java.io.IOException;
import java.sql.SQLException;

import com.sunrisedental.dao.AppointmentDAO;
import com.sunrisedental.dao.BillDAO;
import com.sunrisedental.dao.SystemLogDAO;
import com.sunrisedental.model.Appointment;
import com.sunrisedental.model.Bill;
import com.sunrisedental.model.User;
import com.sunrisedental.util.ClinicConfig;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/billing")
public class BillServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final BillDAO billDAO = new BillDAO();
    private final SystemLogDAO systemLogDAO = new SystemLogDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("consultationFee", ClinicConfig.CONSULTATION_FEE);
        String number = trim(req.getParameter("number"));
        req.setAttribute("number", number);
        if (!number.isEmpty()) {
            lookup(req, number, false);
        }
        req.getRequestDispatcher("/bill.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("consultationFee", ClinicConfig.CONSULTATION_FEE);
        String number = trim(req.getParameter("number"));
        req.setAttribute("number", number);
        lookup(req, number, true);
        req.getRequestDispatcher("/bill.jsp").forward(req, resp);
    }

    private void lookup(HttpServletRequest req, String number, boolean generate) {
        if (number.isEmpty()) {
            req.setAttribute("error", "Enter an appointment number to calculate the bill.");
            return;
        }
        try {
            Appointment appointment = appointmentDAO.findByNumber(number.toUpperCase());
            if (appointment == null) {
                req.setAttribute("error", "No appointment found for number " + number.toUpperCase() + ".");
                return;
            }
            req.setAttribute("appointment", appointment);
            if (generate) {
                Bill bill = billDAO.createFor(appointment);
                req.setAttribute("bill", bill);
                User current = (User) req.getSession().getAttribute("user");
                systemLogDAO.logQuietly(current, "BILL_CALCULATE",
                        "Calculated bill " + bill.getBillNumber() + " for " + appointment.getAppointmentNumber());
            } else {
                Bill existing = billDAO.findByAppointmentId(appointment.getAppointmentId());
                if (existing != null) {
                    existing.setAppointment(appointment);
                    req.setAttribute("bill", existing);
                }
            }
        } catch (SQLException e) {
            req.setAttribute("error", "Could not calculate the bill. Please try again.");
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
