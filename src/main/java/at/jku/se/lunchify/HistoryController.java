package at.jku.se.lunchify;

import at.jku.se.lunchify.models.Invoice;
import at.jku.se.lunchify.models.InvoiceDAO;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;

/**
 * Testing nicht sinnvoll, befüllt Tabelle mit Werten aus dem InvoiceDAO
 * Klasse zum Bearbeiten der eigenen Rechnungen
 */

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
    @FXML
    protected TableColumn<Invoice, String> invoiceRequestDate;
    @FXML
    protected PieChart chartTypeDistribution;

    private InvoiceDAO invoiceDAO;
    int restaurantCount;
    int supermarketCount;

    public void initialize() {
        invoiceTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        showMyInvoices();
        loadPieChart();
    }

    public void showMyInvoices() {
        invoiceDAO = new InvoiceDAO();
        invoiceDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        invoiceAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        reimbursementAmount.setCellValueFactory(new PropertyValueFactory<>("reimbursementAmount"));
        invType.setCellValueFactory(new PropertyValueFactory<>("type"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        invoiceRequestDate.setCellValueFactory(new PropertyValueFactory<>("requestDate"));

        ObservableList<Invoice> invoiceList = invoiceDAO.getSelectedInvoicesToEdit();
        invoiceTable.setItems(invoiceList); // Setze die Rechnungen in die TableView

        //AI-assisted
        supermarketCount = (int) invoiceList.stream()
                .filter(inv -> "Supermarkt".equalsIgnoreCase(inv.getType()))
                .count();

        restaurantCount = (int) invoiceList.stream()
                .filter(inv -> "Restaurant".equalsIgnoreCase(inv.getType()))
                .count();

        //AI assisted
        invoiceTable.setRowFactory(tableView -> {
            //nur klickbar, wenn Rechnung im aktuellen Monat liegt und nicht genehmigt ist
            TableRow<Invoice> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && row.getItem().getDate().getMonth() == LocalDate.now().getMonthValue() - 1 && !row.getItem().getStatus().equals(String.valueOf(Invoice.Invoicestatus.GENEHMIGT))) {
                    Invoice invoice = row.getItem();
                    Invoice selectedInvoice = invoiceDAO.getInvoiceById(invoice.getInvoiceid());
                    showInvoiceDetails(selectedInvoice);
                }
            });

            //grau einfärben, wenn nicht im aktuellen Monat und wenn bereits genehmigt
            row.itemProperty().addListener((observable, oldValue, newValue) -> {
                if (row.getItem() != null) {
                    if (row.getItem().getDate().getMonth() != LocalDate.now().getMonthValue() - 1) {
                        row.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: gray");
                    } else if (row.getItem().getStatus().equals(String.valueOf(Invoice.Invoicestatus.GENEHMIGT))) {
                        row.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: gray");
                    } else {
                        row.setStyle("");
                    }
                }
            });
            return row;
        });
    }

    public void loadPieChart() {
        int total = supermarketCount + restaurantCount;
        // PieChart.Data mit Anzahl und Prozent
        PieChart.Data supermarket = new PieChart.Data(
                String.format("Supermarkt: %d (%.1f%%)", supermarketCount, 100.0 * supermarketCount / total),
                supermarketCount
        );
        PieChart.Data restaurant = new PieChart.Data(
                String.format("Restaurant: %d (%.1f%%)", restaurantCount, 100.0 * restaurantCount / total),
                restaurantCount
        );

        // Hinzufügen zum Chart
        chartTypeDistribution.getData().clear();
        chartTypeDistribution.getData().addAll(supermarket, restaurant);

        //Farbe für Pie-Chart
        supermarket.getNode().setStyle("-fx-pie-color: #10e1c3;");
        restaurant.getNode().setStyle("-fx-pie-color: #a0e196;");

        for (Node legend : chartTypeDistribution.lookupAll(".chart-legend-item")) {
            if (legend instanceof Label label) {
                if (label.getText().startsWith("Supermarkt")) {
                    label.getGraphic().setStyle("-fx-background-color: #10e1c3;");
                } else if (label.getText().startsWith("Restaurant")) {
                    label.getGraphic().setStyle("-fx-background-color: #a0e196;");
                }
            }
        }
    }

    private void showInvoiceDetails (Invoice invoice){
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
