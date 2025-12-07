package ru.honsage.dev.gitpm.domain.ports;

import java.nio.file.Path;
import java.util.List;

public interface GitOperations {
    boolean isGitRepository(Path directory);
    List<Path> findGitRepositories(Path rootDirectory);
    void cloneRepository(String url, Path directory);
    String getRemoteURL(Path repository);
}
