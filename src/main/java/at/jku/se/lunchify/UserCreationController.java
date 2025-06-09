package at.jku.se.lunchify;

import at.jku.se.lunchify.models.User;
import at.jku.se.lunchify.models.UserDAO;
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

    /**
     * Checks if the inputs of the fields are valid
     * <p>
     * Checks if all the inputs are valid. If a single input is invalid, it returns false - otherwise true
     * <p>
     * @return true if all inputs are valid, false if only one is invalid
     */
    public boolean checkValidInput () {
        return !email.getText().isEmpty() && !firstname.getText().isEmpty() && !surname.getText().isEmpty() && !password.getText().isEmpty() && userType.getValue() != null;
    }

    /**
     * Creates a new user and adds him to the database
     * <p>
     * @return User object if insert was successfull
     */
    public User onUserCreationButtonClick() throws Exception {
        if(!checkValidInput()) {
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