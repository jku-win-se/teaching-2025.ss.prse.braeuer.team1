package at.jku.se.lunchify.models;

import at.jku.se.lunchify.security.PasswordService;

import java.sql.*;

//AI-Assisted
public class LoginService {

    private final String jdbcUrl;
    private final String dbUsername;
    private final String dbPassword;
    private final PasswordService passwordService;
    private int userId;

    public LoginService(String jdbcUrl, String dbUsername, String dbPassword, PasswordService passwordService) {
        this.jdbcUrl = jdbcUrl;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
        this.passwordService = passwordService;
    }

    public LoginService() {
        this.jdbcUrl = "jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:6543/postgres";
        this.dbUsername = "postgres.yxshntkgvmksefegyfhz";
        this.dbPassword = "CaMaKe25!";
        this.passwordService = new PasswordService(); // oder via DI-Framework
    }

    public int getUserId() {
        return userId;
    }

    public enum LoginResult {
        SUCCESS_ADMIN,
        SUCCESS_USER,
        INVALID_USER,
        INVALID_PW,
        USER_INACTIVE,
        EMPTY_FIELDS,
        ERROR
    }

    public LoginResult login(String email, String password) {
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return LoginResult.EMPTY_FIELDS;
        }

        try (Connection conn = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)) {
            String sql = "SELECT userid, type, isactive, password FROM public.\"User\" WHERE email = ?";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, email);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        boolean isActive = rs.getBoolean("isactive");

                        if (!isActive) {
                            return LoginResult.USER_INACTIVE;
                        }

                        String dbPassword = rs.getString("password");
                        if (!passwordService.verifyPassword(password, dbPassword)) {
                            return LoginResult.INVALID_PW;
                        }

                        String userType = rs.getString("type");
                        userId = rs.getInt("userid");
                        return "Admin".equals(userType) ? LoginResult.SUCCESS_ADMIN : LoginResult.SUCCESS_USER;
                    } else {
                        return LoginResult.INVALID_USER;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return LoginResult.ERROR;
        }
    }
}