package com.sunrisedental.servlet;

import java.io.IOException;
import java.sql.SQLException;

import com.sunrisedental.dao.PatientDAO;
import com.sunrisedental.util.AdminAccess;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/patients")
public class PatientsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final PatientDAO patientDAO = new PatientDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AdminAccess.transferFlash(req);
        String q = trim(req.getParameter("q"));
        req.setAttribute("q", q);
        try {
            if (q.isEmpty()) {
                req.setAttribute("patients", patientDAO.findAll());
            } else {
                req.setAttribute("patients", patientDAO.search(q));
            }
        } catch (SQLException e) {
            req.setAttribute("error", "Could not load patients. Please check the database connection.");
        }
        req.getRequestDispatcher("/patients.jsp").forward(req, resp);
    }

    private static String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
