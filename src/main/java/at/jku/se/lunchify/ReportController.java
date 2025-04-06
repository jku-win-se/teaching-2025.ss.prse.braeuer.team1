package at.jku.se.lunchify;

import at.jku.se.lunchify.models.Invoice;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReportController {
    @FXML
    protected Label warningText;
    @FXML
    protected ComboBox<String> allUsers;
    @FXML
    protected ChoiceBox<String> invoiceType;
    @FXML
    protected DatePicker dateFrom;
    @FXML
    protected DatePicker dateTo;
    @FXML
    protected Label filterInfo;
    @FXML
    protected TableView<Invoice> invoiceTable;
    @FXML
    protected TableColumn<Invoice, String> userEmail;
    @FXML
    protected TableColumn<Invoice, Date> invoiceDate;
    @FXML
    protected TableColumn<Invoice, Double> invoiceAmount;
    @FXML
    protected TableColumn<Invoice, Double> reimbursementAmount;
    @FXML
    protected TableColumn<Invoice, String> invType;
    @FXML
    protected TableColumn<Invoice, String> invoiceStatus;

    protected String selectedUser;
    protected LocalDate selectedDateFrom;
    protected LocalDate selectedDateTo;
    protected String selectedInvoiceType;
    protected boolean inputCorrect = false;

    String jdbcUrl = "jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:6543/postgres";
    String username = "postgres.yxshntkgvmksefegyfhz";
    String DBpassword = "CaMaKe25!";

    public void showAllUsers() {
        List<String> users = getAllUsers();
        ObservableList<String> userList = FXCollections.observableList(users);
        allUsers.setItems(userList);
    }

    public List<String> getAllUsers() {
        List<String> users = new ArrayList<>();
        users.add("alle Benutzer");

        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, DBpassword)) {
            String sql = "SELECT email FROM \"User\"";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                String userEmail = resultSet.getString("email");
                users.add(userEmail);
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    private void setSelectedData () {
        selectedUser = allUsers.getSelectionModel().getSelectedItem();
        selectedDateFrom = dateFrom.getValue();
        selectedDateTo = dateTo.getValue();
        selectedInvoiceType = invoiceType.getValue();
    }

    private void checkSelectedData () {
        if (selectedUser==null || selectedDateFrom==null || selectedDateTo==null || selectedInvoiceType==null) {
            warningText.setText("Alle Filter setzen!");
        }
        else if(selectedDateTo.isBefore(selectedDateFrom)) {
            warningText.setText("Bis-Datum darf nicht vor dem Von-Datum liegen!");
        }
        else if(selectedDateTo.isAfter(LocalDate.now())) {
            warningText.setText("Bis-Datum darf nicht in der Zukunft liegen!");
        }
        else if(selectedDateTo.getYear()<(LocalDate.now().getYear()-2)) {       //gehört noch korrekt implementiert - genau 12 Monate!
            warningText.setText("Auswertung für max. 12 Monate zurück!");
        }
        else {
            inputCorrect=true;
        }
    }

    public void onInvoiceIndicatorsButtonClick() {
        setSelectedData();
        checkSelectedData();
    //Implementierung offen
        LunchifyApplication.baseController.basePane.setRight(null);
    }

    public void onInvoiceStatisticsButtonClick() throws IOException {
        setSelectedData();
        checkSelectedData();
        if (inputCorrect) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("invoiceStatistics-view.fxml"));
            Parent root = loader.load();
            ReportController controller = loader.getController();
            controller.filterInfo.setText("Rechnungen ("+selectedInvoiceType+") von "+selectedUser+" (Zeitraum: "+selectedDateFrom.toString()+" bis "+selectedDateTo.toString()+")");
            LunchifyApplication.baseController.basePane.setCenter(root);
            ObservableList<Invoice> invoiceList = FXCollections.observableArrayList();
            try (Connection connection = DriverManager.getConnection(jdbcUrl, username, DBpassword)) {
                String sql = "select \"Invoice\".date, \"Invoice\".amount, 2.5, \"Invoice\".type, \"Invoice\".status, email from \"Invoice\" join \"User\" on \"Invoice\".userid = \"User\".userid where email = "+selectedUser+" AND \"Invoice\".date BETWEEN "+dateFrom+" AND "+dateTo+" AND \"Invoice\".type = "+selectedInvoiceType+";";
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql);

                while (resultSet.next()) {
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
