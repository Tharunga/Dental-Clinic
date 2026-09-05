package com.sunrisedental.servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

import com.sunrisedental.dao.SystemLogDAO;
import com.sunrisedental.dao.TreatmentDAO;
import com.sunrisedental.model.Treatment;
import com.sunrisedental.model.User;
import com.sunrisedental.util.AdminAccess;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/treatments/new")
public class TreatmentCreateServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final TreatmentDAO treatmentDAO = new TreatmentDAO();
    private final SystemLogDAO systemLogDAO = new SystemLogDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (AdminAccess.requireAdmin(req, resp) == null) {
            return;
        }
        req.setAttribute("mode", "create");
        req.setAttribute("formAction", req.getContextPath() + "/treatments/new");
        req.setAttribute("pageTitle", "Add treatment");
        req.getRequestDispatcher("/treatment-form.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User current = AdminAccess.requireAdmin(req, resp);
        if (current == null) {
            return;
        }

        String treatmentName = trim(req.getParameter("treatmentName"));
        String costRaw = trim(req.getParameter("cost"));

        req.setAttribute("mode", "create");
        req.setAttribute("formAction", req.getContextPath() + "/treatments/new");
        req.setAttribute("pageTitle", "Add treatment");
        req.setAttribute("treatmentName", treatmentName);
        req.setAttribute("cost", costRaw);

        BigDecimal cost = parseCost(costRaw);
        String error = validate(treatmentName, costRaw, cost);
        if (error != null) {
            req.setAttribute("error", error);
            req.getRequestDispatcher("/treatment-form.jsp").forward(req, resp);
            return;
        }

        try {
            Treatment treatment = new Treatment();
            treatment.setTreatmentName(treatmentName);
            treatment.setCost(cost);
            treatmentDAO.create(treatment);
            systemLogDAO.logQuietly(current, "TREATMENT_CREATE",
                    "Added treatment \"" + treatmentName + "\" (LKR " + cost + ")");
            AdminAccess.flashSuccess(req, "Treatment \"" + treatmentName + "\" added successfully.");
            resp.sendRedirect(req.getContextPath() + "/treatments");
        } catch (SQLException e) {
            req.setAttribute("error", "Could not add the treatment. Please try again.");
            req.getRequestDispatcher("/treatment-form.jsp").forward(req, resp);
        }
    }

    static String validate(String treatmentName, String costRaw, BigDecimal cost) {
        if (treatmentName == null || treatmentName.isBlank()) {
            return "Treatment name is required.";
        }
        if (treatmentName.length() > 80) {
            return "Treatment name must be 80 characters or fewer.";
        }
        if (costRaw == null || costRaw.isBlank()) {
            return "Cost is required.";
        }
        if (cost == null) {
            return "Enter a valid cost amount.";
        }
        if (cost.compareTo(BigDecimal.ZERO) < 0) {
            return "Cost cannot be negative.";
        }
        return null;
    }

    static BigDecimal parseCost(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return new BigDecimal(raw.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String trim(String value) {
        return value == null ? null : value.trim();
    }
}
