package at.jku.se.lunchify;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class HelloController {

    @FXML
    private BorderPane mainPane;

    @FXML
    protected void onLoginButtonClick() {
        ViewLoader object = new ViewLoader();
        Pane view = object.getPage("user-menu-view");
        mainPane.setTop(view);
    }


    @FXML
    private void initialize() {
        ViewLoader object = new ViewLoader();
        Pane view = object.getPage("login-view");
        mainPane.setCenter(view);
    }

}