package ru.honsage.dev.gitpm.application.services;

import ru.honsage.dev.gitpm.domain.exceptions.ExceptionFactory;
import ru.honsage.dev.gitpm.domain.models.Script;
import ru.honsage.dev.gitpm.domain.repositories.ScriptRepository;
import ru.honsage.dev.gitpm.domain.valueobjects.Command;
import ru.honsage.dev.gitpm.domain.valueobjects.ProjectId;
import ru.honsage.dev.gitpm.domain.valueobjects.ScriptId;
import ru.honsage.dev.gitpm.domain.valueobjects.WorkingDir;

import java.util.List;

public class ScriptService {
    private final ScriptRepository scriptRepository;

    public ScriptService(ScriptRepository scriptRepository) {
        this.scriptRepository = scriptRepository;
    }

    public Script createScript(
            ProjectId projectId,
            String title,
            String description,
            String workingDir,
            String command
    ) {
        WorkingDir dir = new WorkingDir(workingDir);
        Command cmd = new Command(command);

        Script script = new Script(
                ScriptId.random(),
                title,
                description,
                dir,
                cmd
        );
        return scriptRepository.save(script, projectId);
    }

    public Script getScript(ScriptId scriptId) {
        return scriptRepository.findById(scriptId)
                .orElseThrow(() -> ExceptionFactory.entityNotFound("Script", scriptId.toString()));
    }

    public List<Script> getAllScripts(ProjectId projectId) {
        return scriptRepository.findAllByProject(projectId);
    }

    public List<Script> getScriptsByTitlePrefix(ProjectId projectId, String titlePrefix) {
        return scriptRepository.findByTitlePrefix(titlePrefix, projectId);
    }

    public Script updateScript(
            ProjectId projectId,
            ScriptId scriptId,
            String newTitle,
            String newDescription,
            String newWorkingDir,
            String newCommand
    ) {
        Script script = getScript(scriptId);
        WorkingDir workingDir = new WorkingDir(newWorkingDir);
        Command command = new Command(newCommand);

        script.update(
                newTitle,
                newDescription,
                workingDir,
                command
        );

        return scriptRepository.update(script, projectId);
    }

    public void deleteScript(ScriptId scriptId) {
        scriptRepository.delete(scriptId);
    }
}
