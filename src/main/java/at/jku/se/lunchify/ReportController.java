package at.jku.se.lunchify;

import at.jku.se.lunchify.models.Invoice;
import at.jku.se.lunchify.models.InvoiceDAO;
import at.jku.se.lunchify.models.User;
import at.jku.se.lunchify.models.UserDAO;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

import static java.sql.Date.valueOf;

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

    protected String selectedMail;
    protected User selectedUser;
    protected Date selectedDateFrom;
    protected Date selectedDateTo;
    protected String selectedInvoiceType;
    protected boolean inputCorrect = false;

    private InvoiceDAO invoiceDAO;
    private UserDAO userDAO;

    String jdbcUrl = "jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:6543/postgres";
    String username = "postgres.yxshntkgvmksefegyfhz";
    String DBpassword = "CaMaKe25!";

    public void initialize() {
        invoiceDAO = new InvoiceDAO();
        userDAO = new UserDAO();
    }

    public void showAllUsers() {
        allUsers.setItems(userDAO.getAllUserMails());
}

    private void setSelectedData () {
        selectedMail = allUsers.getSelectionModel().getSelectedItem();
        selectedUser = userDAO.getUserByEmail(selectedMail);
        System.out.println(selectedMail + ": " + selectedUser.getUserid()); // Test mail und user-id wird zurückgegeben
        selectedDateFrom = valueOf(dateFrom.getValue());
        selectedDateTo = valueOf(dateTo.getValue());
        selectedInvoiceType = invoiceType.getValue();
    }

    private void checkSelectedData () {
        if (selectedMail ==null || selectedDateFrom==null || selectedDateTo==null || selectedInvoiceType==null) {
            warningText.setText("Alle Filter setzen!");
        }
        else if(selectedDateTo.before(selectedDateFrom)) {
            warningText.setText("Bis-Datum darf nicht vor dem Von-Datum liegen!");
        }
        else if(selectedDateTo.after(new Date(2025,4,7))) {  //gehört noch korrekt implementiert -> heute
            warningText.setText("Bis-Datum darf nicht in der Zukunft liegen!");
        }
        //else if(selectedDateTo.getYear()<(LocalDate.now().getYear()-2)) {       //gehört noch korrekt implementiert - genau 12 Monate!
        //    warningText.setText("Auswertung für max. 12 Monate zurück!");
        //}
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("invoiceStatistics-view-TEST.fxml"));
            Parent root = loader.load();
            ReportController controller = loader.getController();
            controller.filterInfo.setText("Rechnungen ("+selectedInvoiceType+") von "+ selectedMail +" (Zeitraum: "+selectedDateFrom.toString()+" bis "+selectedDateTo.toString()+")");
            LunchifyApplication.baseController.basePane.setCenter(root);

            //controller.userEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
            controller.invoiceDate.setCellValueFactory(new PropertyValueFactory<>("date"));
            controller.invoiceAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
            controller.reimbursementAmount.setCellValueFactory(new PropertyValueFactory<>("reimbursementamount"));
            controller.invType.setCellValueFactory(new PropertyValueFactory<>("type"));
            controller.invoiceStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

            //ObservableList<Invoice> invoiceList = invoiceDAO.getSelectedInvoices(selectedUser);
            ObservableList<Invoice> invoiceList = invoiceDAO.getSelectedInvoices(selectedMail,selectedDateFrom,selectedDateTo,selectedInvoiceType);
            //System.out.println(invoiceList);
            controller.invoiceTable.setItems(invoiceList);// Setze die Rechnungen in die TableView

        /*
            // Tabelle konfigurieren (PropertyValueFactory bindet die Columns an die entsprechenden Eigenschaften im Invoice-Objekt)
            controller.userEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
            controller.invoiceDate.setCellValueFactory(new PropertyValueFactory<>("date"));
            controller.invoiceAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
            //controller.reimbursementAmount.setCellValueFactory(new PropertyValueFactory<>("reimbursementAmount"));
            controller.invType.setCellValueFactory(new PropertyValueFactory<>("invType"));
            controller.invoiceStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
         */
        }
    }

}
