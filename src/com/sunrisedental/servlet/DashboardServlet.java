package com.sunrisedental.servlet;

import java.io.IOException;
import java.sql.SQLException;

import com.sunrisedental.dao.AppointmentDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("totalAppointments", appointmentDAO.countAll());
            req.setAttribute("todayAppointments", appointmentDAO.countToday());
            req.setAttribute("recentAppointments", appointmentDAO.findUpcoming(8));
        } catch (SQLException e) {
            req.setAttribute("error", "Could not load dashboard data. Please check the database connection.");
        }
        req.getRequestDispatcher("/dashboard.jsp").forward(req, resp);
    }
}
