package ru.honsage.dev.gitpm.domain.valueobjects;

import ru.honsage.dev.gitpm.domain.exceptions.ExceptionFactory;

import java.util.ArrayList;
import java.util.List;

public final class ExecutableCommand {
    private final String executable;
    private final List<String> args;

    public ExecutableCommand(String executable, List<String> args) {
        if (executable == null || executable.isBlank()) {
            throw ExceptionFactory.validation(
                    "Executable cannot be empty",
                    "ExecutableCommand"
            );
        }
        this.executable = executable;
        this.args = args;
    }

    public List<String> asProcessArgs() {
        List<String> result = new ArrayList<>();
        result.add(executable);
        result.addAll(args);
        return result;
    }

    public static ExecutableCommand parse(String commandText) {
        ArrayList<String> tokens = new ArrayList<>(List.of(commandText.split(" ")));
        String executable = tokens.getFirst();
        tokens.removeFirst();
        return new ExecutableCommand(executable, tokens);
    }

    @Override
    public String toString() {
        return executable + " " + String.join(" ", args);
    }
}
