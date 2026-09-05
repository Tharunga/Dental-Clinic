package com.sunrisedental.servlet;

import java.io.IOException;
import java.sql.SQLException;

import com.sunrisedental.dao.AppointmentDAO;
import com.sunrisedental.model.Appointment;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/appointments/search")
public class AppointmentSearchServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String number = trim(req.getParameter("number"));
        req.setAttribute("number", number);

        Object success = req.getSession().getAttribute("success");
        if (success != null) {
            req.setAttribute("success", success);
            req.getSession().removeAttribute("success");
        }

        if (!number.isEmpty()) {
            try {
                Appointment appointment = appointmentDAO.findByNumber(number.toUpperCase());
                if (appointment == null) {
                    req.setAttribute("error", "No appointment found for number " + number.toUpperCase() + ".");
                } else {
                    req.setAttribute("appointment", appointment);
                }
            } catch (SQLException e) {
                req.setAttribute("error", "Could not search appointments. Please check the database connection.");
            }
        }
        req.getRequestDispatcher("/appointment-details.jsp").forward(req, resp);
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
