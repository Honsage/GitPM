package ru.honsage.dev.gitpm.application.services;

import ru.honsage.dev.gitpm.domain.exceptions.ExceptionFactory;
import ru.honsage.dev.gitpm.domain.models.Script;
import ru.honsage.dev.gitpm.domain.repositories.ScriptRepository;
import ru.honsage.dev.gitpm.domain.valueobjects.ProjectId;
import ru.honsage.dev.gitpm.domain.valueobjects.ScriptId;

import java.util.List;

public class ScriptService {
    private final ScriptRepository scriptRepository;

    public ScriptService(ScriptRepository scriptRepository) {
        this.scriptRepository = scriptRepository;
    }

    public Script createScript(
            ProjectId projectId,
            String title,
            String description
    ) {
        Script script = new Script(
                ScriptId.random(),
                title,
                description
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
            String newDescription
    ) {
        Script script = getScript(scriptId);

        script.update(
                newTitle,
                newDescription
        );

        return scriptRepository.update(script, projectId);
    }

    public void deleteScript(ScriptId scriptId) {
        scriptRepository.delete(scriptId);
    }
}
