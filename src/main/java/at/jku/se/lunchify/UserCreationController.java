package at.jku.se.lunchify;

import at.jku.se.lunchify.models.User;
import at.jku.se.lunchify.security.PasswordService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;

public class UserCreationController {

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

    String jdbcUrl = "jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:6543/postgres";
    String username = "postgres.yxshntkgvmksefegyfhz";
    String DBpassword = "CaMaKe25!";
    PasswordService passwordService = new PasswordService();


    public void onUserCreationButtonClick() throws SQLException {
        Connection connection = DriverManager.getConnection(jdbcUrl, username, DBpassword);

        try {
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
        catch (SQLException e) {
            e.printStackTrace();
        }
        PreparedStatement ps = connection.prepareStatement("insert into \"User\" (email, firstname, surname, type, isactive, password) values (?,?,?,?,?,?);");

        ps.setString(1, email.getText());
        ps.setString(2, firstname.getText());
        ps.setString(3, surname.getText());
        ps.setString(4, userType.getValue());
        ps.setBoolean(5, !(inactiveCheck.isSelected()));
        ps.setString(6, passwordService.hashPassword(password.getText().trim()));
        ps.executeUpdate();
        ps.close();
        connection.close();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Benutzeranlage");
        alert.setHeaderText("Benutzer angelegt"); // oder null
        alert.setContentText("Benutzer wurde erfolgreich angelegt!");
        alert.showAndWait();
    }
}