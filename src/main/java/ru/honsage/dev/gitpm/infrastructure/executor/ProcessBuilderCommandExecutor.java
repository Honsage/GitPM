package ru.honsage.dev.gitpm.infrastructure.executor;

import ru.honsage.dev.gitpm.domain.ports.CommandExecutor;
import ru.honsage.dev.gitpm.domain.ports.ShellType;

import java.io.IOException;
import java.nio.file.Path;

public class ProcessBuilderCommandExecutor implements CommandExecutor {
    @Override
    public Process execute(Path workingDir, String command, ShellType shellType) {
        ProcessBuilder pb = new ProcessBuilder();
        pb.directory(workingDir.toFile());

        switch (shellType) {
            case CMD -> pb.command("cmd.exe", "/c", command);
            case POWERSHELL -> pb.command("powershell.exe", "-Command", command);
            case GIT_BASH -> pb.command("git-bash.exe", "-c", command);
            case WSL_BASH -> pb.command("wsl.exe", "bash", "-c", command);
            case BASH -> pb.command("bash", "-c", command);
        }

        try {
            return pb.start();
        } catch (IOException e) {
            throw new RuntimeException("Error during command execution", e);
        }
    }
}
