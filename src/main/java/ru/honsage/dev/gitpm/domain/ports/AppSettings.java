package ru.honsage.dev.gitpm.domain.ports;

public interface AppSettings {
    ShellType getShellType();
    BrowserType getBrowserType();
    double getSceneWidth();
    double getSceneHeight();
    void setShellType(ShellType shellType);
    void setBrowserType(BrowserType browserType);
    void setSceneWidth(double value);
    void setSceneHeight(double value);
}
