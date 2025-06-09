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
 * Klasse zum Bearbeiten und Löschen der eigenen Rechnungen als User (nur eigene Rechnungen)
 */

public class MyInvoiceDetailController {
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

    /**
     * Method setting the chosen Invoice into a detailed view with a display of the picture/file in the database
     * <p>
     * Method setting the chosen Invoice into a detailed view with a display of the picture/file in the database (in the applocation or in the standard PDF reader)
     * @param invoice Invoice to be loaded
     */
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

    /**
     * Sets the current possible Invoice types into the view
     */
    public void showAllInvoiceTypes() {
        invoiceType.setItems(invoiceSettingService.getAllInvoiceTypes());
    }

    /**
     * Saves changes made to the current Invoice
     */
    public void onSaveChangesButtonClick() {
        warningText.setText("");
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
                }
                else if (invoiceDate.getValue().getMonthValue() != LocalDate.now().getMonthValue() ||
                        invoiceDate.getValue().getYear() != LocalDate.now().getYear()) {
                        warningText.setText("Es können nur Rechnungen aus \ndem aktuellen Monat bearbeitet \nwerden.");
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

    /**
     * Deletes the current Invoice
     */
    public void onDeleteButtonClick() {
        if (invoiceDAO.deleteInvoice(invoice.getInvoiceid())) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Rechnungslöschung");
            alert.setHeaderText("Rechnung gelöscht");
            alert.setContentText("Rechnung wurde erfolgreich gelöscht!");
            alert.showAndWait();
            // AI-assisted: Fenster schließen
            Stage currentStage = (Stage) warningText.getScene().getWindow();
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

    /**
     * Converts a Date Object into a LocalDate Object
     * <p>
     * Converts a Date Object into a LocalDate Object that is needed for JavaFx and PostgreSQL database
     * <p>
     * @param date date to be converted
     * @return LocalDate Object of the given date
     */
    private LocalDate convertDateToLocalDate(java.util.Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    /**
     * Checks if the Invoice type has been changed
     * <p>
     * If the Invoice type has been changed it checks all the necessary data and displays a warning text and changes the reimbursement amount if necessary
     */
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
