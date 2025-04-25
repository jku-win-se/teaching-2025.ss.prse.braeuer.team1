package at.jku.se.lunchify;

import at.jku.se.lunchify.models.Invoice;
import at.jku.se.lunchify.models.InvoiceDAO;
import at.jku.se.lunchify.models.User;
import at.jku.se.lunchify.models.UserDAO;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.pdfbox.debugger.ui.FileOpenSaveDialog;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;


import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

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
    protected TableColumn<Invoice, Date> invoiceDate;
    @FXML
    protected TableColumn<Invoice, Double> invoiceAmount;
    @FXML
    protected TableColumn<Invoice, Double> reimbursementAmount;
    @FXML
    protected TableColumn<Invoice, String> invType;
    @FXML
    protected TableColumn<Invoice, String> invoiceStatus;
    @FXML
    protected Button exportCSVButton;
    @FXML
    protected Button exportPDFButton;

    protected String selectedMail;
    protected User selectedUser;
    protected java.sql.Date selectedDateFrom;
    protected java.sql.Date selectedDateTo;
    protected String selectedInvoiceType;
    protected boolean inputCorrect = false;

    private InvoiceDAO invoiceDAO;
    ObservableList<Invoice> invoiceList;
    private File lastUsedDirectory = FileSystemView.getFileSystemView().getHomeDirectory();
    private File chosenDirectory;
    private File exportFile;

    private UserDAO userDAO;

    private final Date today = new Date();
    private final LocalDate heuteVorEinemJahr = LocalDate.now().minusYears(1);
    private final Date todayLastYear = Date.from(heuteVorEinemJahr.atStartOfDay(ZoneId.systemDefault()).toInstant());

    public void initialize() {
        invoiceDAO = new InvoiceDAO();
        userDAO = new UserDAO();
    }

    public void showAllUsers() {
        allUsers.setItems(userDAO.getAllUserMailsWithAll());
    }

    private void setSelectedData() {
        selectedMail = allUsers.getSelectionModel().getSelectedItem();
        selectedUser = userDAO.getUserByEmail(selectedMail);
        selectedDateFrom = valueOf(dateFrom.getValue());
        selectedDateTo = valueOf(dateTo.getValue());
        selectedInvoiceType = invoiceType.getValue();
    }

    private void checkSelectedData() {
        if (selectedMail == null || selectedDateFrom == null || selectedDateTo == null || selectedInvoiceType == null) {
            warningText.setText("Alle Filter setzen!");
        } else if (selectedDateTo.before(selectedDateFrom)) {
            warningText.setText("Bis-Datum darf nicht vor dem Von-Datum liegen!");
        } else if (selectedDateTo.after(today)) {
            warningText.setText("Bis-Datum darf nicht in der Zukunft liegen!");
        } else if (selectedDateFrom.before(todayLastYear)) {
            warningText.setText("Auswertung für max. 12 Monate zurück!");
        } else {
            inputCorrect = true;
        }
    }

    public void onInvoiceIndicatorsButtonClick() {
        setSelectedData();
        checkSelectedData();
        //Implementierung offen
        LunchifyApplication.baseController.basePane.setRight(null);
    }

    public void onInvoiceStatisticsButtonClick() throws IOException, SQLException {
        setSelectedData();
        checkSelectedData();
        if (inputCorrect) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("invoiceStatistics-view.fxml"));
            Parent root = loader.load();
            ReportController controller = loader.getController();
            controller.filterInfo.setText("Rechnungen (" + selectedInvoiceType + ") von " + selectedMail + " (Zeitraum: " + selectedDateFrom.toString() + " bis " + selectedDateTo.toString() + ")");
            LunchifyApplication.baseController.basePane.setCenter(root);

            //controller.userEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
            controller.invoiceDate.setCellValueFactory(new PropertyValueFactory<>("date"));
            controller.invoiceAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
            controller.reimbursementAmount.setCellValueFactory(new PropertyValueFactory<>("reimbursementAmount"));
            controller.invType.setCellValueFactory(new PropertyValueFactory<>("type"));
            controller.invoiceStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

            invoiceList = invoiceDAO.getSelectedInvoices(selectedMail, selectedDateFrom, selectedDateTo, selectedInvoiceType);
            controller.invoiceTable.setItems(invoiceList);// Setze die Rechnungen in die TableView
        }
    }

    public void onReportCSVExportButtonClick() throws IOException {
        Stage stage = (Stage) exportCSVButton.getScene().getWindow();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(lastUsedDirectory);
        chosenDirectory = directoryChooser.showDialog(stage);
        if (chosenDirectory != null) {
            lastUsedDirectory = chosenDirectory.getParentFile(); // Ordner speichern, falls nochmal geöffnet wird
            FileWriter output = new FileWriter(new File(chosenDirectory.getAbsolutePath() + "/Lunchify-Export-" + LocalDate.now().toString() + ".csv"));
            output.write("Benutzer-ID/Personalnummer;Rechnungsdatum;Rechnungsbetrag;Rückzahlungsbetrag;Typ;Status" + System.lineSeparator());
            for (Invoice inv : invoiceTable.getItems()) {
                output.write(String.valueOf(inv.getUserid())+";"+inv.getDate().toString() + ";" + inv.getAmount() + ";" + inv.getReimbursementAmount() + ";" + inv.getType() + ";" + inv.getStatus() + System.lineSeparator());
            }
            output.close();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("CSV-Export");
            alert.setHeaderText("CSV-Export");
            alert.setContentText("Ihre Datei wurde gespeichert!");
            alert.showAndWait();
        }
    }

    public void onReportPDFExportButtonClick() throws IOException {
        Stage stage = (Stage) exportPDFButton.getScene().getWindow();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(lastUsedDirectory);
        chosenDirectory = directoryChooser.showDialog(stage);
        if (chosenDirectory != null) {
            lastUsedDirectory = chosenDirectory.getParentFile(); // Ordner speichern, falls nochmal geöffnet wird
            Document document = new Document(new PdfDocument(new PdfWriter(chosenDirectory.getAbsolutePath() + "/Lunchify-Export-" + LocalDate.now().toString() + ".pdf")));
            document.add(new Paragraph("Lunchify-Export vom "+LocalDate.now().toString()));
            Table table = new Table(UnitValue.createPercentArray(8)).useAllAvailableWidth();
            table.addHeaderCell("Benutzer-ID/Personalnummer");
            table.addHeaderCell("Rechnungsdatum");
            table.addHeaderCell("Rechnungsbetrag");
            table.addHeaderCell("Rückzahlungsbetrag");
            table.addHeaderCell("Typ");
            table.addHeaderCell("Status");
            for (Invoice inv : invoiceTable.getItems()) {
                table.startNewRow();
                table.addCell(String.valueOf(inv.getUserid()));
                table.addCell(inv.getDate().toString());
                table.addCell(Double.toString(inv.getAmount()));
                table.addCell(Double.toString(inv.getReimbursementAmount()));
                table.addCell(inv.getType());
                table.addCell(inv.getStatus());
            }
            document.add(table);
            document.close();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("PDF-Export");
            alert.setHeaderText("PDF-Export");
            alert.setContentText("Ihre Datei wurde gespeichert!");
            alert.showAndWait();
        }


    }
}

