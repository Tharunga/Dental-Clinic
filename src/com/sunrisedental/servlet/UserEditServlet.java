package com.sunrisedental.servlet;

import java.io.IOException;
import java.sql.SQLException;

import com.sunrisedental.dao.UserDAO;
import com.sunrisedental.model.User;
import com.sunrisedental.util.AdminAccess;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/users/edit")
public class UserEditServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (AdminAccess.requireAdmin(req, resp) == null) {
            return;
        }
        Integer id = parseId(req.getParameter("id"));
        if (id == null) {
            AdminAccess.flashError(req, "User not found.");
            resp.sendRedirect(req.getContextPath() + "/users");
            return;
        }
        try {
            User user = userDAO.findById(id);
            if (user == null) {
                AdminAccess.flashError(req, "User not found.");
                resp.sendRedirect(req.getContextPath() + "/users");
                return;
            }
            populateForm(req, user);
            req.getRequestDispatcher("/user-form.jsp").forward(req, resp);
        } catch (SQLException e) {
            AdminAccess.flashError(req, "Could not load the user. Please try again.");
            resp.sendRedirect(req.getContextPath() + "/users");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User current = AdminAccess.requireAdmin(req, resp);
        if (current == null) {
            return;
        }

        Integer id = parseId(req.getParameter("id"));
        String username = trim(req.getParameter("username"));
        String fullName = trim(req.getParameter("fullName"));
        String password = trim(req.getParameter("password"));
        boolean admin = "1".equals(req.getParameter("isAdmin"));

        if (id == null) {
            AdminAccess.flashError(req, "User not found.");
            resp.sendRedirect(req.getContextPath() + "/users");
            return;
        }

        User formUser = new User();
        formUser.setUserId(id);
        formUser.setUsername(username);
        formUser.setFullName(fullName);
        formUser.setAdmin(admin);
        populateForm(req, formUser);

        String error = validate(username, fullName, password);
        if (error == null) {
            try {
                User existing = userDAO.findById(id);
                if (existing == null) {
                    AdminAccess.flashError(req, "User not found.");
                    resp.sendRedirect(req.getContextPath() + "/users");
                    return;
                }
                if (userDAO.usernameExists(username, id)) {
                    error = "That username is already taken. Choose another.";
                } else if (existing.isAdmin() && !admin && userDAO.countAdmins() <= 1) {
                    error = "Cannot demote the last administrator.";
                } else if (current.getUserId() == id && !admin) {
                    error = "You cannot remove your own administrator access.";
                }
            } catch (SQLException e) {
                error = "Could not validate the user. Please try again.";
            }
        }
        if (error != null) {
            req.setAttribute("error", error);
            req.getRequestDispatcher("/user-form.jsp").forward(req, resp);
            return;
        }

        try {
            userDAO.update(formUser, password);
            if (current.getUserId() == id) {
                User refreshed = userDAO.findById(id);
                req.getSession().setAttribute("user", refreshed);
            }
            AdminAccess.flashSuccess(req, "User \"" + username + "\" updated successfully.");
            resp.sendRedirect(req.getContextPath() + "/users");
        } catch (SQLException e) {
            req.setAttribute("error", "Could not update the user. Please try again.");
            req.getRequestDispatcher("/user-form.jsp").forward(req, resp);
        }
    }

    private void populateForm(HttpServletRequest req, User user) {
        req.setAttribute("mode", "edit");
        req.setAttribute("formAction", req.getContextPath() + "/users/edit");
        req.setAttribute("pageTitle", "Edit user");
        req.setAttribute("userId", user.getUserId());
        req.setAttribute("username", user.getUsername());
        req.setAttribute("fullName", user.getFullName());
        req.setAttribute("isAdmin", user.isAdmin());
    }

    private static String validate(String username, String fullName, String password) {
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
        if (password != null && !password.isBlank() && password.length() < 4) {
            return "Password must be at least 4 characters.";
        }
        return null;
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
