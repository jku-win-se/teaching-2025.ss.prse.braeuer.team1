package at.jku.se.lunchify;

import at.jku.se.lunchify.models.InvoiceSettingService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.io.IOException;

/**
 * Testing nicht sinnvoll, da InvoiceSettingService getestet wird
 */
//AI-Assisted
public class InvoiceSettingController {
    @FXML
    protected Label warningText;
    @FXML
    protected TextField valueInvoiceSupermarket;
    @FXML
    protected TextField valueInvoiceRestaurant;
    @FXML
    protected Button changeInvoiceValueButton;

    InvoiceSettingService service = new InvoiceSettingService();

    public void initialize() {
        double valueSupermarket = service.getCurrentSupermarketValue();
        double valueRestaurant = service.getCurrentRestaurantValue();

        valueInvoiceSupermarket.setPromptText("Supermarkt aktuell: " + valueSupermarket + "€");
        valueInvoiceRestaurant.setPromptText("Restaurant aktuell: " + valueRestaurant + "€");
    }

        public void onChangeInvoiceValueButtonClick() throws IOException {
            String valueSupermarket = valueInvoiceSupermarket.getText().trim();
            String valueRestaurant = valueInvoiceRestaurant.getText().trim();

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
                alert.setHeaderText("Refundierungsbeträge geändert");

                String info = "";
                if (!valueSupermarket.isBlank()) info += "Supermarkt: " + valueSupermarket + "€\n";
                if (!valueRestaurant.isBlank()) info += "Restaurant: " + valueRestaurant + "€";

                alert.setContentText(info.trim());
                warningText.setText("");
                alert.showAndWait();
                LunchifyApplication.baseController.showCenterView("invoice-setting-view.fxml");
            } else {
                warningText.setText("Fehler beim Speichern!");
            }
        }

}
