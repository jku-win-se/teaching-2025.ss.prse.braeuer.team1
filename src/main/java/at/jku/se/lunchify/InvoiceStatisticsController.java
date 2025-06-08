package at.jku.se.lunchify;

import at.jku.se.lunchify.models.Invoice;
import at.jku.se.lunchify.models.InvoiceDAO;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;

public class InvoiceStatisticsController {
    @FXML
    protected Button exportCSVButton;
    @FXML
    protected Button exportPDFButton;
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
    protected TableColumn<Invoice, String> invoiceRequestDate;
    @FXML
    protected CheckBox isAnomalousSelected;

    private static String selectedMail;
    private static String selectedInvoiceType;
    private static java.sql.Date selectedDateFrom;
    private static java.sql.Date selectedDateTo;
    private static boolean selectedIsAnomalous;

    private File lastUsedDirectory = FileSystemView.getFileSystemView().getHomeDirectory();
    private File chosenDirectory;
    private File exportFile;

    private InvoiceDAO invoiceDAO;
    ObservableList<Invoice> invoiceList;

    public static void initData(String email, String type, java.sql.Date from, java.sql.Date to, boolean isAnomalous) {
        selectedMail = email;
        selectedInvoiceType = type;
        selectedDateFrom = from;
        selectedDateTo = to;
        selectedIsAnomalous = isAnomalous;
    }

    public void initialize () throws SQLException {
        invoiceTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        invoiceDAO = new InvoiceDAO();
        if (selectedIsAnomalous) {
            isAnomalousSelected.setSelected(true);
            filterInfo.setText("Anomalische Rechnungen (" + selectedInvoiceType + ") von " + selectedMail + " (Zeitraum: " + selectedDateFrom.toString() + " bis " + selectedDateTo.toString() + ")");
            invoiceList = invoiceDAO.getSelectedInvoices(selectedMail, selectedDateFrom, selectedDateTo, selectedInvoiceType, true);
        }
        else {
            filterInfo.setText("Rechnungen (" + selectedInvoiceType + ") von " + selectedMail + " (Zeitraum: " + selectedDateFrom.toString() + " bis " + selectedDateTo.toString() + ")");
            invoiceList = invoiceDAO.getSelectedInvoices(selectedMail, selectedDateFrom, selectedDateTo, selectedInvoiceType, false);
        }

        //controller.userEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        invoiceDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        invoiceAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        reimbursementAmount.setCellValueFactory(new PropertyValueFactory<>("reimbursementAmount"));
        invType.setCellValueFactory(new PropertyValueFactory<>("type"));
        invoiceStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        invoiceRequestDate.setCellValueFactory(new PropertyValueFactory<>("requestDate"));

        invoiceTable.setItems(invoiceList);// Setze die Rechnungen in die TableView
    }

    public void onReportCSVExportButtonClick() throws IOException {
        Stage stage = (Stage) exportCSVButton.getScene().getWindow();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(lastUsedDirectory);
        chosenDirectory = directoryChooser.showDialog(stage);
        if (chosenDirectory != null) {
            lastUsedDirectory = chosenDirectory.getParentFile(); // Ordner speichern, falls nochmal geöffnet wird
            FileWriter output = new FileWriter(new File(chosenDirectory.getAbsolutePath() + "/Lunchify-Rechnungs-Export-" + LocalDate.now().toString() + ".csv"));
            output.write("Benutzer-ID/Personalnummer;Rechnungsdatum;Rechnungsbetrag;Rückzahlungsbetrag;Typ;Status;Anomalisch?;Einmeldedatum" + System.lineSeparator());
            for (Invoice inv : invoiceTable.getItems()) {
                output.write(String.valueOf(inv.getUserid())+";"+inv.getDate().toString() + ";" +
                        inv.getAmount() + ";" + inv.getReimbursementAmount() + ";" + inv.getType() + ";" +
                        inv.getStatus() + ";"+String.valueOf(inv.isIsanomalous())+";"+inv.getRequestDate()+System.lineSeparator());
            }
            output.close();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("CSV-Export");
            alert.setHeaderText("Lunchify-Rechnungs-Export-" + LocalDate.now().toString() + ".csv\nwurde in "+chosenDirectory.getPath()+" gespeichert");
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
            Document document = new Document(new PdfDocument(new PdfWriter(chosenDirectory.getAbsolutePath() + "/Lunchify-Rechnungs-Export-" + LocalDate.now().toString() + ".pdf")));
            document.setFontSize(Float.valueOf("10.0"));
            document.setLeftMargin(Float.valueOf("20"));
            if (isAnomalousSelected.isSelected()) {
                document.add(new Paragraph("Lunchify-Export für anomalische Rechnungen vom " + LocalDate.now().toString()));
            }
            else{
                document.add(new Paragraph("Lunchify-Export für Rechnungen vom " + LocalDate.now().toString()));
            }
            Table table = new Table(UnitValue.createPercentArray(8)).useAllAvailableWidth();
            table.addHeaderCell("Benutzer-ID/Personalnummer");
            table.addHeaderCell("Rechnungsdatum");
            table.addHeaderCell("Rechnungsbetrag");
            table.addHeaderCell("Rückzahlungsbetrag");
            table.addHeaderCell("Typ");
            table.addHeaderCell("Status");
            table.addHeaderCell("Einmeldedatum");
            for (Invoice inv : invoiceTable.getItems()) {
                table.startNewRow();
                table.addCell(String.valueOf(inv.getUserid()));
                table.addCell(inv.getDate().toString());
                table.addCell(Double.toString(inv.getAmount()));
                table.addCell(Double.toString(inv.getReimbursementAmount()));
                table.addCell(inv.getType());
                table.addCell(inv.getStatus());
                table.addCell(inv.getRequestDate().toString());
            }
            document.add(table);
            document.close();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("PDF-Export");
            alert.setHeaderText("Lunchify-Rechnungs-Export-" + LocalDate.now().toString() + ".pdf\nwurde in "+chosenDirectory.getPath()+" gespeichert");
            alert.setContentText("Ihre Datei wurde gespeichert!");
            alert.showAndWait();
        }
    }
}
