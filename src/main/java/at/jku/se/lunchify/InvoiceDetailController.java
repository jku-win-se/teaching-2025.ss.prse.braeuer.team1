package at.jku.se.lunchify;

import at.jku.se.lunchify.models.Invoice;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.ByteArrayInputStream;

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

    public void setInvoice(Invoice invoice) {
        invoiceValue.setText(String.valueOf(invoice.getAmount()));
        reimbursementValue.setText(String.valueOf(invoice.getReimbursementAmount()));
        invoiceType.setValue(invoice.getType());
        invoiceNumber.setText(String.valueOf(invoice.getInvoicenumber()));
        //invoiceDate.setValue(invoice.getDate().toInstant().atZone(ZoneId.systemDefault().toLocalDate()));

        //AI generated -> nicht bei PDF möglich
        if (invoice.getFile() != null && invoice.getFile().length > 0) {
            invoiceImage.setImage(new Image(new ByteArrayInputStream(invoice.getFile())));
        }
        else System.out.println("Rechnung ist nicht da");
    }

    public void onClearButtonClick() {}

    public void onDeclineButtonClick() {}

}
