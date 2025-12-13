package ru.honsage.dev.gitpm.application.services;

import ru.honsage.dev.gitpm.domain.exceptions.ExceptionFactory;
import ru.honsage.dev.gitpm.domain.models.Project;
import ru.honsage.dev.gitpm.domain.ports.GitOperations;
import ru.honsage.dev.gitpm.domain.repositories.ProjectRepository;
import ru.honsage.dev.gitpm.domain.valueobjects.GitRemoteURL;
import ru.honsage.dev.gitpm.domain.valueobjects.LocalRepositoryPath;
import ru.honsage.dev.gitpm.domain.valueobjects.ProjectId;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectService {
    private final ProjectRepository projectRepository;
    private final GitOperations gitClient;

    public ProjectService(ProjectRepository repository, GitOperations gitClient) {
        this.projectRepository = repository;
        this.gitClient = gitClient;
    }

    public Project createProject(
            String title,
            String description,
            String localPath,
            String remoteURL
    ) {
        LocalRepositoryPath path = new LocalRepositoryPath(localPath);
        GitRemoteURL url = remoteURL == null ? null : new GitRemoteURL(remoteURL);

        if (!gitClient.isGitRepository(path.toPath())) {
            throw ExceptionFactory.businessRule(String.format("Directory '%s' must be a Git repository", localPath));
        }

        Project project = (title == null && description == null) ? new Project(ProjectId.random(), path, url) :
                new Project(ProjectId.random(), title, description, path, url, LocalDateTime.now());
        return projectRepository.save(project);
    }

    public Project getProject(ProjectId id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> ExceptionFactory.entityNotFound("Project", id.toString()));
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public List<Project> getProjectsByTitlePrefix(String titlePrefix) {
        return projectRepository.findByTitlePrefix(titlePrefix);
    }

    public List<Project> getProjectsWithRemote() {
        return projectRepository.findWithRemote();
    }

    public Project updateProject(
            ProjectId id,
            String newTitle,
            String newDescription,
            String newLocalPath,
            String newRemoteURL
    ) {
        Project project = getProject(id);

        LocalRepositoryPath newPath = new LocalRepositoryPath(newLocalPath);
        // Project with another local repository is another project
        if (!project.getLocalPath().equals(newPath)) {
            projectRepository.delete(id);
            return createProject(
                    newTitle,
                    newDescription,
                    newLocalPath,
                    newRemoteURL
            );
        }
        project.update(
                newTitle,
                newDescription,
                new GitRemoteURL(newRemoteURL)
        );

        return projectRepository.update(project);
    }

    public void deleteProject(ProjectId id) {
        projectRepository.delete(id);
    }

    public List<Project> scanForGitRepositories(Path rootDirectory) {
        List<Path> gitDirs = gitClient.findGitRepositories(rootDirectory);
        return gitDirs.stream()
                .filter(this::isLocalPathAvailable)
                .map(this::createProjectFromDirectory)
                .collect(Collectors.toList());
    }

    private Project createProjectFromDirectory(Path directory) {
        LocalRepositoryPath path = new LocalRepositoryPath(directory.toString());
        String remoteUrl = gitClient.getRemoteURL(directory);
        GitRemoteURL url = remoteUrl == null || remoteUrl.isBlank() ? null : new GitRemoteURL(remoteUrl);
        return this.createProject(null, null, path.value(), url == null? null : url.value());
    }

    private boolean isLocalPathAvailable(Path directory) {
        LocalRepositoryPath localPath = new LocalRepositoryPath(directory.toString());
        return projectRepository.findByLocalPath(localPath).isEmpty();
    }
}