package at.jku.se.lunchify;

import at.jku.se.lunchify.models.Invoice;
import at.jku.se.lunchify.models.InvoiceDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;

public class InvoiceDetailController {
    @FXML
    protected Button clearButton;
    @FXML
    protected Button declineButton;
    @FXML
    protected TextField invoiceValue;
    @FXML
    protected TextField reimbursementValue;
    @FXML
    protected ChoiceBox<String> invoiceType;
    @FXML
    protected DatePicker invoiceDate;
    @FXML
    protected TextField invoiceNumber;
    @FXML
    protected ImageView invoiceImage;

    private Invoice invoice;
    private InvoiceDAO invoiceDAO = new InvoiceDAO();

    public void setInvoice(Invoice invoice) throws IOException {
        this.invoice = invoice;
        invoiceValue.setText(String.valueOf(invoice.getAmount()));
        reimbursementValue.setText(String.valueOf(invoice.getReimbursementAmount()));
        invoiceType.setValue(invoice.getType());
        invoiceNumber.setText(String.valueOf(invoice.getInvoicenumber()));

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(invoice.getDate());
        
        invoiceDate.setValue(calendar.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate());

        //AI generated -> nicht bei PDF möglich
        byte[] file = invoice.getFile();
        if (invoice.getFile() != null && invoice.getFile().length > 0) {
            invoiceImage.setImage(new Image(new ByteArrayInputStream(file)));
            //check ob kein image geladen
            if (file[0] == 0x25 && file[1] == 0x50 && file[2] == 0x44 && file[3] == 0x46) {
                invoice.openPDF();
            }
        }
        else System.out.println("Rechnung ist nicht da");
    }

    public void onClearButtonClick() {
        if (checkNoChanges()) {
            if (invoiceDAO.clearInvoice(invoice.getInvoiceid())) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Rechnungsfreigabe");
                alert.setHeaderText("Rechnung freigegeben");
                alert.setContentText("Rechnung wurde erfolgreich freigegeben!");
                alert.showAndWait();
                // AI-assisted: Fenster schließen
                Stage currentStage = (Stage) clearButton.getScene().getWindow();
                currentStage.close();
            }
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Rechnungsfreigabe");
                alert.setHeaderText("Rechnung nicht freigegeben");
                alert.setContentText("Rechnung konnte nicht freigeben werden!");
                alert.showAndWait();
            }
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Fehler");
            alert.setHeaderText("Änderungen zerst speichern");
            alert.setContentText("Änderungen müssen zuerst gespeichert werden!");
            alert.showAndWait();
        }
    }

    public void onDeclineButtonClick() {}

    public void onSaveChangesButtonClick() {}

    public void onDeleteButtonClick() {}

    public boolean checkNoChanges() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(invoice.getDate());
        LocalDate convertedDate = calendar.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        if (!invoice.getInvoicenumber().equals(invoiceNumber.getText())) return false;
        else if (!invoice.getType().equals(invoiceType.getValue())) return false;
        else if (!convertedDate.equals(invoiceDate.getValue())) return false;
        else if (invoice.getAmount()!= Double.parseDouble(invoiceValue.getText())) return false;
        else if (invoice.getReimbursementAmount()!= Double.parseDouble(reimbursementValue.getText())) return false;
        else return true;
    }
}
