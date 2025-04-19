package at.jku.se.lunchify;

import at.jku.se.lunchify.models.InvoiceSettingService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class InvoiceSettingController {
    @FXML
    public Label warningText;
    @FXML
    protected TextField valueInvoiceSupermarket;
    @FXML
    protected TextField valueInvoiceRestaurant;
    @FXML
    protected Button changeInvoiceValueButton;



        public void onChangeInvoiceValueButtonClick() {
            String valueSupermarket = valueInvoiceSupermarket.getText().trim();
            String valueRestaurant = valueInvoiceRestaurant.getText().trim();

            InvoiceSettingService service = new InvoiceSettingService();

            if (!service.isValidInput(valueSupermarket) || !service.isValidInput(valueRestaurant)) {
                warningText.setText("Ungültige Eingaben! Nur positive Zahlen oder leere Felder erlaubt.");
                return;
            }

            if (valueSupermarket.isBlank() && valueRestaurant.isBlank()) {
                warningText.setText("Bitte mindestens einen Wert eingeben.");
                return;
            }

            boolean success = service.updateInvoiceSettings(valueSupermarket, valueRestaurant);

            if (success) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Änderung erfolgreich");
                alert.setHeaderText("Rechnungswerte geändert");

                String info = "";
                if (!valueSupermarket.isBlank()) info += "Supermarkt: " + valueSupermarket + "€\n";
                if (!valueRestaurant.isBlank()) info += "Restaurant: " + valueRestaurant + "€";

                alert.setContentText(info.trim());
                alert.showAndWait();
                warningText.setText("");
            } else {
                warningText.setText("Fehler beim Speichern!");
            }
        }

}
