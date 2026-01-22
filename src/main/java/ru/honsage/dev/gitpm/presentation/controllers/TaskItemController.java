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

import java.time.LocalDateTime;

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
    private MainController mainController;

    @FXML
    public void initialize() {
        // TODO: refer to mainController
        taskCard.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            System.out.println("Hello!");
        });
    }

    public void setViewModel(TaskViewModel taskViewModel) {
        this.taskViewModel = taskViewModel;
        this.bindElements();
        highlightPriority(taskViewModel.getPriority());
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    private void bindElements() {
        titleLabel.textProperty().bind(taskViewModel.titleProperty());
        contentLabel.textProperty().bind(taskViewModel.contentProperty());
        if (taskViewModel.getDeadlineAt() != null)
            deadlineLabel.setText("Дедлайн: " + LocalDateTime.parse(taskViewModel.getDeadlineAt()).toLocalDate().toString());
        createdLabel.setText(LocalDateTime.parse(taskViewModel.getCreatedAt()).toLocalDate().toString());
        completedCheck.selectedProperty().bindBidirectional(taskViewModel.completedProperty());
        taskViewModel.priorityProperty()
                .addListener((obs, oldValue, newValue) ->
                        highlightPriority(newValue));
        taskViewModel.completedProperty().addListener((obs, oldValue, newValue) -> {
            updateCompletedStyle(newValue);
        });
        updateCompletedStyle(taskViewModel.isCompleted());
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

    private void updateCompletedStyle(boolean isCompleted) {
        if (isCompleted) {
            if (!taskCard.getStyleClass().contains("completed")) {
                taskCard.getStyleClass().add("completed");
            }
        } else {
            taskCard.getStyleClass().remove("completed");
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
    public void onCheckCompleted(ActionEvent actionEvent) {
        boolean isCompleted = ((CheckBox)actionEvent.getSource()).isSelected();
        taskViewModel.setIsCompleted(isCompleted);
        if (isCompleted) taskCard.getStyleClass().add("completed");
        else taskCard.getStyleClass().remove("completed");
    }

    @FXML
    public void onEdit(ActionEvent actionEvent) {
        if (mainController != null) {
            mainController.openEditTaskDialog(taskViewModel);
        }
    }

    @FXML
    public void onDelete(ActionEvent actionEvent) {
        if (mainController != null) {
            mainController.deleteTask(taskViewModel);
        }
    }
}
