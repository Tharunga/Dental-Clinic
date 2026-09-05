package com.sunrisedental.servlet;

import java.io.IOException;
import java.sql.SQLException;

import com.sunrisedental.dao.SystemLogDAO;
import com.sunrisedental.dao.UserDAO;
import com.sunrisedental.model.User;
import com.sunrisedental.util.AdminAccess;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/users/new")
public class UserCreateServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final UserDAO userDAO = new UserDAO();
    private final SystemLogDAO systemLogDAO = new SystemLogDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (AdminAccess.requireAdmin(req, resp) == null) {
            return;
        }
        req.setAttribute("mode", "create");
        req.setAttribute("formAction", req.getContextPath() + "/users/new");
        req.setAttribute("pageTitle", "Add user");
        req.getRequestDispatcher("/user-form.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User current = AdminAccess.requireAdmin(req, resp);
        if (current == null) {
            return;
        }

        String username = trim(req.getParameter("username"));
        String fullName = trim(req.getParameter("fullName"));
        String password = trim(req.getParameter("password"));
        boolean admin = "1".equals(req.getParameter("isAdmin"));

        req.setAttribute("mode", "create");
        req.setAttribute("formAction", req.getContextPath() + "/users/new");
        req.setAttribute("pageTitle", "Add user");
        req.setAttribute("username", username);
        req.setAttribute("fullName", fullName);
        req.setAttribute("isAdmin", admin);

        String error = validate(username, fullName, password, true);
        if (error == null) {
            try {
                if (userDAO.usernameExists(username, null)) {
                    error = "That username is already taken. Choose another.";
                }
            } catch (SQLException e) {
                error = "Could not validate username. Please try again.";
            }
        }
        if (error != null) {
            req.setAttribute("error", error);
            req.getRequestDispatcher("/user-form.jsp").forward(req, resp);
            return;
        }

        try {
            User user = new User();
            user.setUsername(username);
            user.setFullName(fullName);
            user.setAdmin(admin);
            userDAO.create(user, password, current.getUserId());
            systemLogDAO.logQuietly(current, "USER_CREATE",
                    "Created user \"" + username + "\" (" + (admin ? "ADMIN" : "RECEPTIONIST") + ")");
            AdminAccess.flashSuccess(req, "User \"" + username + "\" created successfully.");
            resp.sendRedirect(req.getContextPath() + "/users");
        } catch (SQLException e) {
            req.setAttribute("error", "Could not create the user. Please try again.");
            req.getRequestDispatcher("/user-form.jsp").forward(req, resp);
        }
    }

    private static String validate(String username, String fullName, String password, boolean passwordRequired) {
        if (username == null || username.isBlank()) {
            return "Username is required.";
        }
        if (username.length() < 3 || username.length() > 50) {
            return "Username must be between 3 and 50 characters.";
        }
        if (!username.matches("^[A-Za-z0-9._-]+$")) {
            return "Username may only contain letters, numbers, dots, underscores, and hyphens.";
        }
        if (fullName == null || fullName.isBlank()) {
            return "Full name is required.";
        }
        if (fullName.length() > 100) {
            return "Full name must be 100 characters or fewer.";
        }
        if (passwordRequired && (password == null || password.isBlank())) {
            return "Password is required.";
        }
        if (password != null && !password.isBlank() && password.length() < 4) {
            return "Password must be at least 4 characters.";
        }
        return null;
    }

    private static String trim(String value) {
        return value == null ? null : value.trim();
    }
}
