package ru.honsage.dev.gitpm.presentation.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import ru.honsage.dev.gitpm.domain.models.TaskPriority;
import ru.honsage.dev.gitpm.presentation.viewmodels.TaskViewModel;

public class TaskItemController {
    @FXML
    protected VBox taskCard;
    @FXML
    protected CheckBox completedCheck;
    @FXML
    protected Label titleLabel;
    @FXML
    protected Label contentLabel;
    @FXML
    protected Label deadlineLabel;
    @FXML
    protected Label createdLabel;
    @FXML
    protected Button priorityLow;
    @FXML
    protected Button priorityMedium;
    @FXML
    protected Button priorityHigh;
    @FXML
    protected Button editButton;
    @FXML
    protected Button deleteButton;

    private TaskViewModel taskViewModel;

    @FXML
    public void initialize() {
        // TODO: card animation
        taskCard.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if (taskViewModel.getOnOpenDetails() != null) taskViewModel.getOnOpenDetails().run();
        });
    }

    public void setViewModel(TaskViewModel taskViewModel) {
        this.taskViewModel = taskViewModel;
        this.bindElements();
        highlightPriority(taskViewModel.getPriority());
    }

    private void bindElements() {
        titleLabel.textProperty().bind(taskViewModel.titleProperty());
        contentLabel.textProperty().bind(taskViewModel.contentProperty());
        deadlineLabel.textProperty().bind(taskViewModel.deadlineAtProperty());
        createdLabel.textProperty().bind(taskViewModel.createdAtProperty());

        completedCheck.selectedProperty().bindBidirectional(taskViewModel.completedProperty());
        taskViewModel.priorityProperty()
                .addListener((obs, oldValue, newValue) ->
                        highlightPriority(newValue));
    }

    private void highlightPriority(TaskPriority priority) {
        priorityLow.getStyleClass().remove("selected");
        priorityMedium.getStyleClass().remove("selected");
        priorityHigh.getStyleClass().remove("selected");

        switch (priority) {
            case LOW -> priorityLow.getStyleClass().add("selected");
            case MEDIUM -> priorityMedium.getStyleClass().add("selected");
            case HIGH -> priorityHigh.getStyleClass().add("selected");
        }
    }

    @FXML
    public void onPriorityLow(ActionEvent actionEvent) {
        taskViewModel.setPriority(TaskPriority.LOW);
    }

    @FXML
    public void onPriorityMedium(ActionEvent actionEvent) {
        taskViewModel.setPriority(TaskPriority.MEDIUM);
    }

    @FXML
    public void onPriorityHigh(ActionEvent actionEvent) {
        taskViewModel.setPriority(TaskPriority.HIGH);
    }


    @FXML
    public void onCheckSelected(ActionEvent actionEvent) {
        boolean isSelected = ((CheckBox)actionEvent.getSource()).isSelected();
        if (isSelected) taskCard.getStyleClass().add("completed");
        else taskCard.getStyleClass().remove("completed");
    }

    @FXML
    public void onEdit(ActionEvent actionEvent) {
        // TODO: dialog
    }

    @FXML
    public void onDelete(ActionEvent actionEvent) {
        if (taskViewModel.getOnDelete() != null) taskViewModel.getOnDelete().run();
    }
}
