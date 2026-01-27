package ru.honsage.dev.gitpm.presentation.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import ru.honsage.dev.gitpm.domain.ports.BrowserType;

import java.util.Optional;

public class ChangeBrowserDialogController {
    @FXML
    protected ToggleGroup browserToggleGroup;

    private RadioButton selectedType;
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setSelectedBrowser(BrowserType browserType) {
        browserToggleGroup.getToggles().forEach(toggle -> {
            RadioButton radioButton = (RadioButton) toggle;
            if (radioButton.getUserData().equals(browserType.toString())) {
                radioButton.setSelected(true);
                selectedType = radioButton;
            }
        });
    }

    public Optional<String> getSelectedBrowser() {
        return Optional.ofNullable((String) selectedType.getUserData());
    }

    @FXML
    public void onSave(ActionEvent event) {
        selectedType = (RadioButton) browserToggleGroup.getSelectedToggle();
        stage.close();
    }

    @FXML
    public void onCancel(ActionEvent event) {
        stage.close();
    }
}
