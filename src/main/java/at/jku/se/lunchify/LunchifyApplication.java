package at.jku.se.lunchify;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class LunchifyApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("base-view.fxml"));
        BorderPane root = loader.load();
        Scene scene = new Scene(root, 1024, 768);
        stage.setTitle("Lunchify - hol dir dein Geld zurück, du Geizhals! :-D");
        stage.setScene(scene);
        stage.show();

        // Controller setzen, damit er von dem LoginController, UserMenuController etc. erreichbar ist
        BaseController baseController = loader.getController();
        root.setUserData(baseController);
    }

    public static void main(String[] args) {
        launch();
    }

}