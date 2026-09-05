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

@WebServlet("/treatments/edit")
public class TreatmentEditServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final TreatmentDAO treatmentDAO = new TreatmentDAO();
    private final SystemLogDAO systemLogDAO = new SystemLogDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (AdminAccess.requireAdmin(req, resp) == null) {
            return;
        }
        Integer id = parseId(req.getParameter("id"));
        if (id == null) {
            AdminAccess.flashError(req, "Treatment not found.");
            resp.sendRedirect(req.getContextPath() + "/treatments");
            return;
        }
        try {
            Treatment treatment = treatmentDAO.findById(id);
            if (treatment == null) {
                AdminAccess.flashError(req, "Treatment not found.");
                resp.sendRedirect(req.getContextPath() + "/treatments");
                return;
            }
            populateForm(req, treatment);
            req.getRequestDispatcher("/treatment-form.jsp").forward(req, resp);
        } catch (SQLException e) {
            AdminAccess.flashError(req, "Could not load the treatment. Please try again.");
            resp.sendRedirect(req.getContextPath() + "/treatments");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User current = AdminAccess.requireAdmin(req, resp);
        if (current == null) {
            return;
        }

        Integer id = parseId(req.getParameter("id"));
        String treatmentName = trim(req.getParameter("treatmentName"));
        String costRaw = trim(req.getParameter("cost"));
        BigDecimal cost = TreatmentCreateServlet.parseCost(costRaw);

        if (id == null) {
            AdminAccess.flashError(req, "Treatment not found.");
            resp.sendRedirect(req.getContextPath() + "/treatments");
            return;
        }

        Treatment formTreatment = new Treatment();
        formTreatment.setTreatmentId(id);
        formTreatment.setTreatmentName(treatmentName);
        formTreatment.setCost(cost);
        populateForm(req, formTreatment, costRaw);

        String error = TreatmentCreateServlet.validate(treatmentName, costRaw, cost);
        if (error == null) {
            try {
                if (treatmentDAO.findById(id) == null) {
                    AdminAccess.flashError(req, "Treatment not found.");
                    resp.sendRedirect(req.getContextPath() + "/treatments");
                    return;
                }
            } catch (SQLException e) {
                error = "Could not validate the treatment. Please try again.";
            }
        }
        if (error != null) {
            req.setAttribute("error", error);
            req.getRequestDispatcher("/treatment-form.jsp").forward(req, resp);
            return;
        }

        try {
            treatmentDAO.update(formTreatment);
            systemLogDAO.logQuietly(current, "TREATMENT_UPDATE",
                    "Updated treatment \"" + treatmentName + "\" (LKR " + cost + ")");
            AdminAccess.flashSuccess(req, "Treatment \"" + treatmentName + "\" updated successfully.");
            resp.sendRedirect(req.getContextPath() + "/treatments");
        } catch (SQLException e) {
            req.setAttribute("error", "Could not update the treatment. Please try again.");
            req.getRequestDispatcher("/treatment-form.jsp").forward(req, resp);
        }
    }

    private void populateForm(HttpServletRequest req, Treatment treatment) {
        populateForm(req, treatment, treatment.getCost() != null ? treatment.getCost().toPlainString() : "");
    }

    private void populateForm(HttpServletRequest req, Treatment treatment, String costRaw) {
        req.setAttribute("mode", "edit");
        req.setAttribute("formAction", req.getContextPath() + "/treatments/edit");
        req.setAttribute("pageTitle", "Edit treatment");
        req.setAttribute("treatmentId", treatment.getTreatmentId());
        req.setAttribute("treatmentName", treatment.getTreatmentName());
        req.setAttribute("cost", costRaw != null ? costRaw : "");
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
