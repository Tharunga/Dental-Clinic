package com.sunrisedental.servlet;

import java.io.IOException;
import java.sql.SQLException;

import com.sunrisedental.dao.SystemLogDAO;
import com.sunrisedental.util.AdminAccess;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/system-log")
public class SystemLogServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final SystemLogDAO systemLogDAO = new SystemLogDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (AdminAccess.requireAdmin(req, resp) == null) {
            return;
        }
        AdminAccess.transferFlash(req);
        try {
            req.setAttribute("logs", systemLogDAO.findAllNewestFirst());
        } catch (SQLException e) {
            req.setAttribute("error", "Could not load the system log. Please check the database connection.");
        }
        req.getRequestDispatcher("/system-log.jsp").forward(req, resp);
    }
}
