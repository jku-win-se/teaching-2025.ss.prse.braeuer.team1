package at.jku.se.lunchify;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class UserManagementController {

    @FXML
    protected Button newUserButton;

    public void onNewUserButtonClick() throws IOException {
        LunchifyApplication.baseController.showCenterView("user-creation-view.fxml");
    }

    public void onEditUserButtonClick() {
    }

    //Klick auf Spalte soll Änderungs-View aufrufen

}