package at.jku.se.lunchify;

import at.jku.se.lunchify.models.Invoice;
import at.jku.se.lunchify.models.InvoiceDAO;
import at.jku.se.lunchify.models.InvoiceSettingService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

/**
 * Testing nicht sinnvoll, Insert über InvoiceDAO aufgrund DB-Zugriff nicht sinnvoll testbar
 * Klasse zum Hochladen der Rechnungen
 */

public class UploadController {
    @FXML
    protected Button invoiceUploadButton;
    @FXML
    protected Label warningText;
    @FXML
    protected TextField invoiceValue;
    @FXML
    protected Label reimbursementValue;
    @FXML
    protected ComboBox<String> invoiceType;
    @FXML
    protected DatePicker invoiceDate;
    @FXML
    protected Label fileName;
    @FXML
    protected Button invoiceAttachmentButton;
    @FXML
    protected TextField invoiceNumber;
    @FXML
    protected Label isAnomalous;
    @FXML
    protected Label invoiceTypeOCR;
    @FXML
    protected Label invoiceNumberOCR;
    @FXML
    protected Label invoiceValueOCR;
    @FXML
    protected DatePicker invoiceDateOCR;

    double invoiceValueDouble;
    File selectedFile;
    private File lastUsedDirectory = FileSystemView.getFileSystemView().getHomeDirectory();
    boolean invoiceAnomalous = false;
    String selectedType;
    double reimbursementValueDouble;

    private final InvoiceDAO invoiceDAO = new InvoiceDAO();
    private final InvoiceSettingService invoiceSettingService = new InvoiceSettingService();

