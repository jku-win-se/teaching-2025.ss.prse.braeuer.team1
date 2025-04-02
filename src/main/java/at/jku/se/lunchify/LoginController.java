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
        if (email.getText().isEmpty() || password.getText().isEmpty()) {
            warningText.setText("Login-Daten eingeben!");
        } else {
            if (email.getText().equals("admin") && password.getText().equals("admin")) {
                LunchifyApplication.baseController.showMenu("menu-admin-view.fxml"); //Admin-Menu setzen
            } else {
                LunchifyApplication.baseController.showMenu("menu-user-view.fxml"); //User-Menu setzen
            }
            // Ersten View ins Base-Center setzen
            LunchifyApplication.baseController.showCenterView("upload-view.fxml");
        }
    }

}