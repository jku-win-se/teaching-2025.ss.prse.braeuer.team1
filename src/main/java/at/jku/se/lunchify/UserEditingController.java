package at.jku.se.lunchify;

import at.jku.se.lunchify.models.User;
import at.jku.se.lunchify.security.PasswordService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
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
    PasswordService passwordService = new PasswordService();

    String jdbcUrl = "jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:6543/postgres";
    String username = "postgres.yxshntkgvmksefegyfhz";
    String DBpassword = "CaMaKe25!";

    public void initialize() {
        List<String> users = getAllUsers();
        ObservableList<String> userList = FXCollections.observableList(users);
        allUsers.setItems(userList);
        System.out.println("allUsers: " + users);
    }

    public List<String> getAllUsers() {
        List<String> users = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, DBpassword)) {
            String sql = "SELECT email FROM \"User\"";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                String userEmail = resultSet.getString("email");
                users.add(userEmail);
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public void onSelectUserButtonClick() throws Exception {
        String selectedUserEmail = allUsers.getSelectionModel().getSelectedItem();
        ResultSet resultSet = null;
        if (selectedUserEmail != null) {
            try (Connection connection = DriverManager.getConnection(jdbcUrl, username, DBpassword)) {
                String sql = "SELECT * FROM \"User\" WHERE email = '" + selectedUserEmail + "'";
                Statement statement = connection.createStatement();
                resultSet = statement.executeQuery(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            while (resultSet.next()) {
                int userid = resultSet.getInt("userid");
                String emailToEdit = resultSet.getString("email");
                String firstNameToEdit = resultSet.getString("firstname");
                String surnameToEdit = resultSet.getString("surname");
                String passwordToEdit = resultSet.getString("password");
                String typeToEdit = resultSet.getString("type");
                boolean isActive = resultSet.getBoolean("isactive");
                boolean isAnomalous = resultSet.getBoolean("isanomalous");

                email.setText(emailToEdit);
                firstname.setText(firstNameToEdit);
                surname.setText(surnameToEdit);
                //password.setText(passwordToEdit);
                userType.setValue(typeToEdit);
                inactiveCheck.selectedProperty().setValue(!(isActive));

                userToEdit = new User(userid, emailToEdit, firstNameToEdit, surnameToEdit, typeToEdit, isActive, isAnomalous, passwordToEdit);

            }
            System.out.println("userToEdit: " + userToEdit);
        }
    }

    public void onSaveChangesButtonClick() throws SQLException {
        Connection connection = DriverManager.getConnection(jdbcUrl, username, DBpassword);
        try {
            if(!Objects.equals(email.getText(), userToEdit.getEmail())) {
                PreparedStatement checkps = connection.prepareStatement("SELECT userid FROM \"User\" where email = ?");
                checkps.setString(1, email.getText());
                ResultSet rs = checkps.executeQuery();
                while (rs.next()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Benutzeranlage");
                    alert.setHeaderText("Benutzer schon vorhanden"); // oder null
                    alert.setContentText("Benutzer mit dieser E-Mail ist schon vorhanden");
                    alert.showAndWait();
                    return;
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        PreparedStatement ps = connection.prepareStatement("update \"User\" SET email = ?, firstname = ?, surname = ?, type = ?, isactive = ?, password = ? where userid = ?;");

        ps.setString(1, email.getText());
        ps.setString(2, firstname.getText());
        ps.setString(3, surname.getText());
        ps.setString(4, userType.getValue());
        ps.setBoolean(5, !(inactiveCheck.isSelected()));
        ps.setString(6, passwordService.hashPassword(password.getText().trim()));
        ps.setInt(7,userToEdit.getUserid());
        ps.executeUpdate();
        ps.close();
        connection.close();

        //besser vorher noch Check, ob erfolgreich upgedatet wurde
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Benutzeränderung");
        alert.setHeaderText("Benutzer geändert"); // oder null
        alert.setContentText("Benutzer wurde erfolgreich geändert!");
        alert.showAndWait();
        return;
    }
}