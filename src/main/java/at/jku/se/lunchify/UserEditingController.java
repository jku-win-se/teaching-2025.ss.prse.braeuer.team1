package at.jku.se.lunchify;

import at.jku.se.lunchify.models.User;
import at.jku.se.lunchify.models.UserDAO;
import at.jku.se.lunchify.security.PasswordService;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Objects;

public class UserEditingController {

    @FXML
    protected ComboBox<String> allUsers;
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
    @FXML
    protected Label warningText;

    private User userToEdit;
    private final UserDAO userDAO = new UserDAO();
    private final PasswordService passwordService = new PasswordService();

    public void initialize() {
        ObservableList<String> userList = userDAO.getAllUserMails();
        allUsers.setItems(userList);
    }

    /**
     * If a user gets selected, its data gets loaded into the view
     */
    public void onSelectUser() {
        warningText.setText("");
        String selectedUserEmail = allUsers.getSelectionModel().getSelectedItem();
        if (selectedUserEmail != null) {
            userToEdit = userDAO.getUserByEmail(selectedUserEmail);
            email.setText(userToEdit.getEmail());
            firstname.setText(userToEdit.getFirstname());
            surname.setText(userToEdit.getSurname());
            password.setText(userToEdit.getPassword());
            userType.setValue(userToEdit.getType());
            inactiveCheck.selectedProperty().setValue(!(userToEdit.isIsactive()));
        }
    }

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
     * Saves the changes to the user and updates the database
     */
    public void onSaveChangesButtonClick() throws Exception {
        if(!checkValidInput()) {
            warningText.setText("Alle Felder ausfüllen!");
        }
        else {
            warningText.setText("");
            User editedUser = new User(userToEdit.getUserid(), email.getText(), firstname.getText(), surname.getText(), userType.getValue(), (!(inactiveCheck.isSelected())), userToEdit.isIsanomalous(), userToEdit.getPassword());
            //Passwort wurde geändert
            if (!password.getText().equals(userToEdit.getPassword())) {
                editedUser.setPassword(passwordService.hashPassword(password.getText()));
            }

            //Email-Adresse wurde geändert
            if (!Objects.equals(email.getText(), userToEdit.getEmail())) {
                //check ob neue Email schon exisitiert
                if (userDAO.getUserByEmail(editedUser.getEmail()) != null) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Benutzeranlage");
                    alert.setHeaderText("Benutzer schon vorhanden"); // oder null
                    alert.setContentText("Benutzer mit dieser E-Mail ist schon vorhanden");
                    alert.showAndWait();
                }
            } else if (userDAO.updateUser(editedUser)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Benutzeränderung");
                alert.setHeaderText("Benutzer geändert"); // oder null
                alert.setContentText("Benutzer wurde erfolgreich geändert!");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Benutzeränderung");
                alert.setHeaderText("Benutzer nicht geändert"); // oder null
                alert.setContentText("Benutzer konnte nicht geändert werden!");
                alert.showAndWait();
            }
        }
    }
}