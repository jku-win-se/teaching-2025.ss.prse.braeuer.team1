package at.jku.se.lunchify;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;


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
    private Button invoiceSettingButton;


    public void onLogoutButtonClick() throws IOException {
        // Aktuelle Stage holen
        Stage stage = (Stage) logout.getScene().getWindow();

        // FXML laden
        FXMLLoader loader = new FXMLLoader(getClass().getResource("base-view.fxml"));
        BorderPane root = loader.load();

        // Controller setzen, damit er von der Login-View erreichbar ist
        BaseController baseController = loader.getController();
        root.setUserData(baseController);

        // Szene setzen
        Scene scene = new Scene(root); // Hier wird root verwendet
        stage.setScene(scene);
        stage.show();
    }

    public void onUploadButtonClick() throws IOException {
        BaseController baseController = (BaseController) uploadButton.getScene().getRoot().getUserData();
        baseController.showCenterView("upload-view.fxml");
    }

    public void onHistoryButtonClick() throws IOException {
        BaseController baseController = (BaseController) historyButton.getScene().getRoot().getUserData();
        baseController.showCenterView("history-view.fxml");
    }


    public void onUserManagementButtonClick() throws IOException {
            BaseController baseController = (BaseController) userManagementButton.getScene().getRoot().getUserData();
            baseController.showCenterView("user-management-view.fxml");
    }

    public void onInvoiceSettingButtonClick() throws IOException {
        BaseController baseController = (BaseController) invoiceSettingButton.getScene().getRoot().getUserData();
        baseController.showCenterView("invoice-setting-view.fxml");
    }

    public void onReportButtonClick() throws IOException {
        //ReportView offen
    };
}
