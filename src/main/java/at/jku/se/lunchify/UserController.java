package at.jku.se.lunchify;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UserController {

    @FXML
    protected BorderPane mainPane;

    @FXML

    public void onLoginButtonClick(javafx.event.ActionEvent event) throws IOException {
        BorderPane mainPane = FXMLLoader.load(getClass().getResource("base-view.fxml"));
        Scene theOneScene = new Scene(mainPane, 1024, 768);

        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
        window.setScene(theOneScene);
        window.show();

        ViewLoader object = new ViewLoader();
        Pane view = object.getPage("user-menu-view");
        mainPane.setTop(view);
    }
}