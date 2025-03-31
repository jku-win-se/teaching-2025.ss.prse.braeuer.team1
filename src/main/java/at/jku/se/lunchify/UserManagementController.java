package at.jku.se.lunchify;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class UserManagementController {

    @FXML
    protected Button newUserButton;

    public void onNewUserButtonClick() throws IOException {
        BaseController baseController = (BaseController) newUserButton.getScene().getRoot().getUserData();
        baseController.showCenterView("user-creation-view.fxml");
    }

    //Klick auf Spalte soll Änderungs-View aufrufen

}