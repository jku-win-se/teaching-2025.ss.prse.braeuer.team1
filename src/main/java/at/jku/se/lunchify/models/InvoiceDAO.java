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
                String invoicenumber = resultSet.getString("invoicenumber");
                Date date = resultSet.getDate("date");
                double amount = resultSet.getDouble("amount");
                double reimbursementAmount = resultSet.getDouble("reimbursementAmount");
                String type = resultSet.getString("type");
                String status = resultSet.getString("status");
                boolean isAnomalous = resultSet.getBoolean("isanomalous");
                byte[] file = resultSet.getBytes("file");
                int timesChanged = resultSet.getInt("timesChanged");

                Invoice nextInvoice = new Invoice(invoiceid, userid, invoicenumber, date, amount, reimbursementAmount, type, isAnomalous, file, timesChanged);
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

        String sql = "select \"Invoice\".invoiceid, \"Invoice\".invoicenumber,\"Invoice\".date, \"Invoice\".amount, \"Invoice\".reimbursementamount, \"Invoice\".type, \"Invoice\".status,\"Invoice\".isanomalous,\"Invoice\".userid, \"Invoice\".file, \"Invoice\".timesChanged, email from \"Invoice\" join \"User\" on \"Invoice\".userid = \"User\".userid where email = \'"+email+"\' AND \"Invoice\".date BETWEEN \'"+dateFrom+"\' AND \'"+dateTo+"\' AND \"Invoice\".type = \'"+selectedInvoiceType+"\';";
        String sqlAllInvoiceTypes = "select \"Invoice\".invoiceid, \"Invoice\".invoicenumber,\"Invoice\".date, \"Invoice\".amount, \"Invoice\".reimbursementamount, \"Invoice\".type, \"Invoice\".status,\"Invoice\".isanomalous,\"Invoice\".userid, \"Invoice\".file, \"Invoice\".timesChanged, email from \"Invoice\" join \"User\" on \"Invoice\".userid = \"User\".userid where email = \'"+email+"\' AND \"Invoice\".date BETWEEN \'"+dateFrom+"\' AND \'"+dateTo+"\';";
        String sqlAllUsers = "select \"Invoice\".invoiceid, \"Invoice\".invoicenumber,\"Invoice\".date, \"Invoice\".amount, \"Invoice\".reimbursementamount, \"Invoice\".type, \"Invoice\".status,\"Invoice\".isanomalous,\"Invoice\".userid, \"Invoice\".file, \"Invoice\".timesChanged, email from \"Invoice\" join \"User\" on \"Invoice\".userid = \"User\".userid where \"Invoice\".date BETWEEN \'"+dateFrom+"\' AND \'"+dateTo+"\' AND \"Invoice\".type = \'"+selectedInvoiceType+"\';";
        String sqlAllUsersAllTypes = "select \"Invoice\".invoiceid, \"Invoice\".invoicenumber,\"Invoice\".date, \"Invoice\".amount, \"Invoice\".reimbursementamount, \"Invoice\".type, \"Invoice\".status,\"Invoice\".isanomalous,\"Invoice\".userid, \"Invoice\".file, \"Invoice\".timesChanged, email from \"Invoice\" join \"User\" on \"Invoice\".userid = \"User\".userid where \"Invoice\".date BETWEEN \'"+dateFrom+"\' AND \'"+dateTo+"\';";
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
                    String selectedInvoicenumber = resultSet.getString("invoicenumber");
                    Date selectedDate = resultSet.getDate("date");
                    int selectedInvoiceid = resultSet.getInt("invoiceid");
                    double selectedAmount = resultSet.getDouble("amount");
                    double selectedReimbursementAmount = resultSet.getDouble("reimbursementAmount");
                    String selectedType = resultSet.getString("type");
                    String selectedStatus = resultSet.getString("status");
                    boolean selectedIsAnomalous = resultSet.getBoolean("isanomalous");
                    int selectedUserid = resultSet.getInt("userid");
                    byte[] selectedFile = resultSet.getBytes("file");
                    int selectedTimesChanged = resultSet.getInt("timesChanged");
                    Invoice nextInvoice = new Invoice(selectedInvoiceid, selectedUserid, selectedInvoicenumber, selectedDate, selectedAmount,selectedReimbursementAmount, selectedType, selectedIsAnomalous, selectedFile, selectedTimesChanged);
                    nextInvoice.setStatus(selectedStatus);
                    invoices.add(nextInvoice);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        return invoices;
    }

    public ObservableList<Invoice> getSelectedInvoicesToClear(String email, String status, boolean anomalous) {
        ObservableList<Invoice> invoices = FXCollections.observableArrayList();

        String sql = "SELECT \"Invoice\".invoiceid, \"Invoice\".date, \"Invoice\".amount, \"Invoice\".reimbursementamount, \"Invoice\".type, \"Invoice\".timeschanged, \"User\".userid, \"User\".surname, \"User\".firstname " +
                "FROM \"Invoice\" " +
                "JOIN \"User\" ON \"Invoice\".userid = \"User\".userid " +
                "WHERE (? IS NULL OR \"User\".email = ? )" +
                "AND \"Invoice\".status = ? " +
                "AND \"Invoice\".isanomalous = ?;";
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, DBpassword);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, email);
            ps.setString(3, status);
            ps.setBoolean(4, anomalous);
            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                int selectedInvoiceid = resultSet.getInt("invoiceid");
                Date selectedDate = resultSet.getDate("date");
                double selectedAmount = resultSet.getDouble("amount");
                double selectedReimbursementAmount = resultSet.getDouble("reimbursementAmount");
                int selectedUserid = resultSet.getInt("userid");
                String selectedType = resultSet.getString("type");
                int selectedTimesChanged = resultSet.getInt("timeschanged");
                Invoice nextInvoice = new Invoice(selectedInvoiceid, selectedUserid, selectedDate, selectedAmount, selectedReimbursementAmount, selectedType, selectedTimesChanged);
                invoices.add(nextInvoice);
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return invoices;
    }

    public Invoice getInvoiceById (int id) {
        Invoice invoice = null;
        String sql = "SELECT * FROM \"Invoice\" WHERE \"Invoice\".invoiceid = ?";
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, DBpassword);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    int selectedInvoiceid = resultSet.getInt("invoiceid");
                    String selectedInvoicenumber = resultSet.getString("invoicenumber");
                    int selectedUserid = resultSet.getInt("userid");
                    Date selectedDate = resultSet.getDate("date");
                    double selectedAmount = resultSet.getDouble("amount");
                    double selectedReimbursementAmount = resultSet.getDouble("reimbursementAmount");
                    String selectedType = resultSet.getString("type");
                    String selectedStatus = resultSet.getString("status");
                    boolean selectedIsAnomalous = resultSet.getBoolean("isanomalous");
                    int selectedTimesChanged = resultSet.getInt("timesChanged");
                    byte[] selectedFile = resultSet.getBytes("file");
                    connection.close();

                    invoice = new Invoice(selectedInvoiceid, selectedUserid, selectedInvoicenumber, selectedDate, selectedAmount, selectedReimbursementAmount, selectedType, selectedIsAnomalous, selectedFile, selectedTimesChanged);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return invoice;
    }

    public boolean clearInvoice(int id) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, DBpassword);
            PreparedStatement ps = connection.prepareStatement("update \"Invoice\" SET status = ? where invoiceid = ?;")) {
            ps.setString(1, "genehmigt");
            ps.setInt(2, id);
            ps.executeUpdate();
            ps.close();
            connection.close();
            return true;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}