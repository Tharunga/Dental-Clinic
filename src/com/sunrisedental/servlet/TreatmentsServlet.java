package com.sunrisedental.servlet;

import java.io.IOException;
import java.sql.SQLException;

import com.sunrisedental.dao.TreatmentDAO;
import com.sunrisedental.util.AdminAccess;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/treatments")
public class TreatmentsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final TreatmentDAO treatmentDAO = new TreatmentDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AdminAccess.transferFlash(req);
        try {
            req.setAttribute("treatments", treatmentDAO.findAll());
        } catch (SQLException e) {
            req.setAttribute("error", "Could not load treatments. Please check the database connection.");
        }
        req.getRequestDispatcher("/treatments.jsp").forward(req, resp);
    }
}
