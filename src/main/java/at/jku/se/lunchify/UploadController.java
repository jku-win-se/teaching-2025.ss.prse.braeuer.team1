package at.jku.se.lunchify;

import javafx.fxml.FXML;
import javafx.scene.control.*;

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
}
