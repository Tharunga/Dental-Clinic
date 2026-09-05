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

@WebServlet("/dentists/new")
public class DentistCreateServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final DentistDAO dentistDAO = new DentistDAO();
    private final SystemLogDAO systemLogDAO = new SystemLogDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (AdminAccess.requireAdmin(req, resp) == null) {
            return;
        }
        req.setAttribute("mode", "create");
        req.setAttribute("formAction", req.getContextPath() + "/dentists/new");
        req.setAttribute("pageTitle", "Add dentist");
        req.setAttribute("active", true);
        req.getRequestDispatcher("/dentist-form.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User current = AdminAccess.requireAdmin(req, resp);
        if (current == null) {
            return;
        }

        String dentistName = trim(req.getParameter("dentistName"));
        String specialization = trim(req.getParameter("specialization"));
        String mobileNumber = trim(req.getParameter("mobileNumber"));
        String email = trim(req.getParameter("email"));
        boolean active = "1".equals(req.getParameter("isActive"));

        req.setAttribute("mode", "create");
        req.setAttribute("formAction", req.getContextPath() + "/dentists/new");
        req.setAttribute("pageTitle", "Add dentist");
        req.setAttribute("dentistName", dentistName);
        req.setAttribute("specialization", specialization);
        req.setAttribute("mobileNumber", mobileNumber);
        req.setAttribute("email", email);
        req.setAttribute("active", active);

        String error = validate(dentistName, specialization, mobileNumber, email);
        if (error != null) {
            req.setAttribute("error", error);
            req.getRequestDispatcher("/dentist-form.jsp").forward(req, resp);
            return;
        }

        try {
            Dentist dentist = new Dentist();
            dentist.setDentistName(dentistName);
            dentist.setSpecialization(specialization);
            dentist.setMobileNumber(mobileNumber);
            dentist.setEmail(email);
            dentist.setActive(active);
            dentistDAO.create(dentist);
            systemLogDAO.logQuietly(current, "DENTIST_CREATE",
                    "Added dentist \"" + dentistName + "\" (" + (active ? "ACTIVE" : "INACTIVE") + ")");
            AdminAccess.flashSuccess(req, "Dentist \"" + dentistName + "\" added successfully.");
            resp.sendRedirect(req.getContextPath() + "/dentists");
        } catch (SQLException e) {
            req.setAttribute("error", "Could not add the dentist. Please try again.");
            req.getRequestDispatcher("/dentist-form.jsp").forward(req, resp);
        }
    }

    static String validate(String dentistName, String specialization, String mobileNumber, String email) {
        if (dentistName == null || dentistName.isBlank()) {
            return "Dentist name is required.";
        }
        if (dentistName.length() > 100) {
            return "Dentist name must be 100 characters or fewer.";
        }
        if (specialization == null || specialization.isBlank()) {
            return "Specialization is required.";
        }
        if (specialization.length() > 80) {
            return "Specialization must be 80 characters or fewer.";
        }
        if (mobileNumber == null || mobileNumber.isBlank()) {
            return "Mobile number is required.";
        }
        if (mobileNumber.length() > 20) {
            return "Mobile number must be 20 characters or fewer.";
        }
        if (!mobileNumber.matches("^[0-9+\\s()-]{7,20}$")) {
            return "Enter a valid mobile number.";
        }
        if (email == null || email.isBlank()) {
            return "Email is required.";
        }
        if (email.length() > 100) {
            return "Email must be 100 characters or fewer.";
        }
        if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            return "Enter a valid email address.";
        }
        return null;
    }

    private static String trim(String value) {
        return value == null ? null : value.trim();
    }
}
