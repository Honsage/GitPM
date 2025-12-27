package ru.honsage.dev.gitpm.application.services;

import ru.honsage.dev.gitpm.domain.models.Script;
import ru.honsage.dev.gitpm.domain.ports.CommandExecutor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ScriptExecutionService {
    private final CommandExecutor executor;

    private final Map<String, Process> runningProcesses = new ConcurrentHashMap<>();

    public ScriptExecutionService(CommandExecutor commandExecutor) {
        this.executor = commandExecutor;
    }

    public void runScript(Script script) {
        String scriptId = script.getId().toString();
        if (runningProcesses.containsKey(scriptId)) {
            throw new IllegalStateException("Script is already running");
        }

        Process process = executor.execute(
                script.getWorkingDir().toPath(),
                script.getCommand().toString()
        );

        runningProcesses.put(scriptId, process);

        process.onExit().thenRun(() -> runningProcesses.remove(scriptId));
    }

    public void stopScript(String scriptId) {
        Process process = runningProcesses.get(scriptId);
        if (process == null) return;

        process.destroy();

        runningProcesses.remove(scriptId);
    }

    public boolean isRunning(String scriptId) {
        return runningProcesses.containsKey(scriptId);
    }
}
