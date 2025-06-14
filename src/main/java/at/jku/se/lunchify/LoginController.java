package at.jku.se.lunchify;

import at.jku.se.lunchify.models.LoginService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Testing nicht sinnvoll, da LoginService getestet wird
 */

public class LoginController {

    @FXML
    protected Button loginButton;
    @FXML
    protected TextField email;
    @FXML
    protected PasswordField password;
    @FXML
    protected Label warningText;

    public static int currentUserId;
    public static LoginService.LoginResult currentUserType;

    //AI-Assisted
    /**
     * Depending on the result of a login attempt, the user gets an error message or gets logged in for his role / user type
     */
    public void onLoginButtonClick() {
        String userEmail = email.getText().trim();
        String userPassword = password.getText().trim();

        LoginService loginService = new LoginService();
        LoginService.LoginResult result = loginService.login(userEmail, userPassword);

        try {
            switch (result) {
                case EMPTY_FIELDS -> warningText.setText("Login-Daten eingeben!");
                case USER_INACTIVE -> warningText.setText("User inaktiv!");
                case INVALID_PW -> warningText.setText("Falsches Passwort!");
                case INVALID_USER -> warningText.setText("Kein gültiger User!");
                case ERROR -> warningText.setText("Fehler bei der Anmeldung!");
                case SUCCESS_ADMIN, SUCCESS_USER -> {
                    currentUserType = result;
                    LunchifyApplication.baseController.showMenu("menu-admin-view.fxml");
                    LunchifyApplication.baseController.showCenterView("upload-view.fxml");
                    currentUserId = loginService.getUserId();
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            warningText.setText("Fehler bei der Anwendung!");
        }
    }

}