package at.jku.se.lunchify;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.*;

public class UserManagementController {

    @FXML
    protected ComboBox<String> userToEdit;

    String jdbcUrl = "jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:6543/postgres";
    String username = "postgres.yxshntkgvmksefegyfhz";
    String DBpassword = "CaMaKe25!";

    private void populateComboBox() throws SQLException, IOException {
        Connection connection = DriverManager.getConnection(jdbcUrl, username, DBpassword);

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from \"User\" where userid = 1;");
        while (resultSet.next()) {
            String columnValue = resultSet.getString("email");
        }
        connection.close();
    }

    public void onNewUserButtonClick() throws IOException {
        LunchifyApplication.baseController.showCenterView("user-creation-view.fxml");
    }

    public void onEditUserButtonClick() throws IOException {
        LunchifyApplication.baseController.showCenterView("user-editing-view.fxml");
    }



    //Klick auf Spalte soll Änderungs-View aufrufen

}