package at.jku.se.lunchify.models;

import java.sql.*;

//AI-Assisted
public class InvoiceSettingService {
    private final String jdbcUrl = "jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:6543/postgres";
    private final String dbUsername = "postgres.yxshntkgvmksefegyfhz";
    private final String dbPassword = "CaMaKe25!";

    public boolean updateInvoiceSettings(String supermarketValueStr, String restaurantValueStr) {
        boolean updateSupermarket = !supermarketValueStr.isBlank();
        boolean updateRestaurant = !restaurantValueStr.isBlank();

        if (!updateSupermarket && !updateRestaurant) {
            return false; // nichts zu ändern
        }

        if(!isValidInput(supermarketValueStr) || !isValidInput(restaurantValueStr)) {
            return false; // falsche Eingaben
        }

        StringBuilder sql = new StringBuilder("UPDATE \"InvoiceSetting\" SET ");
        if (updateSupermarket) sql.append("valueinvoicesupermarket = ?");
        if (updateRestaurant) {
            if (updateSupermarket) sql.append(", ");
            sql.append("valueinvoicerestaurant = ?");
        }
        sql.append(" WHERE settingid = 1");

        try (Connection conn = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int index = 1;
            if (updateSupermarket) pstmt.setDouble(index++, Double.parseDouble(supermarketValueStr));
            if (updateRestaurant) pstmt.setDouble(index, Double.parseDouble(restaurantValueStr));

            int updated = pstmt.executeUpdate();
            return updated > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isValidInput(String input) {
        if (input == null || input.isBlank()) return true; // leer = erlaubt
        try {
            double value = Double.parseDouble(input);
            return value >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public double getCurrentSupermarketValue() {
        try (Connection conn = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)) {
            String query = "SELECT valueinvoicesupermarket FROM \"InvoiceSetting\" WHERE settingid = 1";
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("valueinvoicesupermarket");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public double getCurrentRestaurantValue() {
        try (Connection conn = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)) {
            String query = "SELECT valueinvoicerestaurant FROM \"InvoiceSetting\" WHERE settingid = 1";
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("valueinvoicerestaurant");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}