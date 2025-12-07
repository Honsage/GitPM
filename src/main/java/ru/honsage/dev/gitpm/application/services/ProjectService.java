package ru.honsage.dev.gitpm.application.services;

import ru.honsage.dev.gitpm.domain.exceptions.ExceptionFactory;
import ru.honsage.dev.gitpm.domain.models.Project;
import ru.honsage.dev.gitpm.domain.ports.GitOperations;
import ru.honsage.dev.gitpm.domain.repositories.ProjectRepository;
import ru.honsage.dev.gitpm.domain.valueobjects.GitRemoteURL;
import ru.honsage.dev.gitpm.domain.valueobjects.LocalRepositoryPath;

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
        GitRemoteURL url = new GitRemoteURL(remoteURL);

        if (!gitClient.isGitRepository(path.toPath())) {
            throw ExceptionFactory.businessRule(String.format("Directory '%s' must be a Git repository", localPath));
        }

        Project project = new Project(
                title,
                description,
                path,
                url,
                LocalDateTime.now()
        );
        return projectRepository.save(project);
    }

    public Project getProject(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> ExceptionFactory.entityNotFound("Project", id.toString()));
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Project updateProject(
            Long id,
            String newTitle,
            String newDescription,
            String newLocalPath,
            String newRemoteURL
    ) {
        Project project = getProject(id);

        // Project from another local repository is another project
        if (!project.getLocalPath().equals(new LocalRepositoryPath(newLocalPath))) {
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

        return projectRepository.save(project);
    }


    public void deleteProject(Long id) {
        projectRepository.delete(id);
    }

    public List<Project> scanForGitRepositories(Path rootDirectory) {
        List<Path> gitDirs = gitClient.findGitRepositories(rootDirectory);

        // TODO: add checking of already loaded repositories to prevent duplication

        return gitDirs.stream().map(this::createProjectFromDirectory).collect(Collectors.toList());
    }

    private Project createProjectFromDirectory(Path directory) {
        LocalRepositoryPath path = new LocalRepositoryPath(directory.toString());
        GitRemoteURL url = new GitRemoteURL(gitClient.getRemoteURL(directory));
        return new Project(path, url);
    }
}
