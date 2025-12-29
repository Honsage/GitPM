package ru.honsage.dev.gitpm.domain.ports;

public enum ShellType {
    CMD,
    POWERSHELL,
    GIT_BASH,
    WSL_BASH,
    BASH;

    @Override
    public String toString() {
        return super.toString();
    }
}
