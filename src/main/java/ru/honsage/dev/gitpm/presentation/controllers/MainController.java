package ru.honsage.dev.gitpm.presentation.controllers;

import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.honsage.dev.gitpm.domain.models.TaskPriority;
import ru.honsage.dev.gitpm.domain.ports.GitOperations;
import ru.honsage.dev.gitpm.presentation.viewmodels.MainViewModel;
import ru.honsage.dev.gitpm.presentation.viewmodels.ProjectViewModel;
import ru.honsage.dev.gitpm.presentation.viewmodels.SimpleScriptViewModel;
import ru.honsage.dev.gitpm.presentation.viewmodels.TaskViewModel;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

public class MainController {
    @FXML
    protected ScrollPane infoTabScroll;
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
    protected SplitPane main;
    @FXML
    protected VBox projectContainer;
    @FXML
    protected HBox projectPane;
    @FXML
    protected TextField projectSearchEntry;
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
    @FXML
    protected Label localPathLabel;
    @FXML
    protected Label remoteUrlLabel;
    @FXML
    protected Label addedAtLabel;
    @FXML
    protected Label projectTitleLabel;
    @FXML
    protected  Label projectDescriptionLabel;
    @FXML
    protected ListView<SimpleScriptViewModel> scriptList;
    @FXML
    protected Label scriptTitle;
    @FXML
    protected Label scriptDescription;
    @FXML
    protected Label scriptCommand;
    @FXML
    protected TextArea scriptOutput;
    @FXML
    protected Button runScriptButton;
    @FXML
    protected Button stopScriptButton;

    private final MainViewModel viewModel;
    private GitOperations git;

    public MainController(MainViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @FXML
    public void initialize() {
        // Projects
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

        projectSearchEntry.textProperty().addListener((obs, oldValue, newValue) -> {
            viewModel.filterByTitlePrefix(newValue);
        });

        projectFlow.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldValue, newValue) -> {
                    viewModel.setSelectedProject(newValue);
                    refreshInfoUI();
                    viewModel.loadScriptsForSelectedProject();
                }
        );

        viewModel.getTasks().addListener(
                (ListChangeListener<TaskViewModel>) _ -> {
                    refreshTaskUI();
                }
        );

        viewModel.loadProjects();

        // Scripts
        scriptList.setItems(viewModel.getScripts());

