package at.jku.se.lunchify;

import at.jku.se.lunchify.models.Invoice;
import at.jku.se.lunchify.models.InvoiceDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

public class UploadController {

    // Datenbank-Zugangsdaten
    private static final String JDBC_URL = "jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:6543/postgres";
    private static final String DB_USER = "postgres.yxshntkgvmksefegyfhz";
    private static final String DB_PASSWORD = "CaMaKe25!";

    @FXML
    protected Button invoiceUploadButton;
    @FXML
    protected Label warningText;
    @FXML
    protected TextField invoiceValue;
    @FXML
    protected TextField reimbursementValue;
    @FXML
    protected ChoiceBox<String> invoiceType;
    @FXML
    protected DatePicker invoiceDate;
    @FXML
    protected Label fileName;
    @FXML
    protected Button invoiceAttachmentButton;
    @FXML
    protected TextField invoiceNumber;

    double invoiceValueDouble;
    double reimbursementValueDouble;
    File selectedFile;
    private File lastUsedDirectory = new File(System.getProperty("user.home") + "/Desktop");
    private InvoiceDAO invoiceDAO = new InvoiceDAO();

    public boolean checkDateInPast(LocalDate date) {
        if (date.isAfter(LocalDate.now())) {
            return false;
        } else {
            return true;
        }
    }

    public boolean checkInvoiceValueIsPositive(Double value) {
        if (invoiceValueDouble <= 0) {
            return false;
        } else {
            return true;
        }
    }

    public double getReimbursementValueFromInvoiceType(String type) {
        if (type.equals("Restaurant")) {
            return 3.0;
        } else if (type.equals("Supermarkt")) {
            return 2.5;
        } else {
            return 0;
        }
    }

    public void onInvoiceUploadButtonClick() throws IOException, SQLException {
        if (invoiceValue.getText().isEmpty() || invoiceType.getValue().isEmpty() || invoiceDate.getValue() == null
                || invoiceNumber.getText().isEmpty() || selectedFile == null) {
            warningText.setText("Alle Felder ausfüllen!");
            return;
        }
        if (checkDateInPast(invoiceDate.getValue())) {
            reimbursementValueDouble = getReimbursementValueFromInvoiceType(invoiceType.getValue());
            if (reimbursementValueDouble == 3.0) {
                reimbursementValue.setText("3.0");
            } else if (reimbursementValueDouble == 2.5) {
                reimbursementValue.setText("2.5");
            } else {
                warningText.setText("Es gab einen Fehler bei der Verarbeitung des Rechnungstyps!");
                return;
            }
            try{
                invoiceValueDouble = Double.parseDouble(invoiceValue.getText());
            } catch (NumberFormatException e) {
                warningText.setText("Der Rechnungsbetrag muss eine Zahl sein!");
                return;
            }
            if (checkInvoiceValueIsPositive(invoiceValueDouble)) {
                if (invoiceDAO.checkInvoicesByDateAndUser(LoginController.currentUserId, invoiceDate.getValue())) {
                    // Wenn es ein Ergebnis gibt, dann wurde für den ausgewählten Tag schon eine Rechnung hochgeladen
                    warningText.setText("Es wurde schon eine Rechnung für den ausgewählten Tag hochgeladen!");

                } else {
                    Invoice invoice = new Invoice(LoginController.currentUserId, invoiceNumber.getText(), Date.valueOf(invoiceDate.getValue()), invoiceValueDouble, reimbursementValueDouble, invoiceType.getValue(), false, FileUtils.readFileToByteArray(selectedFile), 0);

                    if (invoiceDAO.insertInvoice(invoice)) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Hochladen erfolgreich");
                        alert.setHeaderText("Rechnung erfolgreich hochgeladen!");

                        alert.setContentText(
                                "Ihre Rechnung vom " + invoiceDate.getValue().toString() + " wurde erfolgreich hochgeladen.\n" +
                                        "Eingereichter Rechnungsbetrag: " + invoiceValueDouble + " €\n" +
                                        "Voraussichtlicher Rückerstattungsbetrag: " + reimbursementValueDouble + " €"
                        );

                        warningText.setText("");
                        alert.showAndWait();
                        LunchifyApplication.baseController.showCenterView("upload-view.fxml");
                    } else {
                        warningText.setText("Es gab ein Problem mit der Datenbankverbindung!");
                    }
                }
            } else {
                warningText.setText("Rechnungsbetrag muss positiv sein!");
            }
        } else {
            warningText.setText("Rechnungsdatum liegt in der Zukunft!");
        }
    }

    //AI-Assisted
    public void onInvoiceAttachmentButtonClick() throws IOException, SQLException {
        Stage stage = (Stage) invoiceAttachmentButton.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();

        // Filter für alle unterstützten Dateien
        FileChooser.ExtensionFilter allSupported = new FileChooser.ExtensionFilter(
                "Unterstützte Dateien (*.pdf, *.jpg, *.jpeg, *.png)", "*.pdf", "*.jpg", "*.jpeg", "*.png"
        );
        fileChooser.getExtensionFilters().add(allSupported);
        fileChooser.setSelectedExtensionFilter(allSupported);

        // Initialverzeichnis setzen, zuerst Desktop
        fileChooser.setInitialDirectory(lastUsedDirectory);

        // Datei auswählen
        this.selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            fileName.setText(selectedFile.getAbsolutePath());
            lastUsedDirectory = selectedFile.getParentFile(); // Ordner speichern, falls nochmal geöffnet wird
        } else {
            fileName.setText("Keine Datei ausgewählt.");
        }
    }
}
