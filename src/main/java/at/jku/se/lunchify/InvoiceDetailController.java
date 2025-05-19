package at.jku.se.lunchify;

import at.jku.se.lunchify.models.Invoice;
import at.jku.se.lunchify.models.InvoiceDAO;
import at.jku.se.lunchify.models.InvoiceSettingService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;

/**
 * Testing nicht sinnvoll, Statusänderungen über InvoiceDAO aufgrund DB-Zugriff nicht sinnvoll testbar
 * Möglichkeit wäre, RE mit Setup erstellen und RE danach wieder löschen? Soll das umgesetzt werden?
 *
 * Klasse zum Freigeben und Bearbeiten der Rechnungen als Admin (keine eigenen Rechnungen)
 */

public class InvoiceDetailController {
    @FXML
    protected Button clearButton;
    @FXML
    protected Button declineButton;
    @FXML
    protected TextField invoiceValue;
    @FXML
    protected Label reimbursementValue;
    @FXML
    protected ComboBox<String> invoiceType;
    @FXML
    protected DatePicker invoiceDate;
    @FXML
    protected TextField invoiceNumber;
    @FXML
    protected ImageView invoiceImage;
    @FXML
    protected Label warningText;

    private Invoice invoice;
    private final InvoiceDAO invoiceDAO = new InvoiceDAO();
    private final InvoiceSettingService invoiceSettingService = new InvoiceSettingService();

    double invoiceValueDouble;
    String selectedType;
    double reimbursementValueDouble;

    //AI-generated
    public void initialize() {
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

    public void setInvoice(Invoice invoice) throws IOException {
        this.invoice = invoice;
        showAllInvoiceTypes();
        invoiceValue.setText(String.valueOf(invoice.getAmount()));
        reimbursementValue.setText(String.valueOf(invoice.getReimbursementAmount()));
        invoiceType.setValue(invoice.getType());
        invoiceNumber.setText(String.valueOf(invoice.getInvoicenumber()));
        invoiceDate.setValue(convertDateToLocalDate(invoice.getDate()));

        //AI generated -> jpg+png wird in imageView geladen und PDF extra im PDF-Reader
        byte[] file = invoice.getFile();
        if (invoice.getFile() != null && invoice.getFile().length > 0) {
            invoiceImage.setImage(new Image(new ByteArrayInputStream(file)));
            //check ob kein image geladen
            if (file[0] == 0x25 && file[1] == 0x50 && file[2] == 0x44 && file[3] == 0x46) {
                invoice.openPDF();
            }
        }
    }

    public void showAllInvoiceTypes() {
        invoiceType.setItems(invoiceSettingService.getAllInvoiceTypes());
    }

    public void onClearButtonClick() {
        if (checkNoChanges()) {
            if (invoiceDAO.setInvoiceStatus(invoice.getInvoiceid(),"GENEHMIGT")) {
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
            alert.setHeaderText("Änderungen zuerst speichern");
            alert.setContentText("Änderungen müssen zuerst gespeichert werden!");
            alert.showAndWait();
        }
    }

    public void onDeclineButtonClick() {
        if (checkNoChanges()) {
            if (invoiceDAO.setInvoiceStatus(invoice.getInvoiceid(),"ABGELEHNT")) {
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
            alert.setHeaderText("Änderungen zuerst speichern");
            alert.setContentText("Änderungen müssen zuerst gespeichert werden!");
            alert.showAndWait();
        }
    }

    public void onSaveChangesButtonClick() {
        if (invoiceValue.getText().isEmpty() || invoiceType.getValue().isEmpty() || invoiceDate.getValue() == null
                || invoiceNumber.getText().isEmpty()) {
            warningText.setText("Alle Felder ausfüllen!");
            return;
        }
        if (invoiceDAO.checkDateInPast(invoiceDate.getValue())) {
            try{
                invoiceValueDouble = Double.parseDouble(invoiceValue.getText());
            } catch (NumberFormatException e) {
                warningText.setText("Der Rechnungsbetrag muss \neine Zahl sein!");
                return;
            }
            if (invoiceDAO.checkInvoiceValueIsPositive(invoiceValueDouble)) {
                if (!convertDateToLocalDate(invoice.getDate()).equals(invoiceDate.getValue()) && invoiceDAO.checkInvoicesByDateAndUser(invoice.getUserid(), invoiceDate.getValue())) {
                    // Wenn es ein Ergebnis gibt, dann wurde für den ausgewählten Tag schon eine Rechnung hochgeladen
                    warningText.setText("Es wurde schon eine Rechnung \nfür den ausgewählten Tag \nhochgeladen!");

                } else {
                    invoice.setType(invoiceType.getValue());
                    invoice.setInvoicenumber(invoiceNumber.getText());
                    invoice.setAmount(invoiceValueDouble);
                    invoice.setDate(Date.valueOf(invoiceDate.getValue()));
                    reimbursementValueDouble = Double.parseDouble(reimbursementValue.getText());
                    if(invoiceValueDouble < reimbursementValueDouble) {reimbursementValueDouble = invoiceValueDouble;}
                    invoice.setReimbursementAmount(reimbursementValueDouble);

                    if (invoiceDAO.updateInvoice(invoice)) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Rechnung geändert");
                        alert.setHeaderText("Rechnung erfolgreich geändert!"); // oder null
                        alert.setContentText("Rechnung wurde erfolgreich geändert!");
                        warningText.setText("");
                        alert.showAndWait();
                    } else {
                        warningText.setText("Es gab ein Problem \nmit der Datenbankverbindung!");
                    }
                }
            } else {
                warningText.setText("Rechnungsbetrag muss positiv \nsein!");
            }
        } else {
            warningText.setText("Rechnungsdatum liegt in der \nZukunft!");
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
        return (invoice.getInvoicenumber().equals(invoiceNumber.getText()) &&
        invoice.getType().equals(invoiceType.getValue()) &&
        convertDateToLocalDate(invoice.getDate()).equals(invoiceDate.getValue()) &&
        invoice.getAmount() == Double.parseDouble(invoiceValue.getText()));
    }

    private LocalDate convertDateToLocalDate(java.util.Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public void invoiceTypeChanged() {
        selectedType = invoiceType.getSelectionModel().getSelectedItem();
        warningText.setText("");
        if (selectedType!=null && invoiceValue.getText()!=null) {
            try {
                invoiceValueDouble = Double.parseDouble(invoiceValue.getText());
            } catch (NumberFormatException e) {
                warningText.setText("Der Rechnungsbetrag muss \neine Zahl sein!");
                return;
            }
            reimbursementValue.setText(invoiceSettingService.getReimbursementValue(selectedType, invoiceValueDouble) + "");
        }
    }
}
