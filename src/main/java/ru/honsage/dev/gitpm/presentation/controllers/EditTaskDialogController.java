package ru.honsage.dev.gitpm.presentation.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import ru.honsage.dev.gitpm.domain.models.TaskPriority;
import ru.honsage.dev.gitpm.presentation.dto.TaskDTO;
import ru.honsage.dev.gitpm.presentation.viewmodels.TaskViewModel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

// TODO: fetch is_completed
public class EditTaskDialogController {
    @FXML
    protected TextField titleField;
    @FXML
    protected TextArea contentField;
    @FXML
    protected DatePicker deadlinePicker;
    @FXML
    protected Button priorityLow;
    @FXML
    protected Button priorityMedium;
    @FXML
    protected Button priorityHigh;
    @FXML
    protected Button createButton;
    @FXML
    protected Label errorLabel;

    private Stage stage;
    private TaskPriority selectedPriority = TaskPriority.LOW;
    private TaskDTO result;

    @FXML
    private void initialize() {
        selectPriority(TaskPriority.LOW);
        blockDatesBefore(deadlinePicker, LocalDate.now());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setTaskInfo(TaskViewModel taskViewModel) {
        titleField.setText(taskViewModel.getTitle());
        contentField.setText(taskViewModel.getContent());
        if (taskViewModel.getDeadlineAt() != null)
            deadlinePicker.setValue(LocalDateTime.parse(taskViewModel.getDeadlineAt()).toLocalDate());
        selectPriority(taskViewModel.getPriority());
    }

    @FXML
    private void onPriorityLow() {
        selectPriority(TaskPriority.LOW);
    }

    @FXML
    private void onPriorityMedium() {
        selectPriority(TaskPriority.MEDIUM);
    }

    @FXML
    private void onPriorityHigh() {
        selectPriority(TaskPriority.HIGH);
    }

    private void selectPriority(TaskPriority priority) {
        selectedPriority = priority;

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
    private void onSave() {
        if (titleField.getText().isBlank()) {
            errorLabel.setText("Введите название задачи");
            titleField.requestFocus();
            return;
        }

        LocalDateTime deadline = null;
        LocalDate date = deadlinePicker.getValue();
        if (date != null) {
            deadline = date.atStartOfDay();
        }

        result = new TaskDTO(
                null,
                titleField.getText().trim(),
                contentField.getText() == null || contentField.getText().isBlank()
                        ? null
                        : contentField.getText().trim(),
                null,
                false,
                deadline == null ? null : deadline.toString(),
                selectedPriority.toString()
        );

        stage.close();
    }

    @FXML
    private void onCancel() {
        result = null;
        stage.close();
    }

    public Optional<TaskDTO> getResult() {
        return Optional.ofNullable(result);
    }

    private void blockDatesBefore(DatePicker datePicker, LocalDate date) {
        final Callback<DatePicker, DateCell> dayCellFactory = new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item.isBefore(date)) {
                            setDisable(true);
                            setStyle("-fx-background-color: #cccccc;");
                        }
                    }
                };
            }
        };
        datePicker.setDayCellFactory(dayCellFactory);
    }
}
