package ru.honsage.dev.gitpm.domain.ports;

public enum ShellType {
    CMD,
    POWERSHELL,
    GIT_BASH,
    WSL_BASH,
    BASH;

    public static ShellType fromString(String shellTypeName) {
        return switch (shellTypeName) {
            case "CMD" -> CMD;
            case "POWERSHELL" -> POWERSHELL;
            case "GIT_BASH" -> GIT_BASH;
            case "WSL_BASH" -> WSL_BASH;
            case "BASH" -> BASH;
            default -> throw new IllegalArgumentException(
                    String.format("Invalid value: %s", shellTypeName)
            );
        };
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
