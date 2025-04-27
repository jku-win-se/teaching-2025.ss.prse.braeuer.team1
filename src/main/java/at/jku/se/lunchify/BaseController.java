package at.jku.se.lunchify;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

//Klasse zum Steuern des Hauptviews
public class BaseController {

    @FXML
    protected BorderPane basePane;

    //Lade FXML view zum setzen
    public Parent loadView(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        return loader.load();
    }

    //Setze den Hauptview im base-view (center)
    public void showCenterView(String fxml) throws IOException {
            basePane.setCenter(loadView(fxml));
    }

    //Setze den MenüView im base-view (top)
    public void showMenu(String fxml) throws IOException {
                basePane.setTop(loadView(fxml));
    }
}