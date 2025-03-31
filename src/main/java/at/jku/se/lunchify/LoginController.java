package at.jku.se.lunchify;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    @FXML
    protected Label warningText;


    public void onLoginButtonClick() throws IOException {
        if (email.getText().equals("") || password.getText().equals("")) {
            warningText.setText("Login-Daten eingeben!");
        } else {
            // Base-Controller abrufen
            BaseController baseController = (BaseController) loginButton.getScene().getRoot().getUserData();

            //Ist Benutzer Admin? // Menüleiste anzeigen
            if (email.getText().equals("admin") && password.getText().equals("admin")) {
                baseController.showMenu("menu-admin-view.fxml"); //Admin-Menu setzen
            } else {
                baseController.showMenu("menu-user-view.fxml"); //User-Menu setzen
            }
            // Ersten View ins Base-Center setzen
            baseController.showCenterView("upload-view.fxml");
        }
    }

}