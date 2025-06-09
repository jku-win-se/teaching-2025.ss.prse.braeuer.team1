package at.jku.se.lunchify.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

//AI-Assisted
public class InvoiceSettingService {
    private final String jdbcUrl = "jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:6543/postgres";
    private final String dbUsername = "postgres.yxshntkgvmksefegyfhz";
    private final String dbPassword = "CaMaKe25!";

    /**
     * Updates the reimbursement amount for Invoices
     * <p>
     * This method return a boolean whether the update was sucessfull or not
     * <p>
     * @return true if successful, false if an Exception emerges
     */
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
    /**
     * Checks if the input for changing the setting is valid
     * <p>
     * This function returns a booolean in regards to if the input is blank and in the double format or not
     * <p>
     * @param input wished value for the setting
     * @return true if value is valid, false if value is invalid (blank, not in a double format)
     */
    public boolean isValidInput(String input) {
        if (input == null || input.isBlank()) return true; // leer = erlaubt
        try {
            double value = Double.parseDouble(input);
            return value >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    /**
     * Returns the current setting for supermarket reimbursements
     * <p>
     * This method returns the current setting for the supermarket reimbursement amount in the database
     * <p>
     * @return Value if database access was successfull, -1 if not
     */
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
    /**
     * Returns the current setting for restaurant reimbursements
     * <p>
     * This method returns the current setting for the restaurant reimbursement amount in the database
     * <p>
     * @return Value if database access was successfull, -1 if not
     */
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

    /**
     * Returns the possible Invoice types
     * <p>
     * This method returns an ObservableLIst of Strings the possible Invoice types
     * <p>
     * @return ObsrvableList of Strings
     */
    public ObservableList<String> getAllInvoiceTypes() {
        ObservableList<String> invoiceTypes = FXCollections.observableArrayList();
        invoiceTypes.add("Supermarkt");
        invoiceTypes.add("Restaurant");
        return invoiceTypes;
    }
    /**
     * Returns the calculated reimbursement value of the invoice
     * <p>
     * This function returns the calculated reimbursement value of the submitted invoice. If the Invoice value is lower the the current setting, the Invoice value is reimbursed
     * <p>
     * @param invoiceType selected Invoice type
     * @param value Invoice value
     * @return calulated reimbursement value (value - setting value, max. setting value), if Invoice type not "Supermarkt" or "Restaurant" then returns 0
     */
    public double getReimbursementValue(String invoiceType, double value) {
        if (invoiceType.equals("Supermarkt")) {
            return Math.min(getCurrentSupermarketValue(), value);
        }
        else if (invoiceType.equals("Restaurant")) {
            return Math.min(getCurrentRestaurantValue(), value);
        }
        else return 0;
    }
}