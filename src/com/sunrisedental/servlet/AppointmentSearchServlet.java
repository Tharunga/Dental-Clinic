package com.sunrisedental.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import com.sunrisedental.dao.AppointmentDAO;
import com.sunrisedental.dao.DentistDAO;
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
    private final DentistDAO dentistDAO = new DentistDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String number = trim(req.getParameter("number"));
        LocalDate date = parseDate(req.getParameter("date"));
        Integer dentistId = parseDentistId(req.getParameter("dentistId"));

        req.setAttribute("number", number);
        req.setAttribute("date", date != null ? date.toString() : "");
        req.setAttribute("dentistId", dentistId != null ? String.valueOf(dentistId) : "");

        Object success = req.getSession().getAttribute("success");
        if (success != null) {
            req.setAttribute("success", success);
            req.getSession().removeAttribute("success");
        }
        Object flashError = req.getSession().getAttribute("error");
        if (flashError != null) {
            req.setAttribute("error", flashError);
            req.getSession().removeAttribute("error");
        }

        try {
            req.setAttribute("dentists", dentistDAO.findAllActive());
            req.setAttribute("appointments", appointmentDAO.findFiltered(date, dentistId));
        } catch (SQLException e) {
            req.setAttribute("error", "Could not load appointments. Please check the database connection.");
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

    private LocalDate parseDate(String value) {
        String trimmed = trim(value);
        if (trimmed.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(trimmed);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private Integer parseDentistId(String value) {
        String trimmed = trim(value);
        if (trimmed.isEmpty()) {
            return null;
        }
        try {
            int id = Integer.parseInt(trimmed);
            return id > 0 ? id : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
