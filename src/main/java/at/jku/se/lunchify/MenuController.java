package at.jku.se.lunchify;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;


public class MenuController {

    @FXML
    private Button logout;


    public void onLogoutButtonClick() throws IOException {
        // Aktuelle Stage holen
        Stage stage = (Stage) logout.getScene().getWindow();

        // FXML laden
        FXMLLoader loader = new FXMLLoader(getClass().getResource("base-view.fxml"));
        BorderPane root = loader.load();

        // Controller setzen, damit er von der Login-View erreichbar ist
        LunchifyApplication.baseController = loader.getController();

        // Szene setzen
        Scene scene = new Scene(root); // Hier wird root verwendet
        stage.setScene(scene);
        stage.show();
    }

    public void onUploadButtonClick() throws IOException {
        LunchifyApplication.baseController.showCenterView("upload-view.fxml");
    }

    public void onHistoryButtonClick() throws IOException {
        LunchifyApplication.baseController.showCenterView("history-view.fxml");
    }

    public void onUserManagementButtonClick() throws IOException {
        LunchifyApplication.baseController.showCenterView("user-management-view.fxml");
    }

    public void onInvoiceSettingButtonClick() throws IOException {
        LunchifyApplication.baseController.showCenterView("invoice-setting-view.fxml");
    }

    public void onReportButtonClick() throws IOException {
        LunchifyApplication.baseController.showCenterView("report-view.fxml");
    }

    public void onInvoiceClearanceButtonClick() throws IOException {
        LunchifyApplication.baseController.showCenterView("invoiceClearing-view.fxml");
    }
}
