package at.jku.se.lunchify;

import at.jku.se.lunchify.models.User;
import at.jku.se.lunchify.models.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;

//Klasse ruft "nur" andere Views auf bzw. erstellt Export-File, daher nicht sinnvoll testbar (ohne Mocking)

public class UserManagementController {

    @FXML
    protected Button exportCSVButton;

    private File lastUsedDirectory = FileSystemView.getFileSystemView().getHomeDirectory();
    private File chosenDirectory;

    private UserDAO userDAO = new UserDAO();

    /**
     * Loads the view for creating a new user (User Object)
     */
    public void onNewUserButtonClick() throws IOException {
        LunchifyApplication.baseController.showCenterView("user-creation-view.fxml");
    }

    /**
     * Loads the view for editing an existing user
     */
    public void onEditUserButtonClick() throws IOException {
        LunchifyApplication.baseController.showCenterView("user-editing-view.fxml");
    }

    /**
     * Exports all the users with an anomalous flag
     */
    public void onAnomalousUserButtonClick() throws IOException {
        Stage stage = (Stage) exportCSVButton.getScene().getWindow();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(lastUsedDirectory);
        chosenDirectory = directoryChooser.showDialog(stage);
        if (chosenDirectory != null) {
            lastUsedDirectory = chosenDirectory.getParentFile(); // Ordner speichern, falls nochmal geöffnet wird
            FileWriter output = new FileWriter(new File(chosenDirectory.getAbsolutePath() + "/Lunchify-Anomalous-Users-Export-" + LocalDate.now().toString() + ".csv"));
            output.write("Benutzer-ID/Personalnummer;E-Mail;Vorname;Nachname;Typ;Status" + System.lineSeparator());
            for (User user : userDAO.getAnomalousUsers()) {
                output.write(String.valueOf(user.getUserid())+";"+user.getEmail() + ";" + user.getFirstname() + ";" + user.getSurname() + ";" + user.getType() + ";" + user.isIsactive() + System.lineSeparator());
            }
            output.close();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("CSV-Export");
            alert.setHeaderText("Lunchify-Anomalous-Users-Export-" + LocalDate.now().toString() + ".csv\nwurde in "+chosenDirectory.getPath()+" gespeichert");
            alert.setContentText("Ihre Datei wurde gespeichert!");
            alert.showAndWait();
        }
    }
}