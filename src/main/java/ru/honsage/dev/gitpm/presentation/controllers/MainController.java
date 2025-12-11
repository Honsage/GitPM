package ru.honsage.dev.gitpm.presentation.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import ru.honsage.dev.gitpm.presentation.viewmodels.MainViewModel;
import ru.honsage.dev.gitpm.presentation.viewmodels.ProjectViewModel;

import java.io.File;

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
}
