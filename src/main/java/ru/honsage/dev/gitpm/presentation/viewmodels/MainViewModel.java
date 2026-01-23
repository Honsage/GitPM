package ru.honsage.dev.gitpm.presentation.viewmodels;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import ru.honsage.dev.gitpm.application.services.*;
import ru.honsage.dev.gitpm.domain.models.Script;
import ru.honsage.dev.gitpm.domain.models.TaskPriority;
import ru.honsage.dev.gitpm.domain.ports.ShellType;
import ru.honsage.dev.gitpm.domain.valueobjects.*;
import ru.honsage.dev.gitpm.presentation.dto.ProjectDTO;
import ru.honsage.dev.gitpm.presentation.dto.ScriptDTO;
import ru.honsage.dev.gitpm.presentation.dto.TaskDTO;
import ru.honsage.dev.gitpm.presentation.mappers.ProjectDTOMapper;
import ru.honsage.dev.gitpm.presentation.mappers.ScriptDTOMapper;
import ru.honsage.dev.gitpm.presentation.mappers.TaskDTOMapper;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.stream.IntStream;

// Логика представления
public class MainViewModel {
    private final ProjectService projectService;
    private final TaskService taskService;
    private final ScriptService scriptService;
    private final ScriptExecutionService scriptExecutionService;

    private final ObservableList<ProjectViewModel> projects = FXCollections.observableArrayList();
    private final FilteredList<ProjectViewModel> filteredProjects = new FilteredList<>(projects);

    private final ObservableList<TaskViewModel> tasks = FXCollections.observableArrayList();

    private final ObservableList<ScriptViewModel> scripts = FXCollections.observableArrayList();

    private ProjectViewModel selectedProject;
    private ScriptViewModel selectedScript;

    public MainViewModel(
            ProjectService projectService,
            TaskService taskService,
            ScriptService scriptService,
            ScriptExecutionService scriptExecutionService
    ) {
        this.projectService = projectService;
        this.taskService = taskService;
        this.scriptService = scriptService;
        this.scriptExecutionService = scriptExecutionService;
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
        sortTasks();
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

    public void updateTaskForSelectedProject(TaskViewModel taskViewModel) {
        if (selectedProject == null) return;
        taskService.updateTask(
                ProjectId.fromString(selectedProject.getId()),
                TaskId.fromString(taskViewModel.getId()),
                taskViewModel.getTitle(),
                taskViewModel.getContent(),
                taskViewModel.isCompleted(),
                taskViewModel.getDeadlineAt() != null ?
                        LocalDateTime.parse(taskViewModel.getDeadlineAt()) : null,
                taskViewModel.getPriority()
        );
    }

    private void sortTasks() {
        FXCollections.sort(tasks, Comparator
                .comparing(TaskViewModel::isCompleted)
                .thenComparing(t -> t.getPriority().getValue(), Comparator.reverseOrder())
                .thenComparing(TaskViewModel::getDeadlineAt,
                        Comparator.nullsLast(Comparator.naturalOrder()))
        );
    }

    private TaskViewModel bindHandlersToTask(TaskViewModel taskViewModel) {
        taskViewModel.completedProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != oldValue) {
                updateTaskForSelectedProject(taskViewModel);
                sortTasks();
            }
        });

        taskViewModel.priorityProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != oldValue) {
                updateTaskForSelectedProject(taskViewModel);
                sortTasks();
            }
        });
        return taskViewModel;
    }

    public void deleteTask(TaskViewModel taskViewModel) {
        taskService.deleteTask(TaskId.fromString(taskViewModel.getId()));
        tasks.remove(taskViewModel);
    }

    // Scripts

    public ObservableList<ScriptViewModel> getScripts() {
        return this.scripts;
    }

    public ScriptViewModel getSelectedScript() {
        return this.selectedScript;
    }

    public void setSelectedScript(ScriptViewModel script) {
        this.selectedScript = script;
        this.scripts.forEach(s -> s.setSelected(s == script));
    }

    public void loadScriptsForSelectedProject() {
        scripts.clear();

        if (selectedProject == null) return;

        ProjectId projectId = ProjectId.fromString(selectedProject.getId());

        scriptService.getAllScripts(projectId).stream()
                .map(ScriptDTOMapper::toDTO)
                .map(ScriptViewModel::new)
                .forEach(scripts::add);
    }

    public void addScriptForSelectedProject(
            String title,
            String description,
            String workingDir,
            String command
    ) {
        if (selectedProject == null) return;

        ProjectId projectId = ProjectId.fromString(selectedProject.getId());

        var script = scriptService.createScript(
                projectId,
                title,
                description,
                workingDir,
                command
        );

        scripts.add(
                new ScriptViewModel(
                        ScriptDTOMapper.toDTO(script)
                )
        );
    }

    public void updateSelectedScript(
            String title,
            String description,
            String workingDir,
            String command
    ) {
        if (selectedProject == null || selectedScript == null) return;

        ProjectId projectId = ProjectId.fromString(selectedProject.getId());
        ScriptId scriptId = ScriptId.fromString(selectedScript.getScriptId());

        var updatedScript = scriptService.updateScript(
                projectId,
                scriptId,
                title,
                description,
                workingDir,
                command
        );

        this.updateScriptsInList(ScriptDTOMapper.toDTO(updatedScript));
    }

    private void updateScriptsInList(ScriptDTO dto) {
        IntStream.range(0, scripts.size())
                .filter(i -> scripts.get(i).getScriptId().equals(dto.scriptId()))
                .findFirst()
                .ifPresent(i -> scripts.set(i, new ScriptViewModel(dto)));
    }

    public void deleteSelectedScript() {
        if (selectedScript == null) return;

        ScriptId scriptId = ScriptId.fromString(selectedScript.getScriptId());

        scriptService.deleteScript(scriptId);

        scripts.remove(selectedScript);
    }

    public void runSelectedScript() {
        if (selectedScript == null) return;

        scriptExecutionService.runScript(
                scriptToDomain(selectedScript)
        );

        selectedScript.setRunning(true);
    }

    // TODO: move this method to mapper
    private Script scriptToDomain(ScriptViewModel scriptViewModel) {
        ScriptId id = ScriptId.fromString(scriptViewModel.getScriptId());
        WorkingDir workingDir = new WorkingDir(scriptViewModel.getWorkingDir());
        Command command = new Command(scriptViewModel.getCommand());
        return new Script(
                id,
                scriptViewModel.getTitle(),
                scriptViewModel.getDescription(),
                workingDir,
                command
        );
    }

    public void stopSelectedScript() {
        if (selectedScript == null) return;

        scriptExecutionService.stopScript(selectedScript.getScriptId());
        selectedScript.setRunning(false);
    }

    public void setSelectedShellType(String shellTypeName) {
        ShellType shellType = ShellType.fromString(shellTypeName);
        scriptExecutionService.setShellType(shellType);
    }

    public ShellType getSelectedShellType() {
        return scriptExecutionService.getShellType();
    }
}
