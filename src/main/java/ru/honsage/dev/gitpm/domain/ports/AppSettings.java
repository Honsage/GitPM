package ru.honsage.dev.gitpm.domain.ports;

public interface AppSettings {
    ShellType getShellType();
    BrowserType getBrowserType();
    void setShellType(ShellType shellType);
    void setBrowserType(BrowserType browserType);
}
