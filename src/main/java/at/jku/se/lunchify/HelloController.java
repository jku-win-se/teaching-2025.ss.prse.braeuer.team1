package at.jku.se.lunchify;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

public class HelloController {
    @FXML
    private Label label;
    @FXML
    private BorderPane mainPane;


    @FXML
    private void initialize() {
        loadView("login-view.fxml");
    }

    private void loadView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent view = loader.load();
            mainPane.setCenter(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}