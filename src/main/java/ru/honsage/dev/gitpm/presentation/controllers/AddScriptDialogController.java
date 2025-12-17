package ru.honsage.dev.gitpm.presentation.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import ru.honsage.dev.gitpm.presentation.dto.SimpleScriptDTO;

import java.io.File;
import java.util.Optional;

public class AddScriptDialogController {
    @FXML
    protected TextField titleField;
    @FXML
    protected TextArea descriptionField;
    @FXML
    protected TextField workingDirField;
    @FXML
    protected TextArea commandField;
    @FXML
    protected Label errorLabel;

    private Stage stage;
    private SimpleScriptDTO result;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setDefaultDir(String localPath) {
        workingDirField.setText(localPath);
    }

    public Optional<SimpleScriptDTO> getResult() {
        return Optional.ofNullable(result);
    }

    @FXML
    private void onChooseDirectory() {
        DirectoryChooser chooser = new DirectoryChooser();
        File dir = chooser.showDialog(stage);
        if (dir != null) {
            workingDirField.setText(dir.getAbsolutePath());
        }
    }

    @FXML
    private void onCreate() {
        if (titleField.getText().isBlank()) {
            errorLabel.setText("Введите название сценария");
            titleField.requestFocus();
            return;
        }

        if (workingDirField.getText().isBlank()) {
            errorLabel.setText("Выберите рабочую директорию");
            workingDirField.requestFocus();
            return;
        }

        if (commandField.getText().isBlank()) {
            errorLabel.setText("Введите команду для выполнения");
            commandField.requestFocus();
            return;
        }

        result = new SimpleScriptDTO(
                null,
                titleField.getText().trim(),
                descriptionField.getText().trim(),
                null,
                workingDirField.getText().trim(),
                commandField.getText().trim()
        );

        stage.close();
    }

    @FXML
    private void onCancel() {
        stage.close();
    }
}
