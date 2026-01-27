package ru.honsage.dev.gitpm.application.services;

import ru.honsage.dev.gitpm.domain.models.Script;
import ru.honsage.dev.gitpm.domain.ports.AppSettings;
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
    private AppSettings settings;

    private final Map<String, Process> runningProcesses = new ConcurrentHashMap<>();
    private final Map<String, StringBuilder> scriptOutputs = new ConcurrentHashMap<>();

    public ScriptExecutionService(CommandExecutor commandExecutor, AppSettings settings) {
        this.executor = commandExecutor;
        this.settings = settings;
        this.selectedShellType = settings.getShellType();
    }

    public void runScript(Script script) {
        String scriptId = script.getId().toString();
        if (runningProcesses.containsKey(scriptId)) {
            throw new IllegalStateException("Script is already running");
        }

        scriptOutputs.put(scriptId, new StringBuilder());

        Process process = executor.execute(
                script.getWorkingDir().toPath(),
                script.getCommand().toString(),
                this.selectedShellType
        );

        runningProcesses.put(scriptId, process);
        new Thread(() -> readOutput(scriptId, process)).start();
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

    private void readOutput(String scriptId, Process process) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {

            String line;
            while ((line = reader.readLine()) != null) {
                StringBuilder output = scriptOutputs.get(scriptId);
                if (output != null) {
                    output.append(line).append("\n");
                }
            }

        } catch (IOException e) {
            System.err.println("Error reading output: " + e.getMessage());
        }
    }

    public String getOutputFromScript(String scriptId) {
        StringBuilder output = scriptOutputs.get(scriptId);
        return output != null ? output.toString() : "";
    }

    public void clearOutput(String scriptId) {
        scriptOutputs.remove(scriptId);
    }

    public void setShellType(ShellType shellType) {
        this.selectedShellType = shellType;
        this.settings.setShellType(shellType);
    }

    public ShellType getShellType() {
        return selectedShellType;
    }
}
