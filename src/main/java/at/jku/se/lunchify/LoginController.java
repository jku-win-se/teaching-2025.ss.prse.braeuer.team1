package at.jku.se.lunchify;

import at.jku.se.lunchify.security.PasswordService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.*;

public class LoginController {

    // Datenbank-Zugangsdaten
    private static final String JDBC_URL = "jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:6543/postgres";
    private static final String DB_USER = "postgres.yxshntkgvmksefegyfhz";
    private static final String DB_PASSWORD = "CaMaKe25!";

    @FXML
    protected Button loginButton;
    @FXML
    protected TextField email;
    @FXML
    protected PasswordField password;
    @FXML
    protected Label warningText;

    public static int currentUserId;

    PasswordService passwordService = new PasswordService();

    //AI-Assisted
    public void onLoginButtonClick() {
        String userEmail = email.getText().trim();
        String userPassword = password.getText().trim();

        // Falls die Felder leer sind, eine Warnung anzeigen
        if (userEmail.isEmpty() || userPassword.isEmpty()) {
            warningText.setText("Login-Daten eingeben!");
            return;
        }

        // Verbindung zur Datenbank herstellen und Benutzer prüfen
        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT userid, type, isactive, password FROM public.\"User\" WHERE email = ?";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, userEmail);
                try (ResultSet rs = pstmt.executeQuery()) {

                    if (rs.next()) {
                        // Werte aus der Datenbank abrufen
                        String userType = rs.getString("type");
                        boolean isActive = rs.getBoolean("isactive");
                        String dbPassword = rs.getString("password"); // ⚠ Später Hashing verwenden!

                        // Prüfen, ob der User aktiv ist
                        if (!isActive) {
                            warningText.setText("User inaktiv!");
                            return;
                        }

                        // Passwort prüfen
                        if (!passwordService.verifyPassword(userPassword,dbPassword)/*!userPassword.equals(dbPassword)*/) {
                            warningText.setText("Falsches Passwort!");
                            return;
                        }

                        // Menü je nach Benutzerrolle setzen
                        if ("Admin".equals(userType)) {
                            LunchifyApplication.baseController.showMenu("menu-admin-view.fxml");
                        } else {
                            LunchifyApplication.baseController.showMenu("menu-user-view.fxml");
                        }

                        // Erste View nach dem Login ins Base-Center setzen
                        LunchifyApplication.baseController.showCenterView("upload-view.fxml");
                        currentUserId = rs.getInt("userid");

                    } else {
                        warningText.setText("Kein gültiger User!");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            warningText.setText("Fehler bei der Anmeldung!");
        }
    }

}