package ru.honsage.dev.gitpm.domain.models;

import ru.honsage.dev.gitpm.domain.exceptions.ExceptionFactory;
import ru.honsage.dev.gitpm.domain.valueobjects.GitRemoteURL;
import ru.honsage.dev.gitpm.domain.valueobjects.LocalRepositoryPath;

import java.time.LocalDateTime;

public class Project {
    private String title;
    private String description;
    private LocalRepositoryPath localPath;
    private GitRemoteURL remoteURL;
    private final LocalDateTime addedAt;

    public Project(
        String title,
        String description,
        LocalRepositoryPath localPath,
        GitRemoteURL remoteURL,
        LocalDateTime addedAt
    ) {
        validateTitle(title);
        validateAddedAt(addedAt);
        this.title = title;
        this.description = description;
        this.localPath = localPath;
        this.remoteURL = remoteURL;
        this.addedAt = addedAt;
    }

    public Project(
            LocalRepositoryPath localPath,
            GitRemoteURL remoteURL
    ) {
        this(
                localPath.getRepoName(),
                null,
                localPath,
                remoteURL,
                LocalDateTime.now()
        );
    }

    public Project(LocalRepositoryPath localPath) {
        this(localPath,null);
    }

    private void validateTitle(String title) {
        if (title == null) {
            throw ExceptionFactory.validation("Title of Project cannot be empty", "Project.title");
        }
    }

    private void validateAddedAt(LocalDateTime addedAt) {
        if (addedAt == null) {
            throw ExceptionFactory.validation("Addition datetime cannot be null", "Project.addedAt");
        }

        if (addedAt.isAfter(LocalDateTime.now())) {
            throw ExceptionFactory.validation(
                    "Addition datetime cannot be in the future",
                    "Project.addedAt"
            );
        }
    }

    public void update(
            String newTitle,
            String newDescription,
            GitRemoteURL newRemoteURL
    ) {
        validateTitle(newTitle);
        this.title = newTitle;
        this.description = newDescription;
        this.remoteURL = newRemoteURL;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public LocalRepositoryPath getLocalPath() {
        return this.localPath;
    }

    public GitRemoteURL getRemoteURL() {
        return this.remoteURL;
    }

    public LocalDateTime getAddedAt() {
        return this.addedAt;
    }
}