    public void initialize() {
        showAllInvoiceTypes();
        //AI-generated
        invoiceDate.getEditor().setDisable(true);
        invoiceDate.setEditable(false);

        invoiceDate.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                if (date != null && (date.getDayOfWeek() == DayOfWeek.SATURDAY ||
                        date.getDayOfWeek() == DayOfWeek.SUNDAY)) {
                    setDisable(true);
                    setStyle("-fx-background-color: #EEEEEE;");
                }
            }
        });
        invoiceValue.textProperty().addListener((observable, oldValue, newValue) -> {
            if (invoiceValue.getText()!=null) invoiceTypeChanged();
        });
    }

    public void showAllInvoiceTypes() {
        invoiceType.setItems(invoiceSettingService.getAllInvoiceTypes());
    }

    public void onInvoiceUploadButtonClick() throws IOException {
        if (invoiceValue.getText().isEmpty() || invoiceType.getValue().isEmpty() || invoiceDate.getValue() == null
                || invoiceNumber.getText().isEmpty() || selectedFile == null) {
            warningText.setText("Alle Felder ausfüllen!");
            return;
        }
        if (invoiceDAO.checkDateInPast(invoiceDate.getValue())) {
            try{
                invoiceValueDouble = Double.parseDouble(invoiceValue.getText());
            } catch (NumberFormatException e) {
                warningText.setText("Der Rechnungsbetrag muss eine Zahl sein!");
                return;
            }
            if (invoiceDAO.checkInvoiceValueIsPositive(invoiceValueDouble)) {
                if (invoiceDAO.checkInvoicesByDateAndUser(LoginController.currentUserId, invoiceDate.getValue())) {
                    // Wenn es ein Ergebnis gibt, dann wurde für den ausgewählten Tag schon eine Rechnung hochgeladen
                    warningText.setText("Es wurde schon eine Rechnung für den ausgewählten Tag hochgeladen!");

                } else {
                    if(!invoiceTypeOCR.getText().equals(invoiceType.getValue()) || !invoiceNumberOCR.getText().equals(invoiceNumber.getText()) || !invoiceValueOCR.getText().equals(invoiceValue.getText()) || !invoiceDateOCR.getValue().equals(invoiceDate.getValue()))
                    {
                        isAnomalous.setText("0");
                    }
                    if(isAnomalous.getText().equals("0"))
                    {
                        invoiceAnomalous = true;
                    }
                    reimbursementValueDouble = invoiceSettingService.getReimbursementValue(selectedType, invoiceValueDouble);
                    if(invoiceValueDouble < reimbursementValueDouble) {reimbursementValueDouble = invoiceValueDouble;}
                    Invoice invoice = new Invoice(LoginController.currentUserId, invoiceNumber.getText(), Date.valueOf(invoiceDate.getValue()), invoiceValueDouble, reimbursementValueDouble, invoiceType.getValue(), invoiceAnomalous, FileUtils.readFileToByteArray(selectedFile), 0);

                    if (invoiceDAO.insertInvoice(invoice)) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Hochladen erfolgreich");
                        alert.setHeaderText("Rechnung erfolgreich hochgeladen!");

                        alert.setContentText(
                                "Ihre Rechnung vom " + invoiceDate.getValue().toString() + " wurde erfolgreich hochgeladen.\n" +
                                        "Eingereichter Rechnungsbetrag: " + invoiceValueDouble + " €\n" +
                                        "Voraussichtlicher Rückerstattungsbetrag: " + reimbursementValueDouble+ " €"
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
    public void onInvoiceAttachmentButtonClick() throws IOException {
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
            //AI-assisted
            //1. Tesseract-Instanz erzeugen
            Tesseract tesseract = new Tesseract();
            tesseract.setLanguage("deu");

            // 2. Temporäres Wurzelverzeichnis erstellen
            Path tempRoot = Files.createTempDirectory("tessdata-temp");
            Path tessdataDir = tempRoot.resolve("tessdata");
            Files.createDirectories(tessdataDir);
            tempRoot.toFile().deleteOnExit();

            // 3. Traineddata-Datei aus den Ressourcen extrahieren
            try (InputStream in = getClass().getResourceAsStream("/tessdata/deu.traineddata")) {
                if (in == null) {
                    throw new IOException("Resource /tessdata/deu.traineddata not found!");
                }

                Path trainedDataFile = tessdataDir.resolve("deu.traineddata");
                Files.copy(in, trainedDataFile, StandardCopyOption.REPLACE_EXISTING);
            }

            // 4. Tesseract konfigurieren (WICHTIG: parent directory von "tessdata")
            tesseract.setDatapath(tessdataDir.toFile().getAbsolutePath());

            // Beginne mit Auslesen der Datei
            try {
                String text = tesseract.doOCR(selectedFile);
                //Rechnungstyp ermitteln
                if(text.toLowerCase().contains("billa") || text.toLowerCase().contains("billa plus") || text.toLowerCase().contains("hofer") || text.toLowerCase().contains("spar") || text.toLowerCase().contains("eurospar") || text.toLowerCase().contains("interspar") || text.toLowerCase().contains("supermarkt"))
                {
                    invoiceType.setValue("Supermarkt");
                    invoiceTypeOCR.setText(invoiceType.getValue());
                }
                else if(text.toLowerCase().contains("restaurant") || text.toLowerCase().contains("mensa") || text.toLowerCase().contains("imbiss") || text.toLowerCase().contains("grill") || text.toLowerCase().contains("gastronomie"))
                {
                    invoiceType.setValue("Restaurant");
                    invoiceTypeOCR.setText(invoiceType.getValue());
                }
                else {
                    isAnomalous.setText("0");
                }
                //Rechnungsnummer ermitteln
                if(text.toLowerCase().contains("re-nr")) //Billa Rechnungen
                {
                    invoiceNumber.setText(text.toLowerCase().split("re-nr:")[1].trim().substring(0,21));
                    invoiceNumberOCR.setText(invoiceNumber.getText());
                }
                else if(text.toLowerCase().contains("beleg-nr:")) //Burak Supermarkt
                {
                    invoiceNumber.setText(text.toLowerCase().split("beleg-nr:")[1].trim().substring(0,7));
                    invoiceNumberOCR.setText(invoiceNumber.getText());
                }
                else if(text.toLowerCase().contains("bon")) // Spar Rechnungen (Spar, Eurospar und Interspar)
                {
                    invoiceNumber.setText(text.toLowerCase().split("bon")[1].trim().substring(0,4));
                    invoiceNumberOCR.setText(invoiceNumber.getText());
                }
                else if(text.toLowerCase().contains("vielen")) // Hofer Rechnungen
                {
                    String invNumberText = text.toLowerCase().split("vielen")[0].trim();
                    invoiceNumber.setText(invNumberText.substring(invNumberText.length()-36,invNumberText.length()-15));
                    invoiceNumberOCR.setText(invoiceNumber.getText());
                }
                else if(text.toLowerCase().contains("rechnung")) // Restaurant-Rechnung, siehe Ordner Invoices im Repository
                {
                    invoiceNumber.setText(text.toLowerCase().split("rechnung")[1].trim().substring(0,6));
                    invoiceNumberOCR.setText(invoiceNumber.getText());
                }
                else {
                    isAnomalous.setText("0");
                }
                //Rechnungsdatum ermitteln
                if(text.toLowerCase().contains("datum:")) //Billa Rechnungen
                {
                    String day = text.toLowerCase().split("datum:")[1].trim().substring(0,2);
                    String month = text.toLowerCase().split("datum:")[1].trim().substring(3,5);
                    String year = text.toLowerCase().split("datum:")[1].trim().substring(6,10);

                    if (day.startsWith("0"))
                    {
                        day = day.substring(1);
                    }
                    if (month.startsWith("0"))
                    {
                        month = month.substring(1);
                    }
                    invoiceDate.setValue(LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day)));
                    invoiceDateOCR.setValue(invoiceDate.getValue());

                }
                else if(text.toLowerCase().contains("verkauf")) // Burak Supermarkt
                {
                    String day = text.toLowerCase().split("verkauf")[1].trim().substring(0,2);
                    String month = text.toLowerCase().split("verkauf")[1].trim().substring(3,5);
                    String year = text.toLowerCase().split("verkauf")[1].trim().substring(6,10);

                    if (day.startsWith("0"))
                    {
                        day = day.substring(1);
                    }
                    if (month.startsWith("0"))
                    {
                        month = month.substring(1);
                    }
                    invoiceDate.setValue(LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day)));
                    invoiceDateOCR.setValue(invoiceDate.getValue());
                }
                else if(text.toLowerCase().contains("ihr einkauf am")) // Spar Rechnungen (Spar, Eurospar und Interspar)
                {
                    String day = text.toLowerCase().split("am")[1].trim().substring(0,2);
                    String month = text.toLowerCase().split("am")[1].trim().substring(3,5);
                    String year = text.toLowerCase().split("am")[1].trim().substring(6,10);

                    if (day.startsWith("0"))
                    {
                        day = day.substring(1);
                    }
                    if (month.startsWith("0"))
                    {
                        month = month.substring(1);
                    }
                    invoiceDate.setValue(LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day)));
                    invoiceDateOCR.setValue(invoiceDate.getValue());
                }
                else if(text.toLowerCase().contains("vielen")) // Hofer Rechnungen
                {
                    String dateWithText = text.toLowerCase().split("vielen")[0].trim();
                    String date = dateWithText.substring(dateWithText.length()-14,dateWithText.length()-5);
                    String day = date.toLowerCase().substring(0,2);
                    String month = date.toLowerCase().substring(3,5);
                    String year = "20"+date.toLowerCase().substring(6,8);

                    if (day.startsWith("0"))
                    {
                        day = day.substring(1);
                    }
                    if (month.startsWith("0"))
                    {
                        month = month.substring(1);
                    }
                    invoiceDate.setValue(LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day)));
                    invoiceDateOCR.setValue(invoiceDate.getValue());

                }
                else if(text.toLowerCase().contains("rechnung")) // Restaurant-Rechnung, siehe Ordner Invoices im Repository
                {
                    String dateWithText = text.toLowerCase().split("tisch")[0].trim();
                    String date = dateWithText.substring(dateWithText.length()-19,dateWithText.length()-9);
                    String day = date.toLowerCase().substring(0,2);
                    String month = date.toLowerCase().substring(3,5);
                    String year = date.toLowerCase().substring(6,10);

                    if (day.startsWith("0"))
                    {
                        day = day.substring(1);
                    }
                    if (month.startsWith("0"))
                    {
                        month = month.substring(1);
                    }
                    invoiceDate.setValue(LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day)));
                    invoiceDateOCR.setValue(invoiceDate.getValue());

                }
                else {
                    isAnomalous.setText("0");
                }
                //Rechnungssumme ermitteln
                if(text.toLowerCase().contains("summe eur")) //Billa Rechnungen
                {
                    invoiceValue.setText(text.toLowerCase().split("summe eur")[1].trim().split("gegeben")[0]);
                    invoiceValueOCR.setText(invoiceValue.getText());
                }
                else if(text.toLowerCase().contains("gesamt")) // Burak Supermarkt
                {
                    invoiceValue.setText(text.toLowerCase().split("gesamt")[1].trim().split("€")[0].trim().replace(',','.'));
                    invoiceValueOCR.setText(invoiceValue.getText());
                }
                else if(text.toLowerCase().contains("summe:")) // Spar Rechnungen (Spar, Eurospar und Interspar)
                {
                    invoiceValue.setText(text.toLowerCase().split("summe:")[1].trim().substring(0,12).trim().split(" ")[0].replace(',','.'));
                    invoiceValueOCR.setText(invoiceValue.getText());
                }
                else if(text.toLowerCase().contains("hofer preis")) // Hofer Rechnungen
                {

                    String euro = text.toLowerCase().split("hofer preis")[1].trim().substring(0,2);
                    String cent = text.toLowerCase().split("hofer preis")[1].trim().substring(5,7);

                    invoiceValue.setText(euro+"."+cent);
                    invoiceValueOCR.setText(invoiceValue.getText());
                }
                else if(text.toLowerCase().contains("summe in€")) // Restaurant-Rechnung, siehe Ordner Invoices im Repository
                {
                    String euro = text.toLowerCase().split("summe in€")[1].trim().substring(0,2);
                    String cent = text.toLowerCase().split("summe in€")[1].trim().substring(4,6);
                    invoiceValue.setText(euro+"."+cent);
                    invoiceValueOCR.setText(invoiceValue.getText());
                }
                else {
                    isAnomalous.setText("0");
                }
            } catch (TesseractException | IndexOutOfBoundsException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Fehler!");
                alert.setHeaderText("Tesseract OCR-Fehler");
                alert.setContentText(e.getMessage());
            }


        } else {
            fileName.setText("Keine Datei ausgewählt.");
        }
    }

    public void invoiceTypeChanged() {
        selectedType = invoiceType.getSelectionModel().getSelectedItem();
        warningText.setText("");
        if (selectedType!=null && invoiceValue.getText()!=null && !invoiceValue.getText().isEmpty()) {
            try {
                invoiceValueDouble = Double.parseDouble(invoiceValue.getText());
            } catch (NumberFormatException e) {
                warningText.setText("Der Rechnungsbetrag muss eine Zahl sein!");
                reimbursementValue.setText("");
                return;
            }
            reimbursementValue.setText(invoiceSettingService.getReimbursementValue(selectedType, invoiceValueDouble) + " €");
            return;
        }
        reimbursementValue.setText("");
    }
}
