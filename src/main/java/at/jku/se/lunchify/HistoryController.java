package at.jku.se.lunchify;

import at.jku.se.lunchify.models.Invoice;
import at.jku.se.lunchify.models.InvoiceDAO;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;

//Klasse zum Bearbeiten der eigenen Rechnungen
public class HistoryController {
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
    protected TableColumn<Invoice, String> status;

    private InvoiceDAO invoiceDAO;

    public void initialize() {
        showMyInvoices();
    }

    public void showMyInvoices() {
        invoiceDAO = new InvoiceDAO();
        invoiceDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        invoiceAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        reimbursementAmount.setCellValueFactory(new PropertyValueFactory<>("reimbursementAmount"));
        invType.setCellValueFactory(new PropertyValueFactory<>("type"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));

        ObservableList<Invoice> invoiceList = invoiceDAO.getSelectedInvoicesToEdit();
        invoiceTable.setItems(invoiceList);// Setze die Rechnungen in die TableView

        //AI assisted
        invoiceTable.setRowFactory(tableView -> {
            //nur klickbar, wenn Rechnung im aktuellen Monat liegt
            TableRow<Invoice> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && row.getItem().getDate().getMonth() == LocalDate.now().getMonthValue()-1) {
                    Invoice invoice = row.getItem();
                    Invoice selectedInvoice = invoiceDAO.getInvoiceById(invoice.getInvoiceid());
                    showInvoiceDetails(selectedInvoice);
                }
            });

            //grau einfärben, wenn nicht im aktuellen Monat
            row.itemProperty().addListener((observable, oldValue, newValue) -> {
                if (row.getItem() != null) {
                    if (row.getItem().getDate().getMonth() != LocalDate.now().getMonthValue() - 1) {
                        row.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: gray");
                    } else {
                        row.setStyle("");
                    }
                }
            });
            return row;
        });
    }

    private void showInvoiceDetails(Invoice invoice) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("myInvoiceDetail-view.fxml"));
            Parent root = loader.load();

            // AI-generated: Detail-Controller holen und Daten übergeben
            MyInvoiceDetailController controller = loader.getController();
            controller.setInvoice(invoice); // Übergabe-Methode im Detail-Controller
            Stage stage = new Stage();
            stage.setTitle("Rechnungsdetails");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(invoiceTable.getScene().getWindow());
            stage.setOnHidden(event -> {
                // Tabelle aktualisieren
                showMyInvoices();
            });
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
