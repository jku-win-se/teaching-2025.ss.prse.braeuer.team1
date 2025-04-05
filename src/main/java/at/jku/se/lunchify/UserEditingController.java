package at.jku.se.lunchify;

import at.jku.se.lunchify.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
    protected TextField password;
    @FXML
    protected ChoiceBox<String> userType;
    @FXML

    private User userToEdit;

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
                Boolean isActive = resultSet.getBoolean("isactive");
                Boolean isAnomalous = resultSet.getBoolean("isanomalous");

                email.setText(emailToEdit);
                firstname.setText(firstNameToEdit);
                surname.setText(surnameToEdit);
                password.setText(passwordToEdit);
                userType.setValue(typeToEdit);

                userToEdit = new User(userid, emailToEdit, firstNameToEdit, surnameToEdit, "Admin", isActive, isAnomalous, passwordToEdit);

            }
            System.out.println("userToEdit: " + userToEdit);
        }

    }

    public void onSafeChangesButtonClick() throws SQLException {
        Connection connection = DriverManager.getConnection(jdbcUrl, username, DBpassword);
        PreparedStatement ps = connection.prepareStatement("update \"User\" SET email = ?, firstname = ?, surname = ?, type = ?, isactive = ?, isanomalous = ?, password = ? where \"userid = userToEdit.getUserid()\";");

        ps.setString(1, userToEdit.getEmail());
        ps.setString(2, userToEdit.getFirstname());
        ps.setString(3, userToEdit.getSurname());
        ps.setString(4, userToEdit.getType());
        ps.setBoolean(5, userToEdit.isIsactive());
        ps.setBoolean(6, userToEdit.isIsanomalous());
        ps.setString(7, userToEdit.getPassword());
        ps.executeUpdate();
        ps.close();
        connection.close();
    }
}