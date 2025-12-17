package ru.honsage.dev.gitpm.presentation.viewmodels;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import ru.honsage.dev.gitpm.application.services.CommandService;
import ru.honsage.dev.gitpm.application.services.ProjectService;
import ru.honsage.dev.gitpm.application.services.ScriptService;
import ru.honsage.dev.gitpm.application.services.TaskService;
import ru.honsage.dev.gitpm.domain.models.TaskPriority;
import ru.honsage.dev.gitpm.domain.ports.CommandExecutor;
import ru.honsage.dev.gitpm.domain.valueobjects.*;
import ru.honsage.dev.gitpm.presentation.dto.ProjectDTO;
import ru.honsage.dev.gitpm.presentation.dto.SimpleScriptDTO;
import ru.honsage.dev.gitpm.presentation.dto.TaskDTO;
import ru.honsage.dev.gitpm.presentation.mappers.ProjectDTOMapper;
import ru.honsage.dev.gitpm.presentation.mappers.SimpleScriptDTOMapper;
import ru.honsage.dev.gitpm.presentation.mappers.TaskDTOMapper;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.stream.IntStream;

// Логика представления
public class MainViewModel {
    private final ProjectService projectService;
    private final TaskService taskService;
    private final ScriptService scriptService;
    private final CommandService commandService;
    private final CommandExecutor commandExecutor;

    private final ObservableList<ProjectViewModel> projects = FXCollections.observableArrayList();
    private final FilteredList<ProjectViewModel> filteredProjects = new FilteredList<>(projects);

    private final ObservableList<TaskViewModel> tasks = FXCollections.observableArrayList();

    private final ObservableList<SimpleScriptViewModel> scripts = FXCollections.observableArrayList();

    private ProjectViewModel selectedProject;
    private SimpleScriptViewModel selectedScript;

    public MainViewModel(
            ProjectService projectService,
            TaskService taskService,
            ScriptService scriptService,
            CommandService commandService,
            CommandExecutor commandExecutor
    ) {
        this.projectService = projectService;
        this.taskService = taskService;
        this.scriptService = scriptService;
        this.commandService = commandService;
        this.commandExecutor = commandExecutor;
    }

    // Projects

    public ObservableList<ProjectViewModel> getProjects() {
        return this.filteredProjects;
    }

    public ProjectViewModel getSelectedProject() {
        return this.selectedProject;
    }

    public void setSelectedProject(ProjectViewModel project) {
        this.selectedProject = project;
        this.projects.forEach(p -> p.setSelected(p == project));
        if (this.selectedProject != null) this.loadTasksForSelectedProject();
        else tasks.clear();
    }

    public void loadProjects() {
        this.projects.clear();

        this.projectService.getAllProjects().stream()
                .map(ProjectDTOMapper::toDTO)
                .map(ProjectViewModel::new)
                .forEach(projects::add);
    }

    public void scanForProjects(Path rootDirectory) {
        projectService.scanForGitRepositories(rootDirectory).stream()
                .map(ProjectDTOMapper::toDTO)
                .map(ProjectViewModel::new)
                .forEach(projects::add);
    }

    public void addProject(
            String title,
            String description,
            String localPath,
            String remoteURL
    ) {
        var project = projectService.createProject(
                title,
                description,
                localPath,
                remoteURL
        );

        ProjectDTO dto = ProjectDTOMapper.toDTO(project);
        projects.add(new ProjectViewModel(dto));
    }

    public void updateSelected(
            String newTitle,
            String newDescription,
            String newLocalPath,
            String newRemoteURL
    ) {
        if (selectedProject == null) return;

        ProjectId id = ProjectId.fromString(selectedProject.getId());
        var updated = projectService.updateProject(
                id,
                newTitle,
                newDescription,
                newLocalPath,
                newRemoteURL
        );

        this.updateProjectInList(ProjectDTOMapper.toDTO(updated));
    }

    public void deleteSelected() {
        if (selectedProject == null) return;

        ProjectId id = ProjectId.fromString(selectedProject.getId());
        projectService.deleteProject(id);

        projects.remove(selectedProject);
        selectedProject = null;
    }

    public void filterByTitlePrefix(String prefix) {
        filteredProjects.setPredicate(p ->
                prefix == null ||
                prefix.isBlank() ||
                p.getTitle().toLowerCase().startsWith(prefix.toLowerCase())
        );
    }

    public void filterOnlyWithRemote(boolean enabled) {
        if (!enabled) {
            filteredProjects.setPredicate(p -> true);
        } else {
            filteredProjects.setPredicate(p ->
                    p.remoteURLProperty().get() != null &&
                    !p.remoteURLProperty().get().isBlank());
        }
    }

