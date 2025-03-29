package at.jku.se.lunchify;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class StartController {

    @FXML
    private BorderPane mainPane;

    @FXML
    private void initialize() {
        ViewLoader object = new ViewLoader();
        Pane view = object.getPage("login-view");
        mainPane.setCenter(view);
    }

}