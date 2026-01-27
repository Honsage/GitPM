package ru.honsage.dev.gitpm.domain.ports;

public interface AppSettings {
    ShellType getShellType();
    BrowserType getBrowserType();
    double getWindowWidth();
    double getWindowHeight();
    void setShellType(ShellType shellType);
    void setBrowserType(BrowserType browserType);
    void setWindowWidth(double value);
    void setWindowHeight(double value);
}
