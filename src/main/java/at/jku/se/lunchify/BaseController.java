package at.jku.se.lunchify;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class BaseController {

    @FXML
    protected BorderPane basePane;

    public Parent loadView(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        Parent view = loader.load();
        return view;
    }

    public void showCenterView(String fxml) throws IOException {
            basePane.setCenter(loadView(fxml));
    }

    public void showMenu(Boolean isAdmin) throws IOException {
            if(isAdmin) {
                basePane.setTop(loadView("admin-menu-view.fxml"));
            } else {
                basePane.setTop(loadView("user-menu-view.fxml"));
            }
    }



}