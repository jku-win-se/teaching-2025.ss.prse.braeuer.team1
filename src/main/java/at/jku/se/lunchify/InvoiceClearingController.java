package at.jku.se.lunchify;

import at.jku.se.lunchify.models.Invoice;
import at.jku.se.lunchify.models.InvoiceDAO;
import at.jku.se.lunchify.models.UserDAO;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;


/**
 * Testing nicht sinnvoll, befüllt Tabelle mit Werten aus dem InvoiceDAO
 * Klasse zum Freigeben der Rechnungen
 */

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
    @FXML
    protected TableColumn<Invoice, String> invoiceRequestDate;
    @FXML
    protected Button exportPayrollDataJSONButton;
    @FXML
    protected Button exportPayrollDataXMLButton;

    private InvoiceDAO invoiceDAO;
    private UserDAO userDAO;
    protected String selectedMail;

    private File lastUsedDirectory = FileSystemView.getFileSystemView().getHomeDirectory();
    private File chosenDirectory;

    public void initialize() {
        invoiceTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        invoiceDAO = new InvoiceDAO();
        userDAO = new UserDAO();
        allUsers.setValue("alle Benutzer");
        userSelectionChanged();

        //AI assisted
        invoiceTable.setRowFactory(tableView -> {
            TableRow<Invoice> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Invoice invoice = row.getItem();
                    Invoice selectedInvoice = invoiceDAO.getInvoiceById(invoice.getInvoiceid());
                    showInvoiceDetails(selectedInvoice);
                }
            });
            return row;
        });
    }

    /**
     * Method loading all possible Users into a Combobox
     * <p>
     * Method loading all possible Users into a Combobox (all users from database and "Alle Benutzer")
     */
    public void showAllUsers() {
        allUsers.setItems(userDAO.getAllUserMailsWithAll());
}

    /**
     * Method setting all the data for a new set of Invoices to display
     * <p>
     * After setting the data the chosen field go blank
     */
    private void setSelectedData () {
        selectedMail = allUsers.getSelectionModel().getSelectedItem();
        if (selectedMail!=null && selectedMail.equals("alle Benutzer")) selectedMail = null;
    }
    /**
     * Method filling a table with a new set of Invoices to display
     * <p>
     * Method filling a table with a new set of Invoices to display. Invoices should be of status "EINGEREICHT"
     */
    public void userSelectionChanged() {
        setSelectedData();

        invoiceid.setCellValueFactory(new PropertyValueFactory<>("invoiceid"));
        userid.setCellValueFactory(new PropertyValueFactory<>("userid"));
        invoiceDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        invoiceAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        reimbursementAmount.setCellValueFactory(new PropertyValueFactory<>("reimbursementAmount"));
        invType.setCellValueFactory(new PropertyValueFactory<>("type"));
        timesChanged.setCellValueFactory(new PropertyValueFactory<>("timesChanged"));
        invoiceRequestDate.setCellValueFactory(new PropertyValueFactory<>("requestDate"));


        ObservableList<Invoice> invoiceList = invoiceDAO.getSelectedInvoicesToClear(selectedMail, "EINGEREICHT", true);
        invoiceTable.setItems(invoiceList);// Setze die Rechnungen in die TableView
    }

    /**
     * Method loads a detailed view of a chosen Invoice
     * <p>
     * This function loads a detailed view from a chosen Invoice that need to be cleared/accepted
     * @param invoice Invoice to be loaded
     */
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

    /**
     * Method exporting the current payroll data to a JSON file
     * <p>
     * Method exporting the current payroll data (last calendar month) to a JSON file into a chosen directory
     */
    @FXML
    private void onExportPayrollDataJSONButtonClick() throws IOException {
        Map<Integer, Double> reimbursementPerUser = invoiceDAO.getReimbursementSumPerUser();
        Stage stage = (Stage) exportPayrollDataJSONButton.getScene().getWindow();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(lastUsedDirectory);
        chosenDirectory = directoryChooser.showDialog(stage);
        if (chosenDirectory != null) {
            lastUsedDirectory = chosenDirectory.getParentFile(); // Ordner speichern, falls nochmal geöffnet wird
            FileWriter output = new FileWriter(new File(chosenDirectory.getAbsolutePath() + "/Lunchify-Lohnverrechnungs-Export-" + LocalDate.now().toString() + ".json"));
            JSONArray filedata = new JSONArray();
            reimbursementPerUser.forEach((userid, amount) -> {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("Benutzer-ID|Personalnummer", userid);
                jsonObject.put("Monats-Rueckzahlungsbetrag", amount);
                filedata.add(jsonObject);

            });
            output.write(filedata.toJSONString());
            output.close();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("JSON-Lohnverrechnungs-Export");
            alert.setHeaderText("Lunchify-Lohnverrechnungs-Export-" + LocalDate.now().toString() + ".json\nwurde in " + chosenDirectory.getPath() + " gespeichert");
            alert.setContentText("Ihre Datei wurde gespeichert!");
            alert.showAndWait();
        }
    }

    /**
     * Method exporting the current payroll data to a XML file
     * <p>
     * Method exporting the current payroll data (last calendar month) to a XML file into a chosen directory
     */
    @FXML
    private void onExportPayrollDataXMLButtonClick() throws IOException {
        Map<Integer, Double> reimbursementPerUser = invoiceDAO.getReimbursementSumPerUser();
        Stage stage = (Stage) exportPayrollDataJSONButton.getScene().getWindow();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(lastUsedDirectory);
        chosenDirectory = directoryChooser.showDialog(stage);
        if (chosenDirectory != null) {
            lastUsedDirectory = chosenDirectory.getParentFile(); // Ordner speichern, falls nochmal geöffnet wird
            List<Map<String, String>> outputData = new ArrayList<>();
            reimbursementPerUser.forEach((userid, amount) -> {
                outputData.add(Map.of("Benutzer-ID-Personalnummer",userid.toString(),"Monats-Rueckzahlungsbetrag",amount.toString()));
            });
            XmlMapper mapper = new XmlMapper();
            mapper.writer().withRootName("Lunchify-Rueckerstattungsbetraege").writeValue(new File(chosenDirectory.getAbsolutePath() + "/Lunchify-Lohnverrechnungs-Export-" + LocalDate.now().toString() + ".xml"), outputData);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("XML-Lohnverrechnungs-Export");
            alert.setHeaderText("Lunchify-Lohnverrechnungs-Export-" + LocalDate.now().toString() + ".xml\nwurde in " + chosenDirectory.getPath() + " gespeichert");
            alert.setContentText("Ihre Datei wurde gespeichert!");
            alert.showAndWait();
        }
    }
}
