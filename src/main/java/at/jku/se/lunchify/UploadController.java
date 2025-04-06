package at.jku.se.lunchify;

import at.jku.se.lunchify.models.Invoice;
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
    int invoiceNumberInt; // nur Nummern? Problem bei z.B. RE123
    File selectedFile;

    public void onInvoiceUploadButtonClick() throws IOException, SQLException {
        if (invoiceValue.getText().isEmpty() || invoiceType.getValue().isEmpty() || invoiceDate.getValue().equals("")
                || invoiceNumber.getText().isEmpty() || selectedFile == null) {
            warningText.setText("Alle Felder ausfüllen!");
            return;
        }
        if (invoiceDate.getValue().isBefore(LocalDate.now())) {
            warningText.setText("Rechnungsdatum liegt in der Zukunft!");
            return;
        }
        try {
            invoiceValueDouble = Double.parseDouble(invoiceValue.getText());
            if (invoiceValueDouble <= 0) {
                warningText.setText("Rechnungsbetrag muss positiv sein!");

                return;
            }
        } catch (NumberFormatException e) {
            warningText.setText("Rechnungsbetrag muss eine Zahl sein!");
            return;
        }
        try {
            invoiceNumberInt = Integer.parseInt(invoiceNumber.getText());
            warningText.setText("");

            } catch (NumberFormatException e) {
            warningText.setText("Rechnungsnummer muss eine Zahl sein!");
            return;
        }
        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT userid, date FROM public.\"Invoice\" WHERE userid = ? AND date = ?";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, LoginController.currentUserId);
                pstmt.setDate(2, Date.valueOf(invoiceDate.getValue()));
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        // Wenn es ein Ergebnis gibt, dann wurde für den ausgewählten Tag schon eine Rechnung hochgeladen
                            warningText.setText("Es wurde schon eine Rechnung für den ausgewählten Tag hochgeladen!");

                    } else {
                        String jdbcUrl = "jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:6543/postgres";
                        String username = "postgres.yxshntkgvmksefegyfhz";
                        String DBpassword = "CaMaKe25!";

                        Connection connection = DriverManager.getConnection(jdbcUrl, username, DBpassword);

                        PreparedStatement ps = connection.prepareStatement("insert into \"Invoice\" (userid, invoicenumber, date, amount, type, status, isanomalous, file) values(?,?,?,?,?,?,?,?);");
                        ps.setInt(1, LoginController.currentUserId);
                        ps.setInt(2, invoiceNumberInt);
                        ps.setDate(3, Date.valueOf(invoiceDate.getValue()));
                        ps.setDouble(4, invoiceValueDouble);
                        ps.setString(5, invoiceType.getValue());
                        ps.setString(6, "eingereicht");
                        ps.setBoolean(7, false);
                        ps.setBytes(8, FileUtils.readFileToByteArray(selectedFile));
                        ps.executeUpdate();
                        ps.close();
                        connection.close();
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Rechnung hochgeladen");
                        alert.setHeaderText("Rechnung erfolgreich hochgeladen!"); // oder null
                        alert.setContentText("Ihre Rechnung wurde erfolgreich hochgeladen! Sie dürfen gleich eine weitere Rechnung hochladen!");
                        alert.showAndWait();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            warningText.setText("Es gab ein Problem mit der Datenbankverbindung!");
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


        } else {
            // Der Benutzer hat den Dialog abgebrochen oder keine Datei ausgewählt
            fileName.setText("Keine Datei ausgewählt.");
        }
    }
}
