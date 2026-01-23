package ru.honsage.dev.gitpm.presentation.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ru.honsage.dev.gitpm.domain.models.TaskPriority;
import ru.honsage.dev.gitpm.presentation.viewmodels.TaskViewModel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class TaskPopupController {
    @FXML
    private VBox dialogRoot;
    @FXML
    private Label priorityIndicator;
    @FXML
    private Label priorityLabel;
    @FXML
    private Label titleLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private Label createdDateLabel;
    @FXML
    private Label createdTimeLabel;
    @FXML
    private Label deadlineDateLabel;
    @FXML
    private Label deadlineTimeLabel;
    @FXML
    private Label deadlineStatusLabel;

    private Stage stage;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    public void initialize() {
        // Настраиваем TextArea только для чтения
        descriptionArea.setEditable(false);
        descriptionArea.setWrapText(true);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setTaskInfo(TaskViewModel taskViewModel) {
        if (taskViewModel == null) return;

        titleLabel.setText(taskViewModel.getTitle());

        String content = taskViewModel.getContent();
        descriptionArea.setText(content != null ? content : "Описание отсутствует");

        if (taskViewModel.isCompleted()) {
            statusLabel.setText("✓ Выполнена");
            statusLabel.getStyleClass().add("completed");
            statusLabel.getStyleClass().remove("pending");
        } else {
            statusLabel.setText("● В работе");
            statusLabel.getStyleClass().add("pending");
            statusLabel.getStyleClass().remove("completed");
        }

        updatePriorityUI(taskViewModel.getPriority());

        if (taskViewModel.getCreatedAt() != null) {
            LocalDateTime created = LocalDateTime.parse(taskViewModel.getCreatedAt());
            createdDateLabel.setText(created.format(DATE_FORMATTER));
            createdTimeLabel.setText(created.format(TIME_FORMATTER));
        } else {
            createdDateLabel.setText("—");
            createdTimeLabel.setText("");
        }

        if (taskViewModel.getDeadlineAt() != null && !taskViewModel.getDeadlineAt().isEmpty()) {
            LocalDateTime deadline = LocalDateTime.parse(taskViewModel.getDeadlineAt());
            deadlineDateLabel.setText(deadline.format(DATE_FORMATTER));
            deadlineTimeLabel.setText("до " + deadline.format(TIME_FORMATTER));
            updateDeadlineStatus(deadline, taskViewModel.isCompleted());
        } else {
            deadlineDateLabel.setText("Не установлен");
            deadlineTimeLabel.setText("");
            deadlineStatusLabel.setText("Без дедлайна");
            deadlineStatusLabel.getStyleClass().add("none");
        }
    }

    private void updatePriorityUI(TaskPriority priority) {
        priorityIndicator.getStyleClass().removeAll("low", "medium", "high");

        switch (priority) {
            case LOW -> {
                priorityIndicator.getStyleClass().add("low");
                priorityLabel.setText("Низкий");
            }
            case MEDIUM -> {
                priorityIndicator.getStyleClass().add("medium");
                priorityLabel.setText("Средний");
            }
            case HIGH -> {
                priorityIndicator.getStyleClass().add("high");
                priorityLabel.setText("Высокий");
            }
        }
    }

    private void updateDeadlineStatus(LocalDateTime deadline, boolean isCompleted) {
        deadlineStatusLabel.getStyleClass().removeAll("overdue", "today", "future", "none");

        LocalDateTime now = LocalDateTime.now();
        long daysBetween = ChronoUnit.DAYS.between(now.toLocalDate(), deadline.toLocalDate());

        if (isCompleted) {
            if (!deadline.isBefore(now)) {
                deadlineStatusLabel.setText("Выполнено досрочно");
                deadlineStatusLabel.getStyleClass().add("future");
            }
        } else {
            if (deadline.isBefore(now)) {
                deadlineStatusLabel.setText("Просрочено");
                deadlineStatusLabel.getStyleClass().add("overdue");
            } else if (daysBetween == 0) {
                deadlineStatusLabel.setText("Сегодня");
                deadlineStatusLabel.getStyleClass().add("today");
            } else if (daysBetween <= 90) {
                deadlineStatusLabel.setText("Через " + daysBetween + " д.");
                deadlineStatusLabel.getStyleClass().add("today");
            } else {
                deadlineStatusLabel.setText("Времени в запасе");
                deadlineStatusLabel.getStyleClass().add("future");
            }
        }
    }
}
