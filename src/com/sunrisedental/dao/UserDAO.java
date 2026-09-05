package com.sunrisedental.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.sunrisedental.model.User;
import com.sunrisedental.util.DBConnection;
import com.sunrisedental.util.PasswordUtil;

public class UserDAO {

    public User authenticate(String username, String password) throws SQLException {
        String sql = "SELECT user_id, username, password_hash, full_name, is_admin, created_by, created_at "
                + "FROM users WHERE username = ?";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && PasswordUtil.matches(password, rs.getString("password_hash"))) {
                    return mapUser(rs, false);
                }
            }
        }
        return null;
    }

    public List<User> findAll() throws SQLException {
        String sql = "SELECT u.user_id, u.username, u.full_name, u.is_admin, u.created_by, u.created_at, "
                + "c.full_name AS created_by_name "
                + "FROM users u LEFT JOIN users c ON u.created_by = c.user_id "
                + "ORDER BY u.full_name";
        List<User> users = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(mapUser(rs, true));
            }
        }
        return users;
    }

    public User findById(int id) throws SQLException {
        String sql = "SELECT u.user_id, u.username, u.full_name, u.is_admin, u.created_by, u.created_at, "
                + "c.full_name AS created_by_name "
                + "FROM users u LEFT JOIN users c ON u.created_by = c.user_id "
                + "WHERE u.user_id = ?";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs, true);
                }
            }
        }
        return null;
    }

    public void create(User user, String plainPassword, Integer createdByUserId) throws SQLException {
        String sql = "INSERT INTO users (username, password_hash, full_name, is_admin, created_by) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, PasswordUtil.sha256(plainPassword));
            ps.setString(3, user.getFullName());
            ps.setInt(4, user.isAdmin() ? 1 : 0);
            if (createdByUserId == null) {
                ps.setNull(5, Types.INTEGER);
            } else {
                ps.setInt(5, createdByUserId);
            }
            ps.executeUpdate();
        }
    }

    public void update(User user, String plainPasswordOrNull) throws SQLException {
        if (plainPasswordOrNull != null && !plainPasswordOrNull.isBlank()) {
            String sql = "UPDATE users SET username = ?, full_name = ?, is_admin = ?, password_hash = ? WHERE user_id = ?";
            try (Connection con = DBConnection.getConnection();
                    PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getFullName());
                ps.setInt(3, user.isAdmin() ? 1 : 0);
                ps.setString(4, PasswordUtil.sha256(plainPasswordOrNull));
                ps.setInt(5, user.getUserId());
                ps.executeUpdate();
            }
        } else {
            String sql = "UPDATE users SET username = ?, full_name = ?, is_admin = ? WHERE user_id = ?";
            try (Connection con = DBConnection.getConnection();
                    PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getFullName());
                ps.setInt(3, user.isAdmin() ? 1 : 0);
                ps.setInt(4, user.getUserId());
                ps.executeUpdate();
            }
        }
    }

    public void clearCreatedByReferences(int userId) throws SQLException {
        String sql = "UPDATE users SET created_by = NULL WHERE created_by = ?";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public boolean usernameExists(String username, Integer excludeUserId) throws SQLException {
        String sql = excludeUserId == null
                ? "SELECT 1 FROM users WHERE username = ?"
                : "SELECT 1 FROM users WHERE username = ? AND user_id <> ?";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            if (excludeUserId != null) {
                ps.setInt(2, excludeUserId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public int countAdmins() throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE is_admin = 1";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    private User mapUser(ResultSet rs, boolean withCreator) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setFullName(rs.getString("full_name"));
        user.setAdmin(rs.getInt("is_admin") == 1);
        int createdBy = rs.getInt("created_by");
        if (!rs.wasNull()) {
            user.setCreatedBy(createdBy);
        }
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }
        if (withCreator) {
            user.setCreatedByName(rs.getString("created_by_name"));
        }
        return user;
    }
}
