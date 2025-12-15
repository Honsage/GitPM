package ru.honsage.dev.gitpm.domain.repositories;

import ru.honsage.dev.gitpm.domain.models.Script;
import ru.honsage.dev.gitpm.domain.valueobjects.ProjectId;
import ru.honsage.dev.gitpm.domain.valueobjects.ScriptId;

import java.util.List;
import java.util.Optional;

public interface ScriptRepository {
    Optional<Script> findById(ScriptId scriptId);
    List<Script> findByTitlePrefix(String titlePrefix, ProjectId id);
    List<Script> findAllByProject(ProjectId id);

    Script save(Script script, ProjectId id);
    Script update(Script script, ProjectId id);
    void delete(ScriptId scriptId);
}