        scriptList.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(SimpleScriptViewModel item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTitle());
            }
        });

        scriptList.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldValue, selected) -> {
                    viewModel.setSelectedScript(selected);
                    refreshScriptUI();
                }
        );
    }

    public void setGitClient(GitOperations git) {
        this.git = git;
    }

    @FXML
    public void onAddProject(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ru/honsage/dev/gitpm/fxml/dialogs/add-project-dialog.fxml")
            );

            Parent root = loader.load();
            AddProjectDialogController controller = loader.getController();
            Stage stage = new Stage();
            stage.setTitle("Добавление Git проекта");
            stage.getIcons().add(new Image(String.valueOf(getClass().getResource("/ru/honsage/dev/gitpm/images/icon.png"))));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(this.root.getScene().getWindow());
            stage.setScene(new Scene(root));

            controller.setStage(stage);
            controller.setGitClient(this.git);
            stage.showAndWait();

            controller.getResult().ifPresent(project ->
                    viewModel.addProject(
                            project.title(),
                            project.description(),
                            project.localPath(),
                            project.remoteURL()
                    )
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onEditProject(ActionEvent event) {
        var projectViewModel = viewModel.getSelectedProject();
        if (projectViewModel == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ru/honsage/dev/gitpm/fxml/dialogs/edit-project-dialog.fxml")
            );

            Parent root = loader.load();
            EditProjectDialogController controller = loader.getController();
            Stage stage = new Stage();
            stage.setTitle("Редактирование Git проекта");
            stage.getIcons().add(new Image(String.valueOf(getClass().getResource("/ru/honsage/dev/gitpm/images/icon.png"))));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(this.root.getScene().getWindow());
            stage.setScene(new Scene(root));

            controller.setStage(stage);
            controller.setGitClient(this.git);
            controller.setProjectInfo(projectViewModel);
            stage.showAndWait();

            controller.getResult().ifPresent(project ->
                    viewModel.updateSelected(
                            project.title(),
                            project.description(),
                            project.localPath(),
                            project.remoteURL()
                    )
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onDeleteProject(ActionEvent event) {
        viewModel.deleteSelected();
    }

    @FXML
    public void onOpenProjectFolder(ActionEvent event) {
        var project = viewModel.getSelectedProject();
        if (project == null) return;

        String pathStr = project.getLocalPath();
        if (pathStr == null || pathStr.isBlank()) return;

        try {
            Desktop desktop = Desktop.getDesktop();
            desktop.open(new File(pathStr));
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Не удалось открыть папку: " + e.getMessage());
            alert.showAndWait();
        }
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
    public void onAddTask(ActionEvent actionEvent) {
        if (viewModel.getSelectedProject() == null) {
            // TODO: красивый алерт
            Alert a = new Alert(Alert.AlertType.WARNING, "Необходимо выбрать проект!");
            a.show();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ru/honsage/dev/gitpm/fxml/dialogs/add-task-dialog.fxml")
            );

            Stage stage = new Stage();
            stage.setTitle("Добавление задачи");
            stage.getIcons().add(new Image(String.valueOf(getClass().getResource("/ru/honsage/dev/gitpm/images/icon.png"))));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(this.root.getScene().getWindow());
            stage.setScene(new Scene(loader.load()));
            stage.showAndWait();

            AddTaskDialogController controller = loader.getController();

            controller.getResult().ifPresent(task ->
                    viewModel.addTaskForSelectedProject(
                            task.title(),
                            task.content(),
                            task.deadlineAt() == null ? null : LocalDateTime.parse(task.deadlineAt()),
                            TaskPriority.valueOf(task.priority())
                    )
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onTaskTabSelected(Event event) {
        refreshTaskUI();
    }

    @FXML
    public void onInfoTabSelected(Event event) {
        refreshInfoUI();
    }

    @FXML
    public void onScriptTabSelected(Event event) {
        refreshScriptUI();
    }

    private void refreshTaskUI() {
        if (viewModel.getSelectedProject() == null) {
            // TODO: show banner 'choose project'
            return;
        }
        taskFlow.getChildren().clear();
        for (var tvm : viewModel.getTasks()) {
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

    private void refreshInfoUI() {
        var projectViewModel = viewModel.getSelectedProject();
        if (projectViewModel == null) {
            infoTabScroll.setVisible(false);
            // TODO: show banner 'choose project'
            return;
        }
        infoTabScroll.setVisible(true);
        projectTitleLabel.setText(projectViewModel.getTitle());
        projectDescriptionLabel.setText(
                (projectViewModel.getDescription() != null)? projectViewModel.getDescription() : null
        );
        localPathLabel.setText(projectViewModel.getLocalPath());
        remoteUrlLabel.setText(projectViewModel.getRemoteURL());
        addedAtLabel.setText(projectViewModel.getAddedAt());
    }

    private void refreshScriptUI() {
        runScriptButton.disableProperty().unbind();
        stopScriptButton.disableProperty().unbind();
        var script = viewModel.getSelectedScript();

        if (script == null) {
            runScriptButton.setDisable(true);
            stopScriptButton.setDisable(true);
            scriptTitle.setText(null);
            scriptDescription.setText(null);
            scriptCommand.setText(null);
            return;
        }

        runScriptButton.disableProperty().bind(script.runningProperty());
        stopScriptButton.disableProperty().bind(script.runningProperty().not());
        scriptTitle.setText(script.getTitle());
        scriptDescription.setText(script.getDescription());
        scriptCommand.setText(script.getExecutableCommand());
    }

    public void onAddScript(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ru/honsage/dev/gitpm/fxml/dialogs/add-script-dialog.fxml")
            );

            Stage stage = new Stage();
            stage.setTitle("Добавление скрипта");
            stage.getIcons().add(new Image(String.valueOf(getClass().getResource("/ru/honsage/dev/gitpm/images/icon.png"))));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(this.root.getScene().getWindow());
            stage.setScene(new Scene(loader.load()));

            AddScriptDialogController controller = loader.getController();
            controller.setStage(stage);
            controller.setDefaultDir(viewModel.getSelectedProject().getLocalPath());
            stage.showAndWait();

            controller.getResult().ifPresent(dto ->
                    viewModel.addScriptForSelectedProject(
                            dto.title(),
                            dto.description(),
                            dto.workingDir(),
                            dto.executableCommand()
                    )
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onEditScript(ActionEvent event) {
    }

    public void onDeleteScript(ActionEvent event) {
        viewModel.deleteSelectedScript();
    }

    public void onRunScript(ActionEvent event) {
        viewModel.runSelectedScript();
    }

    public void onStopScript(ActionEvent event) {
        viewModel.stopSelectedScript();
    }

    public void onCloseApplication(ActionEvent event) {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }
}
