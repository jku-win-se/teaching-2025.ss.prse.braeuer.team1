package at.jku.se.lunchify;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class HelloController {
    @FXML
    private Label label;
    @FXML
    private BorderPane mainPane;
    @FXML
    private Pane loginView;

    @FXML
    protected void onLoginButtonClick() {
        ViewLoader object = new ViewLoader();
        Pane view = object.getPage("user-menu-view");
        mainPane.setTop(view);
    }


    @FXML
    private void initialize() {
        /*ViewLoader object = new ViewLoader();
        Pane view = object.getPage("loginView");*/
        mainPane.setCenter(loginView);
        System.out.println("Hello World");
    }
    /*
    private void loadView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent view = loader.load();
            mainPane.setCenter(view);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    */
}