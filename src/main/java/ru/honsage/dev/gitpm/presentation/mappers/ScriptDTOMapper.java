package ru.honsage.dev.gitpm.presentation.mappers;

import ru.honsage.dev.gitpm.domain.models.Script;
import ru.honsage.dev.gitpm.presentation.dto.ScriptDTO;

public class ScriptDTOMapper {
    private ScriptDTOMapper() {}

    public static ScriptDTO toDTO(Script script) {
        return new ScriptDTO(
                script.getId().toString(),
                script.getTitle(),
                script.getDescription(),
                script.getWorkingDir().toString(),
                script.getCommand().toString()
        );
    }
}
