package ru.honsage.dev.gitpm.application.services;

import ru.honsage.dev.gitpm.domain.models.Script;
import ru.honsage.dev.gitpm.domain.ports.CommandExecutor;
import ru.honsage.dev.gitpm.domain.ports.ShellType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ScriptExecutionService {
    private final CommandExecutor executor;
    private ShellType selectedShellType;

    private final Map<String, Process> runningProcesses = new ConcurrentHashMap<>();

    public ScriptExecutionService(CommandExecutor commandExecutor) {
        this.executor = commandExecutor;
        this.selectedShellType = ShellType.CMD;
    }

    public void runScript(Script script) {
        String scriptId = script.getId().toString();
        if (runningProcesses.containsKey(scriptId)) {
            throw new IllegalStateException("Script is already running");
        }

        Process process = executor.execute(
                script.getWorkingDir().toPath(),
                script.getCommand().toString(),
                this.selectedShellType
        );
        printOutputToConsole(process);

        runningProcesses.put(scriptId, process);

        process.onExit().thenRun(() -> runningProcesses.remove(scriptId));
    }

    public void stopScript(String scriptId) {
        Process process = runningProcesses.get(scriptId);
        if (process == null) return;

        try {
            // TODO: handle if java < 9
            // Java 9+
            if (process.isAlive()) {
                process.descendants().forEach(ProcessHandle::destroyForcibly);
                process.destroyForcibly();
            }
        } catch (Exception _) {}

        runningProcesses.remove(scriptId);
    }

    public boolean isRunning(String scriptId) {
        return runningProcesses.containsKey(scriptId);
    }

    // TODO: remove console printing
    private void printOutputToConsole(Process process) {
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[OUTPUT]: " + line);
                }

            } catch (IOException e) {
                System.err.println("Error reading output: " + e.getMessage());
            }
        }).start();
    }

    public void setShellType(ShellType shellType) {
        this.selectedShellType = shellType;
    }
}
