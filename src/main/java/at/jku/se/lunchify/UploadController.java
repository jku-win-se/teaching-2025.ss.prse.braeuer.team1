package at.jku.se.lunchify;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.sql.*;



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
    protected Button invoiceAttachmentButton;

    double invoiceValueDouble;
    File selectedFile;

    public void onInvoiceUploadButtonClick() throws IOException  {
        if (invoiceValue.getText().isEmpty() || invoiceType.getValue().isEmpty() || invoiceDate.getValue().equals("")
                || selectedFile == null) {
            warningText.setText("Alle Felder ausfüllen!");
        }
        else {
            try {
                invoiceValueDouble = Double.parseDouble(invoiceValue.getText());
                if (invoiceValueDouble <= 0) {
                    warningText.setText("Rechnungsbetrag muss positiv sein!");
                } else {
                    warningText.setText("");
                    LunchifyApplication.baseController.showCenterView("upload-view.fxml");
                }
            } catch (NumberFormatException e) {
                warningText.setText("Rechnungsbetrag muss eine Zahl sein!");
            }
        }
    }
    public void onInvoiceAttachmentButtonClick() throws IOException, SQLException {
        String jdbcUrl = "jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:6543/postgres";
        String username = "postgres.yxshntkgvmksefegyfhz";
        String DBpassword = "CaMaKe25!";

        //AI generated
        // Zugriff auf die Stage, die mit dem Button verbunden ist
        Stage stage = (Stage) invoiceAttachmentButton.getScene().getWindow();

        // FileChooser erstellen
        FileChooser fileChooser = new FileChooser();

        // Filter für PDF und JPEG-Dateien festlegen
        FileChooser.ExtensionFilter pdfFilter = new FileChooser.ExtensionFilter("PDF Dateien (*.pdf)", "*.pdf");
        FileChooser.ExtensionFilter jpegFilter = new FileChooser.ExtensionFilter("JPEG Dateien (*.jpg, *.jpeg)", "*.jpg", "*.jpeg");

        // Füge die Filter zum FileChooser hinzu
        fileChooser.getExtensionFilters().addAll(pdfFilter, jpegFilter);

        // Zeige den FileChooser und hole die ausgewählte Datei
        this.selectedFile = fileChooser.showOpenDialog(stage);

        // Überprüfen, ob eine Datei ausgewählt wurde
        if (selectedFile != null) {
            // Hier kannst du mit der ausgewählten Datei weiterarbeiten
            Connection connection = DriverManager.getConnection(jdbcUrl, username, DBpassword);

            PreparedStatement ps = connection.prepareStatement("insert into \"Invoice\" (userid, invoicenumber, date, amount, type, status, isanomalous, file) values(?,?,?,?,?,?,?,?);");
            ps.setInt(1, LoginController.getCurrentUserId());
            ps.setInt(2, 1);
            ps.setDate(3, Date.valueOf(invoiceDate.getValue()));
            ps.setDouble(4, invoiceValueDouble);
            ps.setString(5, invoiceType.getValue());
            ps.setString(6, "eingereicht");
            ps.setBoolean(7, false);
            ps.setBytes(8,FileUtils.readFileToByteArray(selectedFile));
            ps.executeUpdate();
            ps.close();
            connection.close();


        } else {
            // Der Benutzer hat den Dialog abgebrochen oder keine Datei ausgewählt
            fileName.setText("Keine Datei ausgewählt.");
        }
    }
}
