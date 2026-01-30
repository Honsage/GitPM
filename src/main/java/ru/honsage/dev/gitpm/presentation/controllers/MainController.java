package ru.honsage.dev.gitpm.presentation.controllers;

import javafx.application.Platform;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import ru.honsage.dev.gitpm.domain.models.TaskPriority;
import ru.honsage.dev.gitpm.domain.ports.AppSettings;
import ru.honsage.dev.gitpm.domain.ports.GitOperations;
import ru.honsage.dev.gitpm.infrastructure.utils.OSUtils;
import ru.honsage.dev.gitpm.presentation.viewmodels.MainViewModel;
import ru.honsage.dev.gitpm.presentation.viewmodels.ProjectViewModel;
import ru.honsage.dev.gitpm.presentation.viewmodels.ScriptViewModel;
import ru.honsage.dev.gitpm.presentation.viewmodels.TaskViewModel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;

// TODO: determine which access modifier is applied to fxml components

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
    protected TextField taskSearchEntry;
    @FXML
    protected TextField scriptSearchEntry;
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
    protected ListView<ScriptViewModel> scriptList;
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
    @FXML
    protected VBox infoTabBannerEmpty;
    @FXML
    protected VBox taskTabBannerEmpty;
    @FXML
    protected VBox scriptTabBannerEmpty;
    @FXML
    protected VBox taskContainer;
    @FXML
    protected SplitPane scriptContainer;
    @FXML
    protected VBox taskEmptyPane;
    @FXML
    protected VBox scriptEmptyPane;
    @FXML
    protected VBox scriptDetailsPane;

    private final MainViewModel viewModel;
    private GitOperations git;
    private AppSettings settings;
    private Thread outputPollingThread;

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
            viewModel.filterProjectsByTitlePrefix(newValue);
            refreshTaskUI();
        });

        taskSearchEntry.textProperty().addListener((obs, oldValue, newValue) -> {
            viewModel.filterTasksByTitlePrefix(newValue);
        });

        scriptSearchEntry.textProperty().addListener((obs, oldValue, newValue) -> {
            viewModel.filterScriptsByTitlePrefix(newValue);
        });

        projectFlow.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldValue, newValue) -> {
                    viewModel.setSelectedProject(newValue);
                    refreshInfoUI();
                    refreshTaskUI();
                    viewModel.loadScriptsForSelectedProject();
                    refreshScriptUI();
                }
        );

        viewModel.getTasks().addListener(
                (ListChangeListener<TaskViewModel>) _ -> {
                    refreshTaskUI();
                }
        );

        localPathLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            OSUtils.copyToClipBoard(localPathLabel.getText());
        });

        remoteUrlLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            OSUtils.copyToClipBoard(remoteUrlLabel.getText());
        });

        viewModel.loadProjects();

        // Scripts
        scriptList.setItems(viewModel.getScripts());

        scriptList.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(ScriptViewModel item, boolean empty) {
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

        scriptCommand.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            OSUtils.copyToClipBoard(scriptCommand.getText());
        });

        startOutputPolling();

        // onClose
        Platform.runLater(() -> {
            Stage mainStage = (Stage) root.getScene().getWindow();
            mainStage.setOnCloseRequest(event -> {
                stopOutputPolling();
                stopAllRunningScripts();
                closeAllDialogs();
            });
        });
    }

    public void setGitClient(GitOperations git) {
        this.git = git;
    }

    public void setAppSettings(AppSettings settings) {
        this.settings = settings;
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
            stage.setMinHeight(290);
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
            stage.setMinHeight(290);
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

        OSUtils.openFolder(pathStr);
    }

    @FXML
    public void onOpenRemote(ActionEvent event) {
        String url = remoteUrlLabel.getText();
        if (url != null) viewModel.openInBrowser(url);
    }

    @FXML
    public void onScanDirectory(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Сканирование Git-репозиториев");
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
            OSUtils.alert("Необходимо выбрать проект!", "warning");
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
            stage.setMinHeight(290);
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

    public void onEditTask(TaskViewModel taskViewModel) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ru/honsage/dev/gitpm/fxml/dialogs/edit-task-dialog.fxml")
            );

            Stage stage = new Stage();
            stage.setTitle("Редактирование задачи");
            stage.getIcons().add(new Image(String.valueOf(getClass().getResource("/ru/honsage/dev/gitpm/images/icon.png"))));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(this.root.getScene().getWindow());
            stage.setMinHeight(290);
            stage.setScene(new Scene(loader.load()));

            EditTaskDialogController controller = loader.getController();
            controller.setStage(stage);
            controller.setTaskInfo(taskViewModel);
            stage.showAndWait();

            controller.getResult().ifPresent(task -> {
                taskViewModel.titleProperty().set(task.title());
                taskViewModel.contentProperty().set(task.content());
                taskViewModel.setIsCompleted(task.isCompleted());
                taskViewModel.deadlineAtProperty().set(task.deadlineAt());
                taskViewModel.setPriority(TaskPriority.valueOf(task.priority()));
                viewModel.updateTaskForSelectedProject(taskViewModel);
                refreshTaskUI();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onDeleteTask(TaskViewModel taskViewModel) {
        viewModel.deleteTask(taskViewModel);
    }

    public void openTaskDetails(TaskViewModel taskViewModel) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ru/honsage/dev/gitpm/fxml/task-popup.fxml")
            );

            Stage stage = new Stage();
            stage.setTitle("Поп-ап задачи");
            stage.getIcons().add(new Image(String.valueOf(getClass().getResource("/ru/honsage/dev/gitpm/images/icon.png"))));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(this.root.getScene().getWindow());
            stage.setScene(new Scene(loader.load()));

            TaskPopupController controller = loader.getController();
            controller.setStage(stage);
            controller.setTaskInfo(taskViewModel);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onFilterCompletedToggled(ActionEvent event) {
        viewModel.filterTasksByCompleted(((ToggleButton) event.getSource()).isSelected());
    }

    @FXML
    public void onFilterOverdueToggled(ActionEvent event) {
        viewModel.filterTasksOverdue(((ToggleButton) event.getSource()).isSelected());
    }

    @FXML
    public void onFilterImportantToggled(ActionEvent event) {
        viewModel.filterTasksImportant(((ToggleButton) event.getSource()).isSelected());
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

    public void refreshTaskUI() {
        boolean hasProject = viewModel.getSelectedProject() != null;

        taskTabBannerEmpty.setVisible(!hasProject);
        taskContainer.setVisible(hasProject);
        taskPane.setVisible(hasProject);

        if (!hasProject) {
            return;
        }

        taskFlow.getChildren().clear();

        var taskList = viewModel.getTasks();
        boolean isTaskListEmpty = taskList.isEmpty();

        taskEmptyPane.setVisible(isTaskListEmpty);
        taskScroll.setVisible(!isTaskListEmpty);

        if (isTaskListEmpty) {
            return;
        }

        for (var tvm : taskList) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/ru/honsage/dev/gitpm/fxml/task-item.fxml")
                );
                Node node = loader.load();
                TaskItemController controller = loader.getController();
                controller.setViewModel(tvm);
                controller.setMainController(MainController.this);

                node.setUserData(tvm.getId());
                taskFlow.getChildren().add(node);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void refreshInfoUI() {
        var project = viewModel.getSelectedProject();
        boolean hasProject = project != null;

        infoTabBannerEmpty.setVisible(!hasProject);
        infoTabScroll.setVisible(hasProject);

        if (!hasProject) {
            return;
        }

        projectTitleLabel.setText(project.getTitle());
        projectDescriptionLabel.setText(project.getDescription());
        localPathLabel.setText(project.getLocalPath());
        remoteUrlLabel.setText(project.getRemoteURL());
        addedAtLabel.setText(LocalDateTime.parse(project.getAddedAt()).toLocalDate().toString());
    }

    private void refreshScriptUI() {
        boolean hasProject = viewModel.getSelectedProject() != null;

        scriptTabBannerEmpty.setVisible(!hasProject);
        scriptContainer.setVisible(hasProject);

        if (!hasProject) {
            return;
        }

        runScriptButton.disableProperty().unbind();
        stopScriptButton.disableProperty().unbind();

        var script = viewModel.getSelectedScript();
        boolean hasScript = script != null;

        scriptEmptyPane.setVisible(!hasScript);
        scriptDetailsPane.setVisible(hasScript);

        if (!hasScript) {
            runScriptButton.setDisable(true);
            stopScriptButton.setDisable(true);
            return;
        }

        runScriptButton.disableProperty().bind(script.runningProperty());
        stopScriptButton.disableProperty().bind(script.runningProperty().not());

        scriptTitle.setText(script.getTitle());
        scriptDescription.setText(script.getDescription());
        scriptCommand.setText(script.getCommand());
    }

    @FXML
    private void onAddScript(ActionEvent event) {
        if (viewModel.getSelectedProject() == null) {
            OSUtils.alert("Необходимо выбрать проект!", "warning");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ru/honsage/dev/gitpm/fxml/dialogs/add-script-dialog.fxml")
            );

            Stage stage = new Stage();
            stage.setTitle("Добавление скрипта");
            stage.getIcons().add(new Image(String.valueOf(getClass().getResource("/ru/honsage/dev/gitpm/images/icon.png"))));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(this.root.getScene().getWindow());
            stage.setMinHeight(290);
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
                            dto.command()
                    )
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onEditScript(ActionEvent event) {
        var scriptViewModel = viewModel.getSelectedScript();
        if (scriptViewModel == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ru/honsage/dev/gitpm/fxml/dialogs/edit-script-dialog.fxml")
            );

            Stage stage = new Stage();
            stage.setTitle("Редактирование скрипта");
            stage.getIcons().add(new Image(String.valueOf(getClass().getResource("/ru/honsage/dev/gitpm/images/icon.png"))));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(this.root.getScene().getWindow());
            stage.setMinHeight(290);
            stage.setScene(new Scene(loader.load()));

            EditScriptDialogController controller = loader.getController();
            controller.setStage(stage);
            controller.setScriptInfo(scriptViewModel);
            stage.showAndWait();

            controller.getResult().ifPresent(dto ->
                    viewModel.updateSelectedScript(
                            dto.title(),
                            dto.description(),
                            dto.workingDir(),
                            dto.command()
                    )
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onDeleteScript(ActionEvent event) {
        viewModel.deleteSelectedScript();
    }

    private void startOutputPolling() {
        outputPollingThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(500);

                    Platform.runLater(() -> {
                        if (viewModel.getSelectedScript() != null) {
                            String currentOutput = viewModel.getScriptsOutput();

                            if (!scriptOutput.getText().equals(currentOutput)) {
                                scriptOutput.setText(currentOutput);

                                scriptOutput.setScrollTop(Double.MAX_VALUE);
                            }
                        }
                    });

                } catch (InterruptedException e) {
                    break;
                }
            }
        });

        outputPollingThread.setDaemon(true);
        outputPollingThread.start();
    }

    public void onRunScript(ActionEvent event) {
        if (viewModel.getSelectedScript() != null) {
            scriptOutput.setText("");
            viewModel.runSelectedScript();
        }
    }

    public void onStopScript(ActionEvent event) {
        if (viewModel.getSelectedScript() != null) {
            viewModel.stopSelectedScript();
            viewModel.clearScriptOutput();
        }
    }

    public void stopOutputPolling() {
        if (outputPollingThread != null) {
            outputPollingThread.interrupt();
        }
    }

    // Do not work in exit on X
    public void onCloseApplication(ActionEvent event) {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onChangeShell(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ru/honsage/dev/gitpm/fxml/dialogs/change-shell-dialog.fxml")
            );

            Stage stage = new Stage();
            stage.setTitle("Настройка командной оболочки");
            stage.getIcons().add(new Image(String.valueOf(getClass().getResource("/ru/honsage/dev/gitpm/images/icon.png"))));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(this.root.getScene().getWindow());
            stage.setScene(new Scene(loader.load()));

            ChangeShellDialogController controller = loader.getController();
            controller.setStage(stage);
            controller.setSelectedShell(viewModel.getSelectedShellType());
            stage.showAndWait();

            controller.getSelectedShell().ifPresent(viewModel::setSelectedShellType);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onChangeBrowser(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ru/honsage/dev/gitpm/fxml/dialogs/change-browser-dialog.fxml")
            );

            Stage stage = new Stage();
            stage.setTitle("Настройка браузера");
            stage.getIcons().add(new Image(String.valueOf(getClass().getResource("/ru/honsage/dev/gitpm/images/icon.png"))));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(this.root.getScene().getWindow());
            stage.setScene(new Scene(loader.load()));

            ChangeBrowserDialogController controller = loader.getController();
            controller.setStage(stage);
            controller.setSelectedBrowser(viewModel.getSelectedBrowserType());
            stage.showAndWait();

            controller.getSelectedBrowser().ifPresent(viewModel::setSelectedBrowserType);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onNormalizeWindow(ActionEvent event) {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.setWidth(800);
        stage.setHeight(500);
        settings.setWindowWidth(stage.getWidth());
        settings.setWindowHeight(stage.getHeight());
    }

    @FXML
    private void onShowUserManual(ActionEvent event) {
        viewModel.showUserManual();
    }

    private void stopAllRunningScripts() {
        for (var script : viewModel.getScripts()) {
            if (script.isRunning()) {
                viewModel.stopScript(script);
            }
        }
    }

    private void closeAllDialogs() {
        for (Window window : Window.getWindows()) {
            if (window != root.getScene().getWindow() && window instanceof Stage) {
                ((Stage) window).close();
            }
        }
    }
}
