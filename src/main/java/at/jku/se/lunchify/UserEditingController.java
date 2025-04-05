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

    public void onSelectUserButtonClick() throws SQLException {
        String selectedUserEmail = allUsers.getSelectionModel().getSelectedItem();
        ResultSet resultSet = null;
        User selectedUser = null;
        if (selectedUserEmail != null) {
            try (Connection connection = DriverManager.getConnection(jdbcUrl, username, DBpassword)) {
                String sql = "SELECT * FROM \"User\" WHERE email = '" + selectedUserEmail + "'";
                Statement statement = connection.createStatement();
                resultSet = statement.executeQuery(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            while (resultSet.next()) {
                email.setText(resultSet.getString("email"));
                firstname.setText(resultSet.getString("firstname"));
                surname.setText(resultSet.getString("surname"));
                password.setText(resultSet.getString("password"));
                userType.setValue(resultSet.getString("type"));
            }
        }
    }
}