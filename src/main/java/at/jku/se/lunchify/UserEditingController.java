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

    private User userToEdit;
    private UserDAO userDAO = new UserDAO();
    private PasswordService passwordService = new PasswordService();

    public void initialize() {
        ObservableList<String> userList = userDAO.getAllUserMails();
        allUsers.setItems(userList);
    }

    public void onSelectUserButtonClick() throws Exception {
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

    public void onSaveChangesButtonClick() throws Exception {
        User editedUser = new User(userToEdit.getUserid(), email.getText(), firstname.getText(), surname.getText(), userType.getValue(), (!(inactiveCheck.isSelected())), userToEdit.isIsanomalous(), userToEdit.getPassword());
        //Passwort wurde geändert
        if(!password.getText().equals(userToEdit.getPassword())) {
            editedUser.setPassword(passwordService.hashPassword(password.getText()));
        }

        //Email-Adresse wurde geändert
        if(!Objects.equals(email.getText(), userToEdit.getEmail())) {
            //check ob neue Email schon exisitiert
            if(userDAO.getUserByEmail(editedUser.getEmail()) != null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Benutzeranlage");
                alert.setHeaderText("Benutzer schon vorhanden"); // oder null
                alert.setContentText("Benutzer mit dieser E-Mail ist schon vorhanden");
                alert.showAndWait();
            }
        }

        else if (userDAO.updateUser(editedUser)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Benutzeränderung");
            alert.setHeaderText("Benutzer geändert"); // oder null
            alert.setContentText("Benutzer wurde erfolgreich geändert!");
            alert.showAndWait();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Benutzeränderung");
            alert.setHeaderText("Benutzer nicht geändert"); // oder null
            alert.setContentText("Benutzer konnte nicht geändert werden!");
            alert.showAndWait();
        }
    }
}