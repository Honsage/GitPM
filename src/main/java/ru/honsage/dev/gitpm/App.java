package ru.honsage.dev.gitpm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("fxml/main.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 800,600);
        stage.setTitle("GitPM – Git Project Manager");
        stage.getIcons().add(new Image(String.valueOf(App.class.getResource("images/icon.png"))));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}