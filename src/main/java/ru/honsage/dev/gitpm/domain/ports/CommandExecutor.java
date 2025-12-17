package ru.honsage.dev.gitpm.domain.ports;

import java.nio.file.Path;

public interface CommandExecutor {
    Process execute(Path workingDir, String command);
}
