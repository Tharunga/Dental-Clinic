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

@WebServlet("/users/delete")
public class UserDeleteServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final UserDAO userDAO = new UserDAO();
    private final SystemLogDAO systemLogDAO = new SystemLogDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User current = AdminAccess.requireAdmin(req, resp);
        if (current == null) {
            return;
        }

        Integer id = parseId(req.getParameter("id"));
        if (id == null) {
            AdminAccess.flashError(req, "User not found.");
            resp.sendRedirect(req.getContextPath() + "/users");
            return;
        }

        try {
            User target = userDAO.findById(id);
            if (target == null) {
                AdminAccess.flashError(req, "User not found.");
                resp.sendRedirect(req.getContextPath() + "/users");
                return;
            }
            if (current.getUserId() == id) {
                AdminAccess.flashError(req, "You cannot delete your own account.");
                resp.sendRedirect(req.getContextPath() + "/users");
                return;
            }
            if (target.isAdmin() && userDAO.countAdmins() <= 1) {
                AdminAccess.flashError(req, "Cannot delete the last administrator.");
                resp.sendRedirect(req.getContextPath() + "/users");
                return;
            }

            // Clear created_by references so FK does not block delete
            userDAO.clearCreatedByReferences(id);
            userDAO.delete(id);
            systemLogDAO.logQuietly(current, "USER_DELETE", "Deleted user \"" + target.getUsername() + "\"");
            AdminAccess.flashSuccess(req, "User \"" + target.getUsername() + "\" deleted.");
        } catch (SQLException e) {
            AdminAccess.flashError(req, "Could not delete the user. Please try again.");
        }
        resp.sendRedirect(req.getContextPath() + "/users");
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
}
