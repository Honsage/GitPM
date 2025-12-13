package ru.honsage.dev.gitpm.presentation.viewmodels;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import ru.honsage.dev.gitpm.application.services.ProjectService;
import ru.honsage.dev.gitpm.application.services.TaskService;
import ru.honsage.dev.gitpm.domain.models.TaskPriority;
import ru.honsage.dev.gitpm.domain.valueobjects.ProjectId;
import ru.honsage.dev.gitpm.presentation.dto.ProjectDTO;
import ru.honsage.dev.gitpm.presentation.dto.TaskDTO;
import ru.honsage.dev.gitpm.presentation.mappers.ProjectDTOMapper;
import ru.honsage.dev.gitpm.presentation.mappers.TaskDTOMapper;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.stream.IntStream;

// Логика представления
public class MainViewModel {
    private final ProjectService projectService;
    private final TaskService taskService;

    private final ObservableList<ProjectViewModel> projects = FXCollections.observableArrayList();
    private final FilteredList<ProjectViewModel> filteredProjects = new FilteredList<>(projects);

    private final ObservableList<TaskViewModel> tasks = FXCollections.observableArrayList();

    private ProjectViewModel selectedProject;

    public MainViewModel(ProjectService projectService, TaskService taskService) {
        this.projectService = projectService;
        this.taskService = taskService;
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
                .forEach(tasks::add);
    }

    // TODO: fix add with dialog
    public void addTaskForSelectedProject(String title) {
        if (this.selectedProject == null) return;

        var task = taskService.createTask(
                ProjectId.fromString(this.selectedProject.getId()),
                title,
                "content",
                LocalDateTime.now().plusDays(20),
                TaskPriority.LOW
        );
        TaskDTO dto = TaskDTOMapper.toDTO(task);
        this.tasks.add(new TaskViewModel(dto));
    }
}
