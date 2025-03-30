package at.jku.se.lunchify;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController {

    @FXML
    protected Button loginButton;
    @FXML
    protected TextField email;
    @FXML
    protected PasswordField password;


    public void onLoginButtonClick() throws IOException {
        // Base-Controller abrufen
        BaseController baseController = (BaseController) loginButton.getScene().getRoot().getUserData();

        //Ist Benutzer Admin? // Menüleiste anzeigen
        if (email.getText().equals("admin") && password.getText().equals("admin")) {
            baseController.showMenu("menu-admin-view.fxml"); //Admin-Menu setzen
        } else {
            baseController.showMenu("menu-user-view.fxml"); //User-Menu setzen
        }
        // Ersten View ins Base-Center setzen
        baseController.showCenterView("rechnung-hochladen-view.fxml");
    }

}