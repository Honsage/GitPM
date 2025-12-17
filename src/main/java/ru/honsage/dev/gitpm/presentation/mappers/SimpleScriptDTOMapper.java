package ru.honsage.dev.gitpm.presentation.mappers;

import ru.honsage.dev.gitpm.domain.models.Command;
import ru.honsage.dev.gitpm.domain.models.Script;
import ru.honsage.dev.gitpm.presentation.dto.SimpleScriptDTO;

public class SimpleScriptDTOMapper {
    private SimpleScriptDTOMapper() {}

    public static SimpleScriptDTO toDTO(Script script, Command command) {
        return new SimpleScriptDTO(
                script.getId().toString(),
                script.getTitle(),
                script.getDescription(),
                command.getId().toString(),
                command.getWorkingDir().toString(),
                command.getExecutableCommand().toString()
        );
    }
}
