package com.sunrisedental.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Browser cookies used by the clinic login page.
 * Stores only the last username (never the password) so staff can sign in faster.
 */
public final class CookieUtil {

    public static final String REMEMBER_USERNAME = "rememberUsername";
    private static final int THIRTY_DAYS_SECONDS = 60 * 60 * 24 * 30;

    private CookieUtil() {
    }

    public static String getCookieValue(HttpServletRequest req, String name) {
        Cookie[] cookies = req.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public static void saveRememberUsername(HttpServletResponse resp, String username) {
        Cookie cookie = new Cookie(REMEMBER_USERNAME, username);
        cookie.setMaxAge(THIRTY_DAYS_SECONDS);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        resp.addCookie(cookie);
    }

    public static void clearRememberUsername(HttpServletResponse resp) {
        Cookie cookie = new Cookie(REMEMBER_USERNAME, "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        resp.addCookie(cookie);
    }
}
