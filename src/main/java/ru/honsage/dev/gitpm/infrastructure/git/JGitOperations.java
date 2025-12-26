package ru.honsage.dev.gitpm.infrastructure.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import ru.honsage.dev.gitpm.domain.ports.GitOperations;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class JGitOperations implements GitOperations {
    public JGitOperations() {}

    @Override
    public boolean isGitRepository(Path directory) {
        return Files.exists(directory.resolve(".git"));
    }

    @Override
    public List<Path> findGitRepositories(Path rootDirectory) {
        List<Path> found = new ArrayList<>();

        try {
            Files.walkFileTree(rootDirectory,
                    EnumSet.noneOf(FileVisitOption.class),
                    3,
                    new SimpleFileVisitor<>() {
                        @Override
                        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                            try {
                                if (!Files.isReadable(dir)) {
                                    return FileVisitResult.SKIP_SUBTREE;
                                }
                            } catch (SecurityException e) {
                                return FileVisitResult.SKIP_SUBTREE;
                            }

                            Path gitDir = dir.resolve(".git");
                            try {
                                if (Files.isDirectory(gitDir)) {
                                    found.add(dir);
                                    return FileVisitResult.SKIP_SUBTREE;
                                }
                            } catch (SecurityException _) {}
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult visitFileFailed(Path file, IOException exc) {
                            return FileVisitResult.SKIP_SUBTREE;
                        }
                    });
        } catch (IOException e) {
            System.err.println("Error walking directory tree: " + e.getMessage());
        }

        return found;
    }

    @Override
    public void cloneRepository(String url, Path directory) {
        try {
            Git.cloneRepository()
                    .setURI(url)
                    .setDirectory(directory.toFile())
                    .call();
        } catch (GitAPIException e) {
            throw new RuntimeException("Failed to clone repository: " + url, e);
        }
    }

    @Override
    public String getRemoteURL(Path repository) {
        try {
            var repo = new FileRepositoryBuilder()
                    .setGitDir(repository.resolve(".git").toFile())
                    .build();

            StoredConfig config = repo.getConfig();
            String remote = config.getString("remote", "origin", "url");
            return this.convertRemoteToURL(remote);

        } catch (IOException e) {
            return null;
        }
    }

    private String convertRemoteToURL(String remote) {
        if (remote == null || remote.isBlank()) return remote;
        if (remote.startsWith("http://") || remote.startsWith("https://")) {
            return remote.replaceAll("\\.git$", "");
        }
        var sshPattern = Pattern.compile(
                "^git@([^:]+):(.+?)(?:\\.git)?$"
        );

        var m = sshPattern.matcher(remote);
        if (m.matches()) {
            String domain = m.group(1);
            String path = m.group(2);
            return "https://" + domain + "/" + path;
        }

        if (remote.startsWith("git://")) {
            return remote
                    .replaceFirst("^git://", "https://")
                    .replaceAll("\\.git$", "");
        }

        return remote.replaceAll("\\.git$", "");
    }
}
