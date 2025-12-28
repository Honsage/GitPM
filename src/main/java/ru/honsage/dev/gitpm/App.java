package ru.honsage.dev.gitpm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ru.honsage.dev.gitpm.application.services.*;
import ru.honsage.dev.gitpm.domain.ports.CommandExecutor;
import ru.honsage.dev.gitpm.domain.ports.GitOperations;
import ru.honsage.dev.gitpm.domain.repositories.ProjectRepository;
import ru.honsage.dev.gitpm.domain.repositories.ScriptRepository;
import ru.honsage.dev.gitpm.domain.repositories.TaskRepository;
import ru.honsage.dev.gitpm.infrastructure.executor.ProcessBuilderCommandExecutor;
import ru.honsage.dev.gitpm.infrastructure.git.JGitOperations;
import ru.honsage.dev.gitpm.infrastructure.persistence.sqlite.DatabaseManager;
import ru.honsage.dev.gitpm.infrastructure.persistence.sqlite.repositories.ProjectRepositoryImpl;
import ru.honsage.dev.gitpm.infrastructure.persistence.sqlite.repositories.ScriptRepositoryImpl;
import ru.honsage.dev.gitpm.infrastructure.persistence.sqlite.repositories.TaskRepositoryImpl;
import ru.honsage.dev.gitpm.presentation.controllers.MainController;
import ru.honsage.dev.gitpm.presentation.viewmodels.MainViewModel;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("fxml/main.fxml"));

        DatabaseManager db = DatabaseManager.getInstance("gitpm.db");
        ProjectRepository projectRepo = new ProjectRepositoryImpl(db);
        TaskRepository taskRepo = new TaskRepositoryImpl(db);
        ScriptRepository scriptRepo = new ScriptRepositoryImpl(db);

        GitOperations git = new JGitOperations();
        CommandExecutor executor = new ProcessBuilderCommandExecutor();

        ProjectService projectService = new ProjectService(projectRepo, git);
        TaskService taskService = new TaskService(taskRepo);
        ScriptService scriptService = new ScriptService(scriptRepo);
        ScriptExecutionService executionService = new ScriptExecutionService(executor);

        MainViewModel viewModel = new MainViewModel(
                projectService,
                taskService,
                scriptService,
                executionService
        );

        MainController mainController = new MainController(viewModel);
        mainController.setGitClient(git);
        fxmlLoader.setControllerFactory(_ -> mainController);
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 800,500);
        stage.setMinHeight(250);
        stage.setMinWidth(600);
        stage.setTitle("GitPM");
        stage.getIcons().add(new Image(String.valueOf(App.class.getResource("images/icon.png"))));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}