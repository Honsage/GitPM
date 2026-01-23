package ru.honsage.dev.gitpm.presentation.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import ru.honsage.dev.gitpm.domain.ports.ShellType;

import java.util.Optional;

public class ChangeShellDialogController {
    @FXML
    protected ToggleGroup shellToggleGroup;
    @FXML
    protected Label errorLabel;

    private RadioButton selectedType;
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setSelectedShell(ShellType shellType) {
        shellToggleGroup.getToggles().forEach(toggle -> {
            RadioButton radioButton = (RadioButton) toggle;
            if (radioButton.getUserData().equals(shellType.toString())) {
                radioButton.setSelected(true);
                selectedType = radioButton;
            }
        });
    }

    public Optional<String> getSelectedShell() {
        return Optional.ofNullable((String) selectedType.getUserData());
    }

    @FXML
    public void onSave(ActionEvent event) {
        selectedType = (RadioButton) shellToggleGroup.getSelectedToggle();
        stage.close();
    }

    @FXML
    public void onCancel(ActionEvent event) {
        stage.close();
    }
}
