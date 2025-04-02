package at.jku.se.lunchify;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.*;
import java.io.IOException;

public class HelloController {
    String jdbcUrl = "jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:6543/postgres";
    String username = "postgres.yxshntkgvmksefegyfhz";
    String password = "CaMaKe25!";

    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() throws SQLException {
        Connection connection = DriverManager.getConnection(jdbcUrl, username, password);

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from \"User\" where userid = 1;");
        while (resultSet.next())
        {
            String columnValue = resultSet.getString("email");
            welcomeText.setText(columnValue);
            connection.close();
        }

    }

}