    private void updateProjectInList(ProjectDTO dto) {
        IntStream.range(0, projects.size())
                .filter(i -> projects.get(i).getId().equals(dto.id()))
                .findFirst()
                .ifPresent(i -> projects.set(i, new ProjectViewModel(dto)));
    }

    // Tasks

    public ObservableList<TaskViewModel> getTasks() {
        return this.tasks;
    }

    public void loadTasksForSelectedProject() {
        if (this.selectedProject == null) {
            tasks.clear();
            return;
        }

        ProjectId projectId = ProjectId.fromString(selectedProject.getId());
        this.tasks.clear();

        taskService.getAllTasks(projectId).stream()
                .map(TaskDTOMapper::toDTO)
                .map(TaskViewModel::new)
                .map(this::bindHandlersToTask)
                .forEach(tasks::add);
    }

    public void addTaskForSelectedProject(
            String title,
            String content,
            LocalDateTime deadlineAt,
            TaskPriority priority
    ) {
        if (this.selectedProject == null) return;

        var task = taskService.createTask(
                ProjectId.fromString(this.selectedProject.getId()),
                title,
                content,
                deadlineAt,
                priority
        );
        TaskDTO dto = TaskDTOMapper.toDTO(task);
        TaskViewModel taskViewModel = this.bindHandlersToTask(new TaskViewModel(dto));
        this.tasks.add(taskViewModel);
    }

    private TaskViewModel bindHandlersToTask(TaskViewModel taskViewModel) {
        taskViewModel.setOnOpenDetails(() -> this.handleOnOpenTaskDetails(taskViewModel));
        taskViewModel.setOnSelected(() -> this.handleOnTaskSelected(taskViewModel));
        taskViewModel.setOnEdit(() -> this.handleOnEditTask(taskViewModel));
        taskViewModel.setOnDelete(() -> this.handleOnDeleteTask(taskViewModel));
        return taskViewModel;
    }

    private void handleOnOpenTaskDetails(TaskViewModel taskViewModel) {
        // TODO: dialog
    }

    private void handleOnTaskSelected(TaskViewModel taskViewModel) {}

    private void handleOnEditTask(TaskViewModel taskViewModel) {
        // TODO: dialog
    }

    private void handleOnDeleteTask(TaskViewModel taskViewModel) {
        taskService.deleteTask(TaskId.fromString(taskViewModel.getId()));
        tasks.remove(taskViewModel);
    }

    // Scripts

    public ObservableList<SimpleScriptViewModel> getScripts() { return this.scripts; }

    public SimpleScriptViewModel getSelectedScript() { return this.selectedScript; }

    public void setSelectedScript(SimpleScriptViewModel script) {
        this.selectedScript = script;
        this.scripts.forEach(s -> s.setSelected(s == script));
    }

    public void loadScriptsForSelectedProject() {
        scripts.clear();

        if (selectedProject == null) return;

        ProjectId projectId = ProjectId.fromString(selectedProject.getId());

        scriptService.getAllScripts(projectId).forEach(script -> {
            var commands = commandService.getAllCommands(script.getId());
            if (commands.isEmpty()) return;

            var command = commands.getFirst();

            scripts.add(
                    new SimpleScriptViewModel(
                            SimpleScriptDTOMapper.toDTO(script, command)
                    )
            );
        });
    }

    public void addScriptForSelectedProject(
            String title,
            String description,
            String workingDir,
            String executableCommand
    ) {
        if (selectedProject == null) return;

        ProjectId projectId = ProjectId.fromString(selectedProject.getId());

        var script = scriptService.createScript(projectId, title, description);

        var command = commandService.createSingleCommand(
                script.getId(),
                new WorkingDir(workingDir),
                ExecutableCommand.parse(executableCommand)
        );

        scripts.add(
                new SimpleScriptViewModel(
                        SimpleScriptDTOMapper.toDTO(script, command)
                )
        );
    }

    public void deleteSelectedScript() {
        if (selectedScript == null) return;

        ScriptId scriptId = ScriptId.fromString(selectedScript.getScriptId());
        CommandId commandId = CommandId.fromString(selectedScript.getCommandId());

        commandService.deleteCommand(commandId);
        scriptService.deleteScript(scriptId);

        scripts.remove(selectedScript);
        selectedScript = null;
    }

    public void runSelectedScript() {
        if (selectedScript == null) return;

        ScriptId scriptId = ScriptId.fromString(selectedScript.getScriptId());

        var commands = commandService.getAllCommands(scriptId);
        if (commands.isEmpty()) return;

        var command = commands.getFirst();

        commandExecutor.execute(
                Path.of(command.getWorkingDir().value()),
                command.getExecutableCommand().toString()
        );
    }

    public void stopSelectedScript() {

    }
}
