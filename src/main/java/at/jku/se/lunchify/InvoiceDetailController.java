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
import java.sql.Date;
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
    @FXML
    protected Label warningText;

    private Invoice invoice;
    private InvoiceDAO invoiceDAO = new InvoiceDAO();

    double invoiceValueDouble;
    double reimbursementValueDouble;

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

        //AI generated -> jpg+png wird in imageView geladen und PDF extra im PDF-Reader
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
            if (invoiceDAO.setInvoiceStatus(invoice.getInvoiceid(),"genehmigt")) {
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

    public void onDeclineButtonClick() {
        if (checkNoChanges()) {
            if (invoiceDAO.setInvoiceStatus(invoice.getInvoiceid(),"abgelehnt")) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Rechnungsablehnung");
                alert.setHeaderText("Rechnung abgelehnt");
                alert.setContentText("Rechnung wurde erfolgreich abgelehnt!");
                alert.showAndWait();
                // AI-assisted: Fenster schließen
                Stage currentStage = (Stage) clearButton.getScene().getWindow();
                currentStage.close();
            }
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Rechnungsablehnung");
                alert.setHeaderText("Rechnung nicht abgelehnt");
                alert.setContentText("Rechnung konnte nicht abgelehnt werden!");
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

    public void onSaveChangesButtonClick() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(invoice.getDate());
        LocalDate convertedDate = calendar.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        if (invoiceValue.getText().isEmpty() || invoiceType.getValue().isEmpty() || invoiceDate.getValue() == null
                || invoiceNumber.getText().isEmpty()) {
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
                if (!convertedDate.equals(invoiceDate.getValue()) && invoiceDAO.checkInvoicesByDateAndUser(LoginController.currentUserId, invoiceDate.getValue())) {
                    // Wenn es ein Ergebnis gibt, dann wurde für den ausgewählten Tag schon eine Rechnung hochgeladen
                    warningText.setText("Es wurde schon eine Rechnung für den ausgewählten Tag hochgeladen!");

                } else {
                    invoice.setType(invoiceType.getValue());
                    invoice.setInvoicenumber(invoiceNumber.getText());
                    invoice.setAmount(invoiceValueDouble);
                    invoice.setDate(Date.valueOf(invoiceDate.getValue()));

                    System.out.println(invoice.getAmount());
                    if (invoiceDAO.updateInvoice(invoice)) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Rechnung geändert");
                        alert.setHeaderText("Rechnung erfolgreich geändert!"); // oder null
                        alert.setContentText("Rechnung wurde erfolgreich geändert!");
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

    public void onDeleteButtonClick() {
        if (invoiceDAO.deleteInvoice(invoice.getInvoiceid())) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Rechnungslöschung");
            alert.setHeaderText("Rechnung gelöscht");
            alert.setContentText("Rechnung wurde erfolgreich gelöscht!");
            alert.showAndWait();
            // AI-assisted: Fenster schließen
            Stage currentStage = (Stage) clearButton.getScene().getWindow();
            currentStage.close();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Rechnungslöschung");
            alert.setHeaderText("Rechnung nicht gelöscht");
            alert.setContentText("Rechnung konnte nicht gelöscht werden!");
            alert.showAndWait();
        }
    }

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
