package ru.honsage.dev.gitpm.presentation.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import ru.honsage.dev.gitpm.domain.ports.GitOperations;
import ru.honsage.dev.gitpm.presentation.dto.ProjectDTO;

import java.io.File;
import java.nio.file.Paths;
import java.util.Optional;

public class AddProjectDialogController {
    @FXML
    protected TextField titleField;
    @FXML
    protected TextArea descriptionField;
    @FXML
    protected TextField localPathField;
    @FXML
    protected TextField remoteUrlField;
    @FXML
    protected Label errorLabel;

    private Stage stage;
    private GitOperations git;
    private ProjectDTO result;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setGitClient(GitOperations git) {
        this.git = git;
    }

    public Optional<ProjectDTO> getResult() {
        return Optional.ofNullable(result);
    }

    @FXML
    private void onChooseDirectory() {
        DirectoryChooser chooser = new DirectoryChooser();
        File dir = chooser.showDialog(stage);
        if (dir != null) {
            localPathField.setText(dir.getAbsolutePath());
        }
    }

    @FXML
    private void onCreate() {
        if (titleField.getText().isBlank()) {
            errorLabel.setText("Введите название проекта");
            titleField.requestFocus();
            return;
        }

        if (localPathField.getText().isBlank()) {
            errorLabel.setText("Выберите локальную директорию с Git проектом");
            localPathField.requestFocus();
            return;
        }

        if (git != null && !git.isGitRepository(Paths.get( localPathField.getText()))) {
            errorLabel.setText("Указанная директория должна являться Git-репозиторием");
            localPathField.requestFocus();
            return;
        }

        result = new ProjectDTO(
                null,
                titleField.getText().trim(),
                descriptionField.getText().trim(),
                localPathField.getText().trim(),
                remoteUrlField.getText().isBlank()
                        ? null
                        : remoteUrlField.getText().trim(),
                null
        );

        stage.close();
    }

    @FXML
    private void onCancel() {
        stage.close();
    }
}
