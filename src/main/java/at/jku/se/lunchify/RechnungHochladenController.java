package at.jku.se.lunchify;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class RechnungHochladenController {
    @FXML
    protected Button invoiceUploadButton;
    @FXML
    protected Label warningText;
    @FXML
    protected TextField invoiceValue;
    double invoiceValueDouble;


    public void onInvoiceUploadButtonClick() {

        try {
            invoiceValueDouble = Double.parseDouble(invoiceValue.getText());
            System.out.println(invoiceValueDouble);
            if (invoiceValueDouble <= 0) {
                warningText.setText("Rechnungsbetrag muss positiv sein!");
            } else {
                warningText.setText("");
            }
        } catch (NumberFormatException e) {
            warningText.setText("Rechnungsbetrag muss eine Zahl sein!");
        }

    }
}
