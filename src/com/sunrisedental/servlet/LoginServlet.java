package com.sunrisedental.servlet;

import java.io.IOException;
import java.sql.SQLException;

import com.sunrisedental.dao.SystemLogDAO;
import com.sunrisedental.dao.UserDAO;
import com.sunrisedental.model.User;
import com.sunrisedental.util.CookieUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final UserDAO userDAO = new UserDAO();
    private final SystemLogDAO systemLogDAO = new SystemLogDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }
        if (req.getAttribute("username") == null) {
            String remembered = CookieUtil.getCookieValue(req, CookieUtil.REMEMBER_USERNAME);
            if (remembered != null && !remembered.isBlank()) {
                req.setAttribute("username", remembered);
                req.setAttribute("rememberMe", true);
            }
        }
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = trim(req.getParameter("username"));
        String password = req.getParameter("password");
        boolean rememberMe = "on".equals(req.getParameter("rememberMe"));

        if (username.isEmpty() || password == null || password.isBlank()) {
            req.setAttribute("error", "Please enter both username and password.");
            req.setAttribute("username", username);
            req.setAttribute("rememberMe", rememberMe);
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
            return;
        }

        try {
            User user = userDAO.authenticate(username, password);
            if (user == null) {
                req.setAttribute("error", "Invalid username or password. Please try again.");
                req.setAttribute("username", username);
                req.setAttribute("rememberMe", rememberMe);
                req.getRequestDispatcher("/login.jsp").forward(req, resp);
                return;
            }
            if (rememberMe) {
                CookieUtil.saveRememberUsername(resp, username);
            } else {
                CookieUtil.clearRememberUsername(resp);
            }
            HttpSession session = req.getSession(true);
            session.setAttribute("user", user);
            systemLogDAO.logQuietly(user, "LOGIN", "User signed in");
            resp.sendRedirect(req.getContextPath() + "/dashboard");
        } catch (SQLException e) {
            req.setAttribute("error", "Unable to connect to the database. Check MySQL and db.properties.");
            req.setAttribute("username", username);
            req.setAttribute("rememberMe", rememberMe);
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
