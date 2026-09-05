package com.sunrisedental.servlet;

import java.io.IOException;
import java.sql.SQLException;

import com.sunrisedental.dao.DentistDAO;
import com.sunrisedental.dao.SystemLogDAO;
import com.sunrisedental.model.Dentist;
import com.sunrisedental.model.User;
import com.sunrisedental.util.AdminAccess;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/dentists/edit")
public class DentistEditServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final DentistDAO dentistDAO = new DentistDAO();
    private final SystemLogDAO systemLogDAO = new SystemLogDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (AdminAccess.requireAdmin(req, resp) == null) {
            return;
        }
        Integer id = parseId(req.getParameter("id"));
        if (id == null) {
            AdminAccess.flashError(req, "Dentist not found.");
            resp.sendRedirect(req.getContextPath() + "/dentists");
            return;
        }
        try {
            Dentist dentist = dentistDAO.findById(id);
            if (dentist == null) {
                AdminAccess.flashError(req, "Dentist not found.");
                resp.sendRedirect(req.getContextPath() + "/dentists");
                return;
            }
            populateForm(req, dentist);
            req.getRequestDispatcher("/dentist-form.jsp").forward(req, resp);
        } catch (SQLException e) {
            AdminAccess.flashError(req, "Could not load the dentist. Please try again.");
            resp.sendRedirect(req.getContextPath() + "/dentists");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User current = AdminAccess.requireAdmin(req, resp);
        if (current == null) {
            return;
        }

        Integer id = parseId(req.getParameter("id"));
        String dentistName = trim(req.getParameter("dentistName"));
        String specialization = trim(req.getParameter("specialization"));
        String mobileNumber = trim(req.getParameter("mobileNumber"));
        String email = trim(req.getParameter("email"));
        boolean active = "1".equals(req.getParameter("isActive"));

        if (id == null) {
            AdminAccess.flashError(req, "Dentist not found.");
            resp.sendRedirect(req.getContextPath() + "/dentists");
            return;
        }

        Dentist formDentist = new Dentist();
        formDentist.setDentistId(id);
        formDentist.setDentistName(dentistName);
        formDentist.setSpecialization(specialization);
        formDentist.setMobileNumber(mobileNumber);
        formDentist.setEmail(email);
        formDentist.setActive(active);
        populateForm(req, formDentist);

        String error = DentistCreateServlet.validate(dentistName, specialization, mobileNumber, email);
        if (error == null) {
            try {
                if (dentistDAO.findById(id) == null) {
                    AdminAccess.flashError(req, "Dentist not found.");
                    resp.sendRedirect(req.getContextPath() + "/dentists");
                    return;
                }
            } catch (SQLException e) {
                error = "Could not validate the dentist. Please try again.";
            }
        }
        if (error != null) {
            req.setAttribute("error", error);
            req.getRequestDispatcher("/dentist-form.jsp").forward(req, resp);
            return;
        }

        try {
            dentistDAO.update(formDentist);
            systemLogDAO.logQuietly(current, "DENTIST_UPDATE",
                    "Updated dentist \"" + dentistName + "\" (" + (active ? "ACTIVE" : "INACTIVE") + ")");
            AdminAccess.flashSuccess(req, "Dentist \"" + dentistName + "\" updated successfully.");
            resp.sendRedirect(req.getContextPath() + "/dentists");
        } catch (SQLException e) {
            req.setAttribute("error", "Could not update the dentist. Please try again.");
            req.getRequestDispatcher("/dentist-form.jsp").forward(req, resp);
        }
    }

    private void populateForm(HttpServletRequest req, Dentist dentist) {
        req.setAttribute("mode", "edit");
        req.setAttribute("formAction", req.getContextPath() + "/dentists/edit");
        req.setAttribute("pageTitle", "Edit dentist");
        req.setAttribute("dentistId", dentist.getDentistId());
        req.setAttribute("dentistName", dentist.getDentistName());
        req.setAttribute("specialization", dentist.getSpecialization());
        req.setAttribute("mobileNumber", dentist.getMobileNumber());
        req.setAttribute("email", dentist.getEmail());
        req.setAttribute("active", dentist.isActive());
    }

    private static Integer parseId(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String trim(String value) {
        return value == null ? null : value.trim();
    }
}
