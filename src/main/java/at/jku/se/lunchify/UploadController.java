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
import java.time.chrono.ChronoLocalDate;
import java.util.regex.Pattern;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;


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
    @FXML
    protected Label isAnomalous;

    double invoiceValueDouble;
    double reimbursementValueDouble;
    File selectedFile;

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
        Tesseract tesseract = new Tesseract();

        // Setze Pfad zu den Sprachdaten im Projekt (relativ oder absolut)
        tesseract.setDatapath("src/main/resources/tessdata");
        tesseract.setLanguage("deu"); // oder "eng"

        try {
            String text = tesseract.doOCR(selectedFile);
            System.out.println("Erkannter Text:\n" + text);
        } catch (TesseractException e) {
            System.err.println("OCR-Fehler: " + e.getMessage());
        }

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
                        alert.setTitle("Rechnung hochgeladen");
                        alert.setHeaderText("Rechnung erfolgreich hochgeladen!"); // oder null
                        alert.setContentText("Ihre Rechnung wurde erfolgreich hochgeladen! Sie dürfen gleich eine weitere Rechnung hochladen!");
                        alert.showAndWait();
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
    public void onInvoiceAttachmentButtonClick() throws IOException, SQLException {
        //AI generated
        // Zugriff auf die Stage, die mit dem Button verbunden ist
        Stage stage = (Stage) invoiceAttachmentButton.getScene().getWindow();

        // FileChooser erstellen
        FileChooser fileChooser = new FileChooser();

        // Filter für PDF-, JPEG- und PNG-Dateien festlegen
        FileChooser.ExtensionFilter pdfFilter = new FileChooser.ExtensionFilter("PDF Dateien (*.pdf)", "*.pdf");
        FileChooser.ExtensionFilter jpegFilter = new FileChooser.ExtensionFilter("JPEG Dateien (*.jpg, *.jpeg)", "*.jpg", "*.jpeg");
        FileChooser.ExtensionFilter pngFilter = new FileChooser.ExtensionFilter("PNG Dateien (*.png)", "*.png");

        // Füge die Filter zum FileChooser hinzu
        fileChooser.getExtensionFilters().addAll(pdfFilter, jpegFilter, pngFilter);

        // Zeige den FileChooser und hole die ausgewählte Datei
        this.selectedFile = fileChooser.showOpenDialog(stage);

        // Überprüfen, ob eine Datei ausgewählt wurde
        if (selectedFile != null) {
            // Hier kannst du mit der ausgewählten Datei weiterarbeiten
            fileName.setText(selectedFile.getAbsoluteFile().toString());
            Tesseract tesseract = new Tesseract();

            // Setze Pfad zu den Sprachdaten im Projekt (relativ oder absolut)
            tesseract.setDatapath("src/main/resources/tessdata");
            tesseract.setLanguage("deu"); // oder "eng"

            try {
                String text = tesseract.doOCR(selectedFile);
                System.out.println("Erkannter Text:\n" + text);
                //Rechnungstyp ermitteln
                if(text.toLowerCase().contains("billa") || text.toLowerCase().contains("billa plus") || text.toLowerCase().contains("hofer") || text.toLowerCase().contains("spar") || text.toLowerCase().contains("eurospar") || text.toLowerCase().contains("interspar") || text.toLowerCase().contains("supermarkt"))
                {
                    invoiceType.setValue("Supermarkt");
                }
                else if(text.toLowerCase().contains("restaurant") || text.toLowerCase().contains("mensa") || text.toLowerCase().contains("imbiss") || text.toLowerCase().contains("grill") || text.toLowerCase().contains("gastronomie"))
                {
                    invoiceType.setValue("Restaurant");
                }
                else {
                    isAnomalous.setText("0");
                }
                //Rechnungsnummer ermitteln
                if(text.toLowerCase().contains("re-nr")) //Billa Rechnungen
                {
                    invoiceNumber.setText(text.toLowerCase().split("re-nr:")[1].trim().substring(0,21));
                }
                else if(text.toLowerCase().contains("beleg-nr:")) //Burak Supermarkt
                {
                    invoiceNumber.setText(text.toLowerCase().split("beleg-nr:")[1].trim().substring(0,7));
                }
                else if(text.toLowerCase().contains("bon")) // Spar Rechnungen (Spar, Eurospar und Interspar)
                {
                    invoiceNumber.setText(text.toLowerCase().split("bon")[1].trim().substring(0,4));
                }
                else if(text.toLowerCase().contains("vielen")) // Hofer Rechnungen
                {
                    String invNumberText = text.toLowerCase().split("vielen")[0].trim();
                    invoiceNumber.setText(invNumberText.substring(invNumberText.length()-36,invNumberText.length()-15));
                }
                else if(text.toLowerCase().contains("rechnung")) // Restaurant-Rechnung, siehe Ordner Invoices im Repository
                {
                    invoiceNumber.setText(text.toLowerCase().split("rechnung")[1].trim().substring(0,6));
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

                }
                else {
                    isAnomalous.setText("0");
                }
                //Rechnungssumme ermitteln
                if(text.toLowerCase().contains("summe eur")) //Billa Rechnungen
                {
                    invoiceValue.setText(text.toLowerCase().split("summe eur")[1].trim().split("gegeben")[0]);
                }
                else if(text.toLowerCase().contains("gesamt")) // Burak Supermarkt
                {
                    invoiceValue.setText(text.toLowerCase().split("gesamt")[1].trim().split("€")[0].trim().replace(',','.'));
                }
                else if(text.toLowerCase().contains("summe:")) // Spar Rechnungen (Spar, Eurospar und Interspar)
                {
                    invoiceValue.setText(text.toLowerCase().split("summe:")[1].trim().substring(0,12).trim().split(" ")[0].replace(',','.'));
                }
                else if(text.toLowerCase().contains("hofer preis")) // Hofer Rechnungen
                {

                    String euro = text.toLowerCase().split("hofer preis")[1].trim().substring(0,2);
                    String cent = text.toLowerCase().split("hofer preis")[1].trim().substring(5,7);

                    invoiceValue.setText(euro+"."+cent);
                }
                else if(text.toLowerCase().contains("summe in€")) // Restaurant-Rechnung, siehe Ordner Invoices im Repository
                {
                    String euro = text.toLowerCase().split("summe in€")[1].trim().substring(0,2);
                    String cent = text.toLowerCase().split("summe in€")[1].trim().substring(4,6);
                    invoiceValue.setText(euro+"."+cent);
                }
                else {
                    isAnomalous.setText("0");
                }
            } catch (TesseractException | IndexOutOfBoundsException e) {
                System.err.println("OCR-Fehler: " + e.getMessage());
            }


        } else {
            // Der Benutzer hat den Dialog abgebrochen oder keine Datei ausgewählt
            fileName.setText("Keine Datei ausgewählt.");
        }
    }
}
