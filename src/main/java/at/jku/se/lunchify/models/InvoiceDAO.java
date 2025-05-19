package at.jku.se.lunchify.models;

import at.jku.se.lunchify.LoginController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Testing nicht sinnvoll bei DB-Zugriffen, welche Mengen zurückgeben
 */

public class InvoiceDAO {

    /**
     * Database credentals for the Supabase PostgreSQL-Database
     */
    String jdbcUrl = "jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:6543/postgres";
    String username = "postgres.yxshntkgvmksefegyfhz";
    String dbPassword = "CaMaKe25!";

    /**
     * Compares if the given date is in the past
     * <p>
     * This function returns a booolean in regard to a comparison to the current date
     * <p>
     * @param date  checks if date is in the past, comparing to current date
     * @return true if date is in the past, false if the date is in the future
     */
    public boolean checkDateInPast(LocalDate date) {
        return !date.isAfter(LocalDate.now());
    }

    /**
     * Compares if the given value is positive
     * <p>
     * This function returns a booolean in regard to if the value is positive
     * <p>
     * @param value  checks if value is positive or negative
     * @return true if value is positive, false if value is negative
     */
    public boolean checkInvoiceValueIsPositive(Double value) {
        return value > 0;
    }

    /**
     * Returns a list of all invoices in the database
     * <p>
     * This method return an ObservableList<Invoice> of all invoices in the database
     * <p>
     * @return ObservableList<Invoice> if successful, stacktrace if an Exception emerges
     */
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
                Date requestDate = resultSet.getDate("requestDate");

                Invoice nextInvoice = new Invoice(invoiceid, userid, invoicenumber, date, amount, reimbursementAmount, type, isAnomalous, file, timesChanged, requestDate);
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
                Date selectedRequestDate = resultSet.getDate("requestDate");
                Invoice nextInvoice = new Invoice(selectedInvoiceid, selectedUserid, selectedInvoicenumber, selectedDate, selectedAmount, selectedReimbursementAmount, selectedType, selectedIsAnomalous, selectedFile, selectedTimesChanged, selectedRequestDate);
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
                Date selectedRequestDate = resultSet.getDate("requestDate");
                Invoice nextInvoice = new Invoice(selectedInvoiceid, selectedUserid, selectedInvoicenumber, selectedDate, selectedAmount, selectedReimbursementAmount, selectedType, selectedIsAnomalous, selectedFile, selectedTimesChanged, selectedRequestDate);
                nextInvoice.setStatus(selectedStatus);
                invoices.add(nextInvoice);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return invoices;
    }

    public Map<Integer, Double> getReimbursementSumPerUser() {
        int userid;
        double amount;
        Map<Integer, Double> reimbursementPerUser = new HashMap<>();
        String sql = "select userid, sum(reimbursementamount) as \"risum\" from \"Invoice\" where date >= date_trunc('month', current_date)\n" +
                "  AND date < date_trunc('month', current_date + interval '1 month') group by userid";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, dbPassword)) {

            ResultSet resultSet = connection.createStatement().executeQuery(sql);
            while (resultSet.next()) {
                userid = resultSet.getInt("userid");
                amount = resultSet.getDouble("risum");
                reimbursementPerUser.put(userid, amount);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reimbursementPerUser;
    }

    public ObservableList<Invoice> getSelectedInvoicesToClear(String email, String status, boolean anomalous) {
        ObservableList<Invoice> invoices = FXCollections.observableArrayList();

        String sql = "SELECT \"Invoice\".invoiceid, \"Invoice\".invoicenumber, \"Invoice\".date, \"Invoice\".amount, \"Invoice\".reimbursementamount, \"Invoice\".type, \"Invoice\".timeschanged, \"Invoice\".requestdate, \"User\".userid, \"User\".surname, \"User\".firstname " +
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
                Date selectedRequestDate = resultSet.getDate("requestDate");
                Invoice nextInvoice = new Invoice(selectedInvoiceid, selectedUserid, selectedInvoicenumber, selectedDate, selectedAmount, selectedReimbursementAmount, selectedType, true, null, selectedTimesChanged, selectedRequestDate);
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
        String sql = "SELECT \"Invoice\".invoiceid, \"Invoice\".date, \"Invoice\".amount, \"Invoice\".reimbursementamount, \"Invoice\".type, \"Invoice\".status, \"Invoice\".requestdate " +
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
                Date selectedRequestDate = resultSet.getDate("requestDate");
                Invoice nextInvoice = new Invoice(selectedInvoiceid, selectedDate, selectedAmount, selectedReimbursementAmount, selectedType, selectedStatus,selectedRequestDate);
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
                    Date selectedRequestDate = resultSet.getDate("requestDate");
                    connection.close();

                    invoice = new Invoice(selectedInvoiceid, selectedUserid, selectedInvoicenumber, selectedDate, selectedAmount, selectedReimbursementAmount, selectedType, selectedStatus, selectedIsAnomalous, selectedFile, selectedTimesChanged,selectedRequestDate);
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
        String sql = "insert into \"Invoice\" (userid, invoicenumber, date, amount, reimbursementamount, type, status, isanomalous, file,timeschanged, requestdate) values(?,?,?,?,?,?,?,?,?,?,?);";
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, dbPassword);
             PreparedStatement sp = connection.prepareStatement(sql))
        {
            sp.setInt(1, invoice.getUserid());
            sp.setString(2, invoice.getInvoicenumber());
            sp.setDate(3, (Date) invoice.getDate());
            sp.setDouble(4, invoice.getAmount());
            sp.setDouble(5, invoice.getReimbursementAmount());
            sp.setString(6, invoice.getType());
            if(invoice.isIsanomalous())
            {
                sp.setString(7, invoice.getStatus());
            }
            else
            {
                sp.setString(7, "GENEHMIGT");
            }
            sp.setBoolean(8, invoice.isIsanomalous());
            sp.setBytes(9, invoice.getFile());
            sp.setInt(10, 0);
            sp.setDate(11, (Date) invoice.getRequestDate());
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
        //wenn aktueller User -> timesChanged wird erhöht (nicht, wenn Admin ändert)
        if(LoginController.currentUserId == invoice.getUserid()) invoice.setTimesChanged(invoice.getTimesChanged()+1);
        //Status wird wieder auf eingereicht gesetzt
        invoice.setStatus(String.valueOf(Invoice.Invoicestatus.EINGEREICHT));

        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, dbPassword);
             PreparedStatement ps = connection.prepareStatement("update \"Invoice\" SET type = ?, invoicenumber = ?, date = ?, amount = ? , reimbursementamount = ?, timeschanged = ?, status = ? where invoiceid = ?;")) {
            ps.setString(1, invoice.getType());
            ps.setString(2, invoice.getInvoicenumber());
            ps.setDate(3, (Date) invoice.getDate());
            ps.setDouble(4, invoice.getAmount());
            ps.setDouble(5, invoice.getReimbursementAmount());
            ps.setInt(6, invoice.getTimesChanged());
            ps.setString(7, invoice.getStatus());
            ps.setInt(8, invoice.getInvoiceid());
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