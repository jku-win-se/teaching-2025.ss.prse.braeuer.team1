package at.jku.se.lunchify.models;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class InvoiceKpiService {

    private final String jdbcUrl;
    private final String dbUsername;
    private final String dbPassword;
    private final String userEmail;
    private final String invoiceType;
    private final Date fromDate;
    private final Date toDate;

    private int months; // muss über fromDate und toDate ermittelt werden, befindet sich beides in einem Monat -> 1, ansonsten die anzahl der betroffenen monate des zeitraums
    private String userName; //muss über userEmail abgefragt werden, falls "alle Benutzer" ->"Alle Benutzer"
    private int userCount; //Bei ausgewähltem Benutzer =1 (userEmail), falls "alle Benutzer" ausgewählt wurde muss gezählt werden
    private double sumReimbursements; //Summe der rückerstatteten Beträge von "GENEHMIGT" Rechnungen von ausgewählten Benutzer
    private int invoiceCount; //Anzahl der "GENEHMIGT" Rechnungen des/der ausgewählten Benutzer
    private int invoiceCountSupermarket; //Anzahl der "GENEHMIGT" Rechnungen des/der ausgewählten Benutzer vom Typ Supermarkt
    private int invoiceCountRestaurant; //Anzahl der "GENEHMIGT" Rechnungen des/der ausgewählten Benutzer vom Typ Restaurant


    public InvoiceKpiService(String userEmail, String invoiceType, Date fromDate, Date toDate) {
        this.userEmail = userEmail;
        this.invoiceType = invoiceType;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.jdbcUrl = "jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:6543/postgres";
        this.dbUsername = "postgres.yxshntkgvmksefegyfhz";
        this.dbPassword = "CaMaKe25!";

        calculateKpis();
    }

    private void calculateKpis() {
        try (Connection conn = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)) {

            // Berechne Monate
            LocalDate from = fromDate.toLocalDate();
            LocalDate to = toDate.toLocalDate();
            months = (int) ChronoUnit.MONTHS.between(from.withDayOfMonth(1), to.withDayOfMonth(1)) + 1;

            // Nutzername & ID
            Integer userId = null;
            if (!userEmail.equalsIgnoreCase("alle Benutzer")) {
                try (PreparedStatement stmt = conn.prepareStatement("SELECT userid, firstname, surname FROM \"User\" WHERE email = ?")) {
                    stmt.setString(1, userEmail);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            userId = rs.getInt("userid");
                            userName = rs.getString("firstname") + " " + rs.getString("surname");
                        }
                    }
                }
            } else {
                userName = "Alle Benutzer";
            }

            // Benutzer zählen
            if (userId == null) {
                try (PreparedStatement stmt = conn.prepareStatement("""
                    SELECT COUNT(DISTINCT u.userid)
                    FROM "User" u
                    JOIN "Invoice" i ON u.userid = i.userid
                    WHERE i.date BETWEEN ? AND ?
                """)) {
                    stmt.setDate(1, fromDate);
                    stmt.setDate(2, toDate);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) userCount = rs.getInt(1);
                    }
                }
            } else {
                userCount = 1;
            }

            // Hauptdaten: Anzahl, Summe, Typen
            String baseQuery = """
                SELECT type, COUNT(*) as count, SUM(reimbursementamount) as total
                FROM "Invoice"
                WHERE status = 'GENEHMIGT' AND date BETWEEN ? AND ?
            """;

            if (userId != null) baseQuery += " AND userid = ?";
            if (!invoiceType.equalsIgnoreCase("alle Rechnungstypen")) baseQuery += " AND type = ?";

            baseQuery += " GROUP BY type";

            try (PreparedStatement stmt = conn.prepareStatement(baseQuery)) {
                stmt.setDate(1, fromDate);
                stmt.setDate(2, toDate);

                int idx = 3;
                if (userId != null) stmt.setInt(idx++, userId);
                if (!invoiceType.equalsIgnoreCase("alle Rechnungstypen")) stmt.setString(idx, invoiceType);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String type = rs.getString("type");
                        int count = rs.getInt("count");
                        double total = rs.getDouble("total");

                        invoiceCount += count;
                        sumReimbursements += total;

                        if (type.equalsIgnoreCase("Supermarkt")) {
                            invoiceCountSupermarket = count;
                        } else if (type.equalsIgnoreCase("Restaurant")) {
                            invoiceCountRestaurant = count;
                        }
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // === Getter für Controller ===
    public String getUserName() { return userName; }
    public int getMonths() { return months; }
    public int getUserCount() { return userCount; }
    public int getInvoiceCount() { return invoiceCount; }
    public double getSumReimbursements() { return sumReimbursements; }
    public int getInvoiceCountSupermarket() { return invoiceCountSupermarket; }
    public int getInvoiceCountRestaurant() { return invoiceCountRestaurant; }
}
