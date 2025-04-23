package at.jku.se.lunchify;

import at.jku.se.lunchify.models.Invoice;
import at.jku.se.lunchify.models.InvoiceDAO;
import at.jku.se.lunchify.models.UserDAO;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Date;

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
    protected TableColumn<Invoice, String> userid;
    @FXML
    protected TableColumn<Invoice, String> invoiceid;
    @FXML
    protected TableColumn<Invoice, Integer> timesChanged;

    private InvoiceDAO invoiceDAO;
    private UserDAO userDAO;

    protected String selectedMail;

    public void initialize() {
        invoiceDAO = new InvoiceDAO();
        userDAO = new UserDAO();
        allUsers.setValue("alle Benutzer");
        userSelectionChanged();

        //AI assisted
        invoiceTable.setRowFactory(tableView -> {
            TableRow<Invoice> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    Invoice invoice = row.getItem();
                    Invoice selectedInvoice = invoiceDAO.getInvoiceById(invoice.getInvoiceid());
                    showInvoiceDetails(selectedInvoice);
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
        if (selectedMail.equals("alle Benutzer")) selectedMail = null;
    }

    public void userSelectionChanged() {
        setSelectedData();

        invoiceid.setCellValueFactory(new PropertyValueFactory<>("invoiceid"));
        userid.setCellValueFactory(new PropertyValueFactory<>("userid"));
        invoiceDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        invoiceAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        reimbursementAmount.setCellValueFactory(new PropertyValueFactory<>("reimbursementAmount"));
        invType.setCellValueFactory(new PropertyValueFactory<>("type"));
        timesChanged.setCellValueFactory(new PropertyValueFactory<>("timesChanged"));


        ObservableList<Invoice> invoiceList = invoiceDAO.getSelectedInvoicesToClear(selectedMail, "eingereicht", true);
        invoiceTable.setItems(invoiceList);// Setze die Rechnungen in die TableView
    }

    private void showInvoiceDetails(Invoice invoice) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("invoiceDetail-view.fxml"));
            Parent root = loader.load();

            // AI-generated: Detail-Controller holen und Daten übergeben
            InvoiceDetailController controller = loader.getController();
            controller.setInvoice(invoice); // Übergabe-Methode im Detail-Controller
            Stage stage = new Stage();
            stage.setTitle("Rechnungsdetails");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(invoiceTable.getScene().getWindow());
            stage.setOnHidden(event -> {
                // Tabelle aktualisieren
                userSelectionChanged();
            });
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
