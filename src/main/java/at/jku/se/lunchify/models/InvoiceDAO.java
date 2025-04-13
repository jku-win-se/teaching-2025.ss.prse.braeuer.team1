package at.jku.se.lunchify.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class InvoiceDAO {
    String jdbcUrl = "jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:6543/postgres";
    String username = "postgres.yxshntkgvmksefegyfhz";
    String DBpassword = "CaMaKe25!";

    // Methode zum Abrufen aller Rechnungen
    public ObservableList<Invoice> getAllInvoices() {
        ObservableList<Invoice> invoices = FXCollections.observableArrayList();

        String sql = "SELECT * FROM \"Invoice\"";
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, DBpassword);
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet resultSet = ps.executeQuery()) {

            while (resultSet.next()) {
                int invoiceid = resultSet.getInt("invoiceid");
                int userid = resultSet.getInt("userid");
                int invoicenumber = resultSet.getInt("invoicenumber");
                Date date = resultSet.getDate("date");
                double amount = resultSet.getDouble("amount");
                String type = resultSet.getString("type");
                String status = resultSet.getString("status");
                boolean isAnomalous = resultSet.getBoolean("isanomalous");
                byte[] file = resultSet.getBytes("file");

                Invoice nextInvoice = new Invoice(invoiceid, userid, invoicenumber, date, amount, type, isAnomalous, file);
                nextInvoice.setStatus(status);
                invoices.add(nextInvoice);
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return invoices;
    }

    public ObservableList<Invoice> getSelectedInvoices(String email, Date dateFrom, Date dateTo, String selectedInvoiceType) {
        ObservableList<Invoice> invoices = FXCollections.observableArrayList();

        String sql = "select \"Invoice\".invoicenumber,\"Invoice\".date, \"Invoice\".amount, 2.5, \"Invoice\".type, \"Invoice\".status,\"Invoice\".isanomalous,\"Invoice\".userid, email from \"Invoice\" join \"User\" on \"Invoice\".userid = \"User\".userid where email = \'"+email+"\' AND \"Invoice\".date BETWEEN \'"+dateFrom+"\' AND \'"+dateTo+"\' AND \"Invoice\".type = \'"+selectedInvoiceType+"\';";
        String sqlAllInvoiceTypes = "select \"Invoice\".invoicenumber,\"Invoice\".date, \"Invoice\".amount, 2.5, \"Invoice\".type, \"Invoice\".status,\"Invoice\".isanomalous,\"Invoice\".userid, email from \"Invoice\" join \"User\" on \"Invoice\".userid = \"User\".userid where email = \'"+email+"\' AND \"Invoice\".date BETWEEN \'"+dateFrom+"\' AND \'"+dateTo+"\';";
        String sqlAllUsers = "select \"Invoice\".invoicenumber,\"Invoice\".date, \"Invoice\".amount, 2.5, \"Invoice\".type, \"Invoice\".status,\"Invoice\".isanomalous,\"Invoice\".userid, email from \"Invoice\" join \"User\" on \"Invoice\".userid = \"User\".userid where \"Invoice\".date BETWEEN \'"+dateFrom+"\' AND \'"+dateTo+"\' AND \"Invoice\".type = \'"+selectedInvoiceType+"\';";
        String sqlAllUsersAllTypes = "select \"Invoice\".invoicenumber,\"Invoice\".date, \"Invoice\".amount, 2.5, \"Invoice\".type, \"Invoice\".status,\"Invoice\".isanomalous,\"Invoice\".userid, email from \"Invoice\" join \"User\" on \"Invoice\".userid = \"User\".userid where \"Invoice\".date BETWEEN \'"+dateFrom+"\' AND \'"+dateTo+"\';";
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, DBpassword))
             {
                 PreparedStatement ps;
                 //alle Benutzer und alle Rechnungstypen
                 if (email.equals("alle Benutzer") && selectedInvoiceType.equals("alle Rechnungstypen")) ps = connection.prepareStatement(sqlAllUsersAllTypes);
                 //alle Rechnungstypen
                 else if (selectedInvoiceType.equals("alle Rechnungstypen")) ps = connection.prepareStatement(sqlAllInvoiceTypes);
                 //alle Benutzer
                 else if (email.equals("alle Benutzer")) ps = connection.prepareStatement(sqlAllUsers);
                 //Filter auf Benutzer und Rechnungstypen
                 else ps = connection.prepareStatement(sql);
                 ResultSet resultSet = ps.executeQuery();
                 while (resultSet.next()) {
                    int selectedInvoicenumber = resultSet.getInt("invoicenumber");
                    Date selectedDate = resultSet.getDate("date");
                    double selectedAmount = resultSet.getDouble("amount");
                    String selectedType = resultSet.getString("type");
                    String selectedStatus = resultSet.getString("status");
                    boolean selectedIsAnomalous = resultSet.getBoolean("isanomalous");
                    int selectedUserid = resultSet.getInt("userid");

                    Invoice nextInvoice = new Invoice(selectedInvoicenumber, selectedUserid, selectedInvoicenumber, selectedDate, selectedAmount, selectedType, selectedIsAnomalous, null);
                    nextInvoice.setStatus(selectedStatus);
                    invoices.add(nextInvoice);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        return invoices;
    }

    public ObservableList<Invoice> getSelectedInvoices(User user) {
        ObservableList<Invoice> invoices = FXCollections.observableArrayList();

        String sql = "select \"Invoice\".date, \"Invoice\".amount, email from \"Invoice\" join \"User\" on \"Invoice\".userid = \"User\".userid where email = "+user.getEmail()+";";
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, DBpassword);
             PreparedStatement sps = connection.prepareStatement(sql);
             ResultSet resultSet = sps.executeQuery()) {

            while (resultSet.next()) {
                Date selectedDate = resultSet.getDate("date");
                double selectedAmount = resultSet.getDouble("amount");

                Invoice nextInvoice = new Invoice(user.getUserid(), selectedDate, selectedAmount);
                invoices.add(nextInvoice);
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return invoices;
    }
}