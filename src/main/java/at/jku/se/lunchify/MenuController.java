package at.jku.se.lunchify;

import at.jku.se.lunchify.models.LoginService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

/**
 * Testing nicht sinnvoll, weil Klasse Menuview steuert (Aufruf anderer Views)
 */

public class MenuController {

    @FXML
    private Button logout;
    @FXML
    private Button uploadButton;
    @FXML
    private Button historyButton;
    @FXML
    private Button userManagementButton;
    @FXML
    private Button invoiceClearanceButton;
    @FXML
    private Button invoiceSettingButton;
    @FXML
    private Button reportButton;

    public void initialize() {
        setActiveMenuButton(uploadButton);
        if (LoginController.currentUserType == LoginService.LoginResult.SUCCESS_USER) {
            userManagementButton.setVisible(false);
            invoiceClearanceButton.setVisible(false);
            invoiceSettingButton.setVisible(false);
            reportButton.setVisible(false);
        }
    }

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
        setActiveMenuButton(uploadButton);
    }

    public void onHistoryButtonClick() throws IOException {
        LunchifyApplication.baseController.showCenterView("history-view.fxml");
        setActiveMenuButton(historyButton);
    }

    public void onUserManagementButtonClick() throws IOException {
        LunchifyApplication.baseController.showCenterView("user-management-view.fxml");
        setActiveMenuButton(userManagementButton);
    }

    public void onInvoiceSettingButtonClick() throws IOException {
        LunchifyApplication.baseController.showCenterView("invoice-setting-view.fxml");
        setActiveMenuButton(invoiceSettingButton);
    }

    public void onReportButtonClick() throws IOException {
        LunchifyApplication.baseController.showCenterView("report-view.fxml");
        setActiveMenuButton(reportButton);
    }

    public void onInvoiceClearanceButtonClick() throws IOException {
        LunchifyApplication.baseController.showCenterView("invoiceClearing-view.fxml");
        setActiveMenuButton(invoiceClearanceButton);
    }

    //AI-generated
    private void setActiveMenuButton(Button activeButton) {
        List<Button> allButtons = List.of(uploadButton, historyButton, userManagementButton, invoiceClearanceButton, invoiceSettingButton, reportButton);

        for (Button btn : allButtons) {
            btn.getStyleClass().remove("menu-button-active");
        }
        activeButton.getStyleClass().add("menu-button-active");
    }
}
