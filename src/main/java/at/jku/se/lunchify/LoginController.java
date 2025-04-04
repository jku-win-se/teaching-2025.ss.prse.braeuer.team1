package at.jku.se.lunchify;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.*;

import java.io.IOException;


public class LoginController {

    @FXML
    protected Button loginButton;
    @FXML
    protected TextField email;
    @FXML
    protected PasswordField password;
    @FXML
    protected Label warningText;


    public void onLoginButtonClick() throws IOException {
        if (email.getText().isEmpty() || password.getText().isEmpty()) {
            warningText.setText("Login-Daten eingeben!");
        } else {
            if (email.getText().equals("admin") && password.getText().equals("admin")) {
                LunchifyApplication.baseController.showMenu("menu-admin-view.fxml"); //Admin-Menu setzen
            } else {
                LunchifyApplication.baseController.showMenu("menu-user-view.fxml"); //User-Menu setzen
            }
            // Ersten View ins Base-Center setzen
            LunchifyApplication.baseController.showCenterView("upload-view.fxml");
        }
    }

    String jdbcUrl = "jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:6543/postgres";
    String username = "postgres.yxshntkgvmksefegyfhz";
    String DBpassword = "CaMaKe25!";


//    @FXML
//    protected void onLoginButtonClick() throws SQLException {
//        Connection connection = DriverManager.getConnection(jdbcUrl, username, DBpassword);
//
//        Statement statement = connection.createStatement();
//        ResultSet resultSet = statement.executeQuery("select * from \"User\" where userid = 1;");
//        while (resultSet.next())
//        {
//            String columnValue = resultSet.getString("email");
//            warningText.setText(columnValue);
//            connection.close();
//        }
//
//    }
}