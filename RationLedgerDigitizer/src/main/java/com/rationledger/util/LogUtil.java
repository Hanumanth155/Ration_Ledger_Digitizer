package com.rationledger.util;

import java.sql.Connection;
import java.sql.PreparedStatement;

public final class LogUtil {
    private LogUtil() {}
    public static void log(String username, String action, String details) {
        String sql = "INSERT INTO audit_logs(username,action,details) VALUES(?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, action);
            ps.setString(3, details);
            ps.executeUpdate();
        } catch (Exception ignored) {}
    }
}
