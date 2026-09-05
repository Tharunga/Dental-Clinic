package com.sunrisedental.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.sunrisedental.model.SystemLog;
import com.sunrisedental.model.User;
import com.sunrisedental.util.DBConnection;

public class SystemLogDAO {

    public void insert(Integer userId, String username, String action, String details) throws SQLException {
        String sql = "INSERT INTO system_log (user_id, username, action, details) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            if (userId == null) {
                ps.setNull(1, Types.INTEGER);
            } else {
                ps.setInt(1, userId);
            }
            ps.setString(2, username);
            ps.setString(3, action);
            ps.setString(4, truncate(details, 255));
            ps.executeUpdate();
        }
    }

    /** Best-effort write — never throws; main flow must not fail because of logging. */
    public void logQuietly(User user, String action, String details) {
        try {
            Integer userId = user == null ? null : user.getUserId();
            String username = user == null ? null : user.getUsername();
            insert(userId, username, action, details);
        } catch (SQLException ignored) {
            // logging must not break the primary action
        }
    }

    public List<SystemLog> findAllNewestFirst() throws SQLException {
        String sql = "SELECT log_id, user_id, username, action, details, created_at "
                + "FROM system_log ORDER BY created_at DESC, log_id DESC";
        List<SystemLog> rows = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                SystemLog log = new SystemLog();
                log.setLogId(rs.getInt("log_id"));
                int uid = rs.getInt("user_id");
                if (!rs.wasNull()) {
                    log.setUserId(uid);
                }
                log.setUsername(rs.getString("username"));
                log.setAction(rs.getString("action"));
                log.setDetails(rs.getString("details"));
                Timestamp createdAt = rs.getTimestamp("created_at");
                if (createdAt != null) {
                    log.setCreatedAt(createdAt.toLocalDateTime());
                }
                rows.add(log);
            }
        }
        return rows;
    }

    private static String truncate(String value, int max) {
        if (value == null) {
            return null;
        }
        return value.length() <= max ? value : value.substring(0, max);
    }
}
