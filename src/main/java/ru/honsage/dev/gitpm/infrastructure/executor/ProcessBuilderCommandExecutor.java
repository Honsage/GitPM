package ru.honsage.dev.gitpm.infrastructure.executor;

import ru.honsage.dev.gitpm.domain.ports.CommandExecutor;

import java.io.IOException;
import java.nio.file.Path;

public class ProcessBuilderCommandExecutor implements CommandExecutor {
    @Override
    public Process execute(Path workingDir, String command) {
        ProcessBuilder pb = new ProcessBuilder();
        pb.directory(workingDir.toFile());

        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            pb.command("cmd.exe", "/c", command);
        } else {
            pb.command("bash", "-c", command);
        }

        try {
            return pb.start();
        } catch (IOException e) {
            throw new RuntimeException("Error during command execution", e);
        }
    }
}
