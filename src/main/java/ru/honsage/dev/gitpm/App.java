package ru.honsage.dev.gitpm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ru.honsage.dev.gitpm.application.services.ProjectService;
import ru.honsage.dev.gitpm.domain.ports.GitOperations;
import ru.honsage.dev.gitpm.domain.repositories.ProjectRepository;
import ru.honsage.dev.gitpm.infrastructure.git.JGitOperations;
import ru.honsage.dev.gitpm.infrastructure.persistence.sqlite.DatabaseManager;
import ru.honsage.dev.gitpm.infrastructure.persistence.sqlite.repositories.ProjectRepositoryImpl;
import ru.honsage.dev.gitpm.presentation.controllers.MainController;
import ru.honsage.dev.gitpm.presentation.viewmodels.MainViewModel;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("fxml/main.fxml"));
        DatabaseManager db = DatabaseManager.getInstance("gitpm.db");
        ProjectRepository projectRepo = new ProjectRepositoryImpl(db);
        GitOperations git = new JGitOperations();
        ProjectService projectService = new ProjectService(projectRepo, git);
        MainViewModel viewModel = new MainViewModel(projectService);
        fxmlLoader.setControllerFactory(_ -> new MainController(viewModel));
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