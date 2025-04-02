package at.jku.se.lunchify;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class UploadController {
    @FXML
    protected Button invoiceUploadButton;
    @FXML
    protected Label warningText;
    @FXML
    protected TextField invoiceValue;
    @FXML
    protected ChoiceBox<String> invoiceType;
    @FXML
    protected DatePicker invoiceDate;
    @FXML
    protected Label fileName;
    @FXML
    protected Button invoiceChooseButton;
    double invoiceValueDouble;

    public void onInvoiceUploadButtonClick() throws IOException  {
        if (invoiceValue.getText().equals("") || invoiceType.getValue().equals("")|| invoiceDate.getValue().equals("")) {
            warningText.setText("Alle Felder ausfüllen!");
        }
        else {
            try {
                invoiceValueDouble = Double.parseDouble(invoiceValue.getText());
                if (invoiceValueDouble <= 0) {
                    warningText.setText("Rechnungsbetrag muss positiv sein!");
                } else {
                    warningText.setText("");
                    BaseController baseController = (BaseController) invoiceUploadButton.getScene().getRoot().getUserData();
                    baseController.showCenterView("upload-view.fxml");
                }
            } catch (NumberFormatException e) {
                warningText.setText("Rechnungsbetrag muss eine Zahl sein!");
            }
        }
    }
    public void onInvoiceChooseButtonClick() throws IOException  {
        //AI generated
        // Zugriff auf die Stage, die mit dem Button verbunden ist
        Stage stage = (Stage) invoiceChooseButton.getScene().getWindow();

        // FileChooser erstellen
        FileChooser fileChooser = new FileChooser();

        // Filter für PDF und JPEG-Dateien festlegen
        FileChooser.ExtensionFilter pdfFilter = new FileChooser.ExtensionFilter("PDF Dateien (*.pdf)", "*.pdf");
        FileChooser.ExtensionFilter jpegFilter = new FileChooser.ExtensionFilter("JPEG Dateien (*.jpg, *.jpeg)", "*.jpg", "*.jpeg");

        // Füge die Filter zum FileChooser hinzu
        fileChooser.getExtensionFilters().addAll(pdfFilter, jpegFilter);

        // Zeige den FileChooser und hole die ausgewählte Datei
        File selectedFile = fileChooser.showOpenDialog(stage);

        // Überprüfen, ob eine Datei ausgewählt wurde
        if (selectedFile != null) {
            // Hier kannst du mit der ausgewählten Datei weiterarbeiten
            fileName.setText(selectedFile.getAbsoluteFile().toString());

        } else {
            // Der Benutzer hat den Dialog abgebrochen oder keine Datei ausgewählt
            fileName.setText("Keine Datei ausgewählt.");
        }
    }
}
