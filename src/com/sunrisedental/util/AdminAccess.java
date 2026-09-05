package com.sunrisedental.util;

import com.sunrisedental.model.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public final class AdminAccess {

    private AdminAccess() {
    }

    public static User requireAdmin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");
        if (user == null || !user.isAdmin()) {
            if (session != null) {
                session.setAttribute("error", "Only administrators can manage users.");
            }
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return null;
        }
        return user;
    }

    public static void flashSuccess(HttpServletRequest req, String message) {
        req.getSession().setAttribute("success", message);
    }

    public static void flashError(HttpServletRequest req, String message) {
        req.getSession().setAttribute("error", message);
    }

    public static void transferFlash(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return;
        }
        Object success = session.getAttribute("success");
        if (success != null) {
            req.setAttribute("success", success);
            session.removeAttribute("success");
        }
        Object error = session.getAttribute("error");
        if (error != null) {
            req.setAttribute("error", error);
            session.removeAttribute("error");
        }
    }
}
