package at.jku.se.lunchify.models;

import at.jku.se.lunchify.LoginController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.io.FileUtils;
import org.springframework.cglib.core.Local;

import javax.print.DocFlavor;
import java.sql.*;
import java.time.LocalDate;

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

        String sql = "select \"Invoice\".invoicenumber,\"Invoice\".date, \"Invoice\".amount, \"Invoice\".reimbursementamount, \"Invoice\".type, \"Invoice\".status,\"Invoice\".isanomalous,\"Invoice\".userid, email from \"Invoice\" join \"User\" on \"Invoice\".userid = \"User\".userid where email = \'"+email+"\' AND \"Invoice\".date BETWEEN \'"+dateFrom+"\' AND \'"+dateTo+"\' AND \"Invoice\".type = \'"+selectedInvoiceType+"\';";
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, DBpassword))
             {
                 PreparedStatement sps = connection.prepareStatement(sql);
                 ResultSet resultSet = sps.executeQuery();
                 while (resultSet.next()) {
                    String selectedInvoicenumber = resultSet.getString("invoicenumber");
                    Date selectedDate = resultSet.getDate("date");
                    double selectedAmount = resultSet.getDouble("amount");
                    double selectedReimbursementAmount = resultSet.getDouble("reimbursementAmount");
                    String selectedType = resultSet.getString("type");
                    String selectedStatus = resultSet.getString("status");
                    boolean selectedIsAnomalous = resultSet.getBoolean("isanomalous");
                    int selectedUserid = resultSet.getInt("userid");
                    int selectedTimesChanged = resultSet.getInt("timesChanged");
                    Invoice nextInvoice = new Invoice(selectedUserid, selectedInvoicenumber, selectedDate, selectedAmount,selectedReimbursementAmount, selectedType, selectedIsAnomalous, null,selectedTimesChanged);
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

        String sql = "select \"Invoice\".date, \"Invoice\".amount,, \"Invoice\".reimbursementamount, email from \"Invoice\" join \"User\" on \"Invoice\".userid = \"User\".userid where email = "+user.getEmail()+";";
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, DBpassword);
             PreparedStatement sps = connection.prepareStatement(sql);
             ResultSet resultSet = sps.executeQuery()) {

            while (resultSet.next()) {
                Date selectedDate = resultSet.getDate("date");
                double selectedAmount = resultSet.getDouble("amount");
                double selectedReimbursementAmount = resultSet.getDouble("reimbursementAmount");
                Invoice nextInvoice = new Invoice(user.getUserid(), selectedDate, selectedAmount,selectedReimbursementAmount);
                invoices.add(nextInvoice);
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return invoices;
    }

    public boolean insertInvoice(Invoice invoice) {
        String sql = "insert into \"Invoice\" (userid, invoicenumber, date, amount, reimbursementamount, type, status, isanomalous, file,timeschanged) values(?,?,?,?,?,?,?,?,?,?);";
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, DBpassword);
             PreparedStatement sps = connection.prepareStatement(sql))
        {
            sps.setInt(1, invoice.getUserid());
            sps.setString(2, invoice.getInvoicenumber());
            sps.setDate(3, (Date) invoice.getDate());
            sps.setDouble(4, invoice.getAmount());
            sps.setDouble(5, invoice.getReimbursementAmount());
            sps.setString(6, invoice.getType());
            sps.setString(7, invoice.getStatus());
            sps.setBoolean(8, invoice.isIsanomalous());
            sps.setBytes(9, invoice.getFile());
            sps.setInt(10, 0);
            sps.executeUpdate();
            sps.close();
            return true;
        }
     catch (Exception e) {
        e.printStackTrace();
        return false;
    }
    }

    public boolean checkInvoicesByDateAndUser(int userid, LocalDate date) {
        String sql = "SELECT userid, date FROM public.\"Invoice\" WHERE userid = ? AND date = ?";
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, DBpassword);
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
}