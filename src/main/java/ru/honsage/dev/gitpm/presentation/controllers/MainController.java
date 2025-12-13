package ru.honsage.dev.gitpm.presentation.controllers;

import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import ru.honsage.dev.gitpm.presentation.viewmodels.MainViewModel;
import ru.honsage.dev.gitpm.presentation.viewmodels.ProjectViewModel;
import ru.honsage.dev.gitpm.presentation.viewmodels.TaskViewModel;

import java.io.File;
import java.io.IOException;

public class MainController {
    @FXML
    protected BorderPane root;
    @FXML
    protected VBox header;
    @FXML
    protected HBox headerPane;
    @FXML
    protected Label titleLabel;
    @FXML
    protected MenuBar menuBar;
    @FXML
    protected Menu menuFile;
    @FXML
    protected Menu menuEdit;
    @FXML
    protected Menu menuHelp;
    @FXML
    protected SplitPane main;
    @FXML
    protected VBox projectContainer;
    @FXML
    protected HBox projectPane;
    @FXML
    protected ListView<ProjectViewModel> projectFlow;
    @FXML
    protected VBox toolContainer;
    @FXML
    protected TabPane tabContainer;
    @FXML
    protected Tab infoTab;
    @FXML
    protected Tab scriptTab;
    @FXML
    protected Tab taskTab;
    @FXML
    protected ScrollPane taskScroll;
    @FXML
    protected HBox taskPane;
    @FXML
    protected Button addTaskButton;
    @FXML
    protected VBox taskFlow;

    private final MainViewModel viewModel;

    public MainController(MainViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @FXML
    public void initialize() {
        projectFlow.setItems(viewModel.getProjects());
        projectFlow.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(ProjectViewModel item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getTitle());
                }
            }
        });

        projectFlow.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldValue, newValue) ->
                        viewModel.setSelectedProject(newValue)
        );

        viewModel.getTasks().addListener(
                (ListChangeListener<TaskViewModel>) _ -> refreshTaskUI()
        );

        viewModel.loadProjects();
    }

    @FXML
    public void onAddProject(ActionEvent event) {
        // TODO: replace mock with dialog
        viewModel.addProject("Project", "", "C:/", null);
    }

    @FXML
    public void onScanDirectory(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Scan for Git Repositories");
        File selected = chooser.showDialog(null);
        if (selected != null) {
            viewModel.scanForProjects(selected.toPath());
        }
    }

    @FXML
    public void onFilterRemoteToggled(ActionEvent event) {
        viewModel.filterOnlyWithRemote(((ToggleButton) event.getSource()).isSelected());
    }

    @FXML
    public void onEditProject(ActionEvent event) {
        if (viewModel.getSelectedProject() == null) return;

        // TODO: replace mock with dialog
        viewModel.updateSelected(
                viewModel.getSelectedProject().getTitle() + " (edited)",
                viewModel.getSelectedProject().descriptionProperty().get(),
                viewModel.getSelectedProject().getLocalPath(),
                viewModel.getSelectedProject().remoteURLProperty().get()
        );
    }

    @FXML
    public void onDeleteProject(ActionEvent event) {
        viewModel.deleteSelected();
    }

    @FXML
    public void onAddTask(ActionEvent actionEvent) {
        if (viewModel.getSelectedProject() == null) {
            // TODO: красивый алерт
            Alert a = new Alert(Alert.AlertType.WARNING, "Необходимо выбрать проект!");
            a.show();
            return;
        }

        this.createTextDialog().showAndWait().ifPresent(title -> {
            if (title.isBlank()) return;
            viewModel.addTaskForSelectedProject(title);
        });
    }

    @FXML
    public void onTaskTabSelected(Event event) {
        refreshTaskUI();
    }

    // TEMP
    private TextInputDialog createTextDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Добавить задачу");
        dialog.setHeaderText("Введите название задачи");
        dialog.setContentText("Название:");
        return dialog;
    }

    private void refreshTaskUI() {
        taskFlow.getChildren().clear();
        for (TaskViewModel tvm : viewModel.getTasks()) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/ru/honsage/dev/gitpm/fxml/task-item.fxml")
                );
                Node node = loader.load();
                TaskItemController controller = loader.getController();
                controller.setViewModel(tvm);

                node.setUserData(tvm.getId());
                taskFlow.getChildren().add(node);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
