package com.sunrisedental.servlet;

import java.io.IOException;
import java.sql.SQLException;

import com.sunrisedental.dao.DentistDAO;
import com.sunrisedental.util.AdminAccess;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/dentists")
public class DentistsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final DentistDAO dentistDAO = new DentistDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (AdminAccess.requireAdmin(req, resp) == null) {
            return;
        }
        AdminAccess.transferFlash(req);
        try {
            req.setAttribute("dentists", dentistDAO.findAll());
        } catch (SQLException e) {
            req.setAttribute("error", "Could not load dentists. Please check the database connection.");
        }
        req.getRequestDispatcher("/dentists.jsp").forward(req, resp);
    }
}
