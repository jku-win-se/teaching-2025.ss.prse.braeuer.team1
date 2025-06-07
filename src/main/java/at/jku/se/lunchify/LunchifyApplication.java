package at.jku.se.lunchify;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class LunchifyApplication extends Application {

    public static BaseController baseController;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("base-view.fxml"));
        BorderPane root = loader.load();
        Scene scene = new Scene(root, 1024, 768);
        stage.setTitle("Lunchify - hol dir dein Geld zurück, du Geizhals! :-D");
        stage.setScene(scene);
        stage.setMinWidth(1024);
        stage.setMinHeight(768);
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("Logo-Lunchify.jpeg"))));
        stage.show();

        // Controller setzen, damit er von dem LoginController, MenuController etc. erreichbar ist
        LunchifyApplication.baseController = loader.getController();
    }

    public static void main(String[] args) {
        launch();
    }

}

