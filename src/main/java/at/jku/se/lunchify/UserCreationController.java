package at.jku.se.lunchify;

import at.jku.se.lunchify.models.User;
import at.jku.se.lunchify.models.UserDAO;
import at.jku.se.lunchify.security.PasswordService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class UserCreationController {
    @FXML
    public Label warningText;
    @FXML
    protected TextField email;
    @FXML
    protected TextField firstname;
    @FXML
    protected TextField surname;
    @FXML
    protected PasswordField password;
    @FXML
    protected ChoiceBox<String> userType;
    @FXML
    protected CheckBox inactiveCheck;

    private final UserDAO userDAO = new UserDAO();

    public User onUserCreationButtonClick() throws Exception {
        if(email.getText().isEmpty() || firstname.getText().isEmpty() || surname.getText().isEmpty() || password.getText().isEmpty() || userType.getValue()==null) {
            warningText.setText("Alle Felder ausfüllen!");
            return null;
        }
        else {
            String newEmail = email.getText();
            String newFirstname = firstname.getText();
            String newSurname = surname.getText();
            String newPassword = password.getText();
            String newUserType = userType.getValue();
            boolean isactive = !inactiveCheck.isSelected();

            User userToCreate = new User(1, newEmail, newFirstname, newSurname, newUserType, isactive, false, newPassword);

            //User existiert bereits
            if (userDAO.checkUserAlreadyExists(userToCreate)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Benutzeranlage");
                alert.setHeaderText("Benutzer schon vorhanden"); // oder null
                alert.setContentText("Benutzer mit dieser E-Mail ist schon vorhanden");
                alert.showAndWait();
                return null;
            }

            //User kann erfolgreich angelegt werden
            else if (userDAO.insertUser(userToCreate)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Benutzeranlage");
                alert.setHeaderText("Benutzer angelegt"); // oder null
                alert.setContentText("Benutzer wurde erfolgreich angelegt!");
                alert.showAndWait();
                return userToCreate;
            }

            //User kann nicht angelegt werden
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Benutzeranlage");
                alert.setHeaderText("Benutzer nicht angelegt"); // oder null
                alert.setContentText("Benutzer konnte nicht angelegt werden!");
                alert.showAndWait();
                return null;
            }
        }
    }
}