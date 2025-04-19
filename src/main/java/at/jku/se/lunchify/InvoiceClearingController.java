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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static java.sql.Date.valueOf;

public class InvoiceClearingController {
    @FXML
    protected ComboBox<String> allUsers;
    @FXML
    protected TableView<Invoice> invoiceTable;
    @FXML
    protected TableColumn<Invoice, Date> invoiceDate;
    @FXML
    protected TableColumn<Invoice, Double> invoiceAmount;
    @FXML
    protected TableColumn<Invoice, Double> reimbursementAmount;
    @FXML
    protected TableColumn<Invoice, String> invType;
    @FXML
    protected TableColumn<Invoice, String> email;

    protected String selectedMail;
    protected User selectedUser;

    private InvoiceDAO invoiceDAO;
    private UserDAO userDAO;

    public void initialize() {
        invoiceDAO = new InvoiceDAO();
        userDAO = new UserDAO();

        invoiceTable.setRowFactory(tableView -> {
            TableRow<Invoice> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                Invoice invoice = row.getItem();
                //Hier soll Rechnungsdetail angezeigt werden
                    System.out.println("Rechnung iHv:" + invoice.getAmount());
                }
            });
            return row;
        });
    }

    public void showAllUsers() {
        allUsers.setItems(userDAO.getAllUserMailsWithAll());
}

    private void setSelectedData () {
        selectedMail = allUsers.getSelectionModel().getSelectedItem();
        selectedUser = userDAO.getUserByEmail(selectedMail);
    }


    public void userSelectionChanged() throws IOException {
        setSelectedData();

        //userEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        email.setCellValueFactory(new PropertyValueFactory<>("email"));
        invoiceDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        invoiceAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        reimbursementAmount.setCellValueFactory(new PropertyValueFactory<>("reimbursementAmount"));
        invType.setCellValueFactory(new PropertyValueFactory<>("type"));


        ObservableList<Invoice> invoiceList = invoiceDAO.getSelectedInvoicesToClear(selectedUser, "eingereicht", true);
        invoiceTable.setItems(invoiceList);// Setze die Rechnungen in die TableView
    }
}
