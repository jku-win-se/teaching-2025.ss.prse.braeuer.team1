package at.jku.se.lunchify.models;

import at.jku.se.lunchify.LoginController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;

public class InvoiceDAO {
    String jdbcUrl = "jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:6543/postgres";
    String username = "postgres.yxshntkgvmksefegyfhz";
    String dbPassword = "CaMaKe25!";

    InvoiceSettingService invoiceSettingService = new InvoiceSettingService();

    public boolean checkDateInPast(LocalDate date) {
        return !date.isAfter(LocalDate.now());
    }

    public boolean checkInvoiceValueIsPositive(Double value) {
        return value > 0;
    }

    // Methode zum Abrufen aller Rechnungen
    public ObservableList<Invoice> getAllInvoices() {
        ObservableList<Invoice> invoices = FXCollections.observableArrayList();

        String sql = "SELECT * FROM \"Invoice\"";
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, dbPassword);
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

    public ObservableList<Invoice> getSelectedInvoices(String email, Date dateFrom, Date dateTo, String invoiceType) throws SQLException {
        ObservableList<Invoice> invoices = FXCollections.observableArrayList();
        String selectedEmail = email;
        String selectedInvoiceType = invoiceType;
        if (selectedEmail.equals("alle Benutzer")) selectedEmail = null;
        if (selectedInvoiceType.equals("alle Rechnungstypen")) selectedInvoiceType = null;
        String sql = "SELECT * "+
                "FROM \"Invoice\" " +
                "JOIN \"User\" ON \"Invoice\".userid = \"User\".userid " +
                "WHERE (? IS NULL OR email = ?) " +
                "AND \"Invoice\".date BETWEEN ? AND ? " +
                "AND (? IS NULL OR \"Invoice\".type = ?)";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, dbPassword)) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, selectedEmail);                    // IS NULL
            ps.setString(2, selectedEmail);            // Vergleich
            ps.setDate(3, dateFrom);                   // Von
            ps.setDate(4, dateTo);                     // Bis
            ps.setString(5, selectedInvoiceType);      // IS NULL
            ps.setString(6, selectedInvoiceType);      // Vergleich

            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                int selectedInvoiceid = resultSet.getInt("invoiceid");
                String selectedInvoicenumber = resultSet.getString("invoicenumber");
                Date selectedDate = resultSet.getDate("date");
                double selectedAmount = resultSet.getDouble("amount");
                double selectedReimbursementAmount = resultSet.getDouble("reimbursementAmount");
                String selectedType = resultSet.getString("type");
                String selectedStatus = resultSet.getString("status");
                boolean selectedIsAnomalous = resultSet.getBoolean("isanomalous");
                int selectedUserid = resultSet.getInt("userid");
                byte[] selectedFile = resultSet.getBytes("file");
                int selectedTimesChanged = resultSet.getInt("timesChanged");
                Invoice nextInvoice = new Invoice(selectedInvoiceid, selectedUserid, selectedInvoicenumber, selectedDate, selectedAmount, selectedReimbursementAmount, selectedType, selectedIsAnomalous, selectedFile, selectedTimesChanged);
                nextInvoice.setStatus(selectedStatus);
                invoices.add(nextInvoice);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return invoices;
    }

    public ObservableList<Invoice> getAnomalousSelectedInvoices(String email, Date dateFrom, Date dateTo, String invoiceType) throws SQLException {
        ObservableList<Invoice> invoices = FXCollections.observableArrayList();
        String selectedEmail = email;
        String selectedInvoiceType = invoiceType;
        if (selectedEmail.equals("alle Benutzer")) selectedEmail = null;
        if (selectedInvoiceType.equals("alle Rechnungstypen")) selectedInvoiceType = null;
        String sql = "SELECT * "+
                "FROM \"Invoice\" " +
                "JOIN \"User\" ON \"Invoice\".userid = \"User\".userid " +
                "WHERE (? IS NULL OR email = ?) " +
                "AND \"Invoice\".date BETWEEN ? AND ? " +
                "AND (? IS NULL OR \"Invoice\".type = ?)" +
                "AND \"Invoice\".isanomalous = TRUE";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, dbPassword)) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, selectedEmail);                    // IS NULL
            ps.setString(2, selectedEmail);            // Vergleich
            ps.setDate(3, dateFrom);                   // Von
            ps.setDate(4, dateTo);                     // Bis
            ps.setString(5, selectedInvoiceType);      // IS NULL
            ps.setString(6, selectedInvoiceType);      // Vergleich

            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                int selectedInvoiceid = resultSet.getInt("invoiceid");
                String selectedInvoicenumber = resultSet.getString("invoicenumber");
                Date selectedDate = resultSet.getDate("date");
                double selectedAmount = resultSet.getDouble("amount");
                double selectedReimbursementAmount = resultSet.getDouble("reimbursementAmount");
                String selectedType = resultSet.getString("type");
                String selectedStatus = resultSet.getString("status");
                boolean selectedIsAnomalous = resultSet.getBoolean("isanomalous");
                int selectedUserid = resultSet.getInt("userid");
                byte[] selectedFile = resultSet.getBytes("file");
                int selectedTimesChanged = resultSet.getInt("timesChanged");
                Invoice nextInvoice = new Invoice(selectedInvoiceid, selectedUserid, selectedInvoicenumber, selectedDate, selectedAmount, selectedReimbursementAmount, selectedType, selectedIsAnomalous, selectedFile, selectedTimesChanged);
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

        String sql = "SELECT \"Invoice\".invoiceid, \"Invoice\".invoicenumber, \"Invoice\".date, \"Invoice\".amount, \"Invoice\".reimbursementamount, \"Invoice\".type, \"Invoice\".timeschanged, \"User\".userid, \"User\".surname, \"User\".firstname " +
                "FROM \"Invoice\" " +
                "JOIN \"User\" ON \"Invoice\".userid = \"User\".userid " +
                "WHERE (? IS NULL OR \"User\".email = ? )" +
                "AND \"Invoice\".status = ? " +
                "AND \"Invoice\".isanomalous = ? " +
                "AND \"Invoice\".userid <> ?"; //ADMIN kann seine eigenen Rechnungen nicht freigeben
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, dbPassword);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, email);
            ps.setString(3, status);
            ps.setBoolean(4, anomalous);
            ps.setInt(5, LoginController.currentUserId);
            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                int selectedInvoiceid = resultSet.getInt("invoiceid");
                Date selectedDate = resultSet.getDate("date");
                String selectedInvoicenumber = resultSet.getString("invoicenumber");
                double selectedAmount = resultSet.getDouble("amount");
                double selectedReimbursementAmount = resultSet.getDouble("reimbursementAmount");
                int selectedUserid = resultSet.getInt("userid");
                String selectedType = resultSet.getString("type");
                int selectedTimesChanged = resultSet.getInt("timeschanged");
                Invoice nextInvoice = new Invoice(selectedInvoiceid, selectedUserid, selectedInvoicenumber, selectedDate, selectedAmount, selectedReimbursementAmount, selectedType, true, null, selectedTimesChanged);
                invoices.add(nextInvoice);
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return invoices;
    }

    public ObservableList<Invoice> getSelectedInvoicesToEdit() {
        ObservableList<Invoice> invoices = FXCollections.observableArrayList();
        String sql = "SELECT \"Invoice\".invoiceid, \"Invoice\".date, \"Invoice\".amount, \"Invoice\".reimbursementamount, \"Invoice\".type, \"Invoice\".status " +
                "FROM \"Invoice\" " +
                "WHERE \"Invoice\".userid = ? " +
                "ORDER BY \"Invoice\".date DESC";
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, dbPassword);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, LoginController.currentUserId);
            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                int selectedInvoiceid = resultSet.getInt("invoiceid");
                Date selectedDate = resultSet.getDate("date");
                double selectedAmount = resultSet.getDouble("amount");
                double selectedReimbursementAmount = resultSet.getDouble("reimbursementamount");
                String selectedType = resultSet.getString("type");
                String selectedStatus = resultSet.getString("status");
                Invoice nextInvoice = new Invoice(selectedInvoiceid, selectedDate, selectedAmount, selectedReimbursementAmount, selectedType, selectedStatus);
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
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, dbPassword);
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

    public boolean setInvoiceStatus(int id, String newStatus) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, dbPassword);
             PreparedStatement ps = connection.prepareStatement("update \"Invoice\" SET status = ? where invoiceid = ?;")) {
            ps.setString(1, newStatus);
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

    public boolean checkInvoicesByDateAndUser(int userid, LocalDate date) {
        String sql = "SELECT userid, date FROM public.\"Invoice\" WHERE userid = ? AND date = ?";
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, dbPassword);
             PreparedStatement sps = connection.prepareStatement(sql)) {
            sps.setInt(1,userid);
            sps.setDate(2,Date.valueOf(date));
            ResultSet resultSet = sps.executeQuery();
            if (resultSet.next()) {
                return true;
            }
            else{
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertInvoice(Invoice invoice) {
        String sql = "insert into \"Invoice\" (userid, invoicenumber, date, amount, reimbursementamount, type, status, isanomalous, file,timeschanged) values(?,?,?,?,?,?,?,?,?,?);";
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, dbPassword);
             PreparedStatement sp = connection.prepareStatement(sql))
        {
            sp.setInt(1, invoice.getUserid());
            sp.setString(2, invoice.getInvoicenumber());
            sp.setDate(3, (Date) invoice.getDate());
            sp.setDouble(4, invoice.getAmount());
            sp.setDouble(5, invoice.getReimbursementAmount());
            sp.setString(6, invoice.getType());
            sp.setString(7, invoice.getStatus());
            sp.setBoolean(8, invoice.isIsanomalous());
            sp.setBytes(9, invoice.getFile());
            sp.setInt(10, 0);
            sp.executeUpdate();
            sp.close();
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteInvoice(int id) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, dbPassword);
             PreparedStatement ps = connection.prepareStatement("delete FROM \"Invoice\" where invoiceid = ?;")) {
            ps.setInt(1, id);
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

    public boolean updateInvoice(Invoice invoice) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, dbPassword);
             PreparedStatement ps = connection.prepareStatement("update \"Invoice\" SET type = ?, invoicenumber = ?, date = ?, amount = ? , reimbursementamount = ? where invoiceid = ?;")) {
            ps.setString(1, invoice.getType());
            ps.setString(2, invoice.getInvoicenumber());
            ps.setDate(3, (Date) invoice.getDate());
            ps.setDouble(4, invoice.getAmount());
            ps.setDouble(5, invoice.getReimbursementAmount());
            ps.setInt(6, invoice.getInvoiceid());
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