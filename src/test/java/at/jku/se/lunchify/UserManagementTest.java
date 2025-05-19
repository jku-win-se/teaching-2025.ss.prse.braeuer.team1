package at.jku.se.lunchify;

import javafx.embed.swing.JFXPanel;
import javafx.scene.control.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

//AI-assisted
class UserManagementTest {

    private UserCreationController creationController;
    private UserEditingController editingController;

    @BeforeAll
    static void initJFX() {
        new JFXPanel(); // Init JavaFX Toolkit
    }

    @BeforeEach
    void setUp() {
        creationController = new UserCreationController();
        creationController.email = new TextField("test@example.com");
        creationController.firstname = new TextField("Max");
        creationController.surname = new TextField("Mustermann");
        creationController.password = new PasswordField();
        creationController.password.setText("securepass");
        creationController.userType = new ChoiceBox<>();
        creationController.userType.getItems().addAll("User", "Admin");
        creationController.userType.setValue("User");
        creationController.inactiveCheck = new CheckBox();

        editingController = new UserEditingController();
        editingController.email = new TextField("test@example.com");
        editingController.firstname = new TextField("Max");
        editingController.surname = new TextField("Mustermann");
        editingController.password = new PasswordField();
        editingController.password.setText("securepass");
        editingController.userType = new ChoiceBox<>();
        editingController.userType.getItems().addAll("User", "Admin");
        editingController.userType.setValue("User");
        editingController.inactiveCheck = new CheckBox();
    }

    @Test
    void testValidateInputCreation_AllFieldsFilled_ReturnsTrue() {
        assertTrue(creationController.checkValidInput());
    }

    @Test
    void testValidateInputCreation_MissingFields_ReturnsWarning() {
        creationController.email.setText("");
        assertFalse(creationController.checkValidInput());
    }

    @Test
    void testValidateInputEditing_AllFieldsFilled_ReturnsTrue() {
        assertTrue(editingController.checkValidInput());
    }

    @Test
    void testValidateInputEditing_MissingFields_ReturnsWarning() {
        editingController.email.setText("");
        assertFalse(editingController.checkValidInput());
    }
}