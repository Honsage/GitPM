package ru.honsage.dev.gitpm.presentation.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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
    protected ListView<Void> projectFlow;
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

    public void onAddProject(ActionEvent actionEvent) {
    }

    public void onScanDirectory(ActionEvent actionEvent) {
    }

    public void onFilterRemoteToggled(ActionEvent actionEvent) {
    }

    public void onEditProject(ActionEvent actionEvent) {
    }

    public void onDeleteProject(ActionEvent actionEvent) {
    }
}
