package ru.honsage.dev.gitpm.domain.ports;

public interface AppSettings {
    ShellType getShellType();
    void setShellType(ShellType shellType);
}